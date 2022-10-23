/*
 * Created on Nov 05, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.dac.datasync;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.mapping.Column;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.dictionary.LeafElement;
import com.bluejungle.framework.comp.IConfiguration;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncManager;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTask;
import com.nextlabs.destiny.container.shared.inquirymgr.LoggablePreparedStatement;

/**
 * Archives enrollment information.
 *
 * Debateably doesn't belong here, but the enrollment information is
 * closely tied to the activity logs, we want to archive the
 * enrollment information on a schedule used for the activity logs,
 * and we already have the archiving infrastructure here.
 *
 * DICT_ELEMENTS entries have an active_from/active_to date, but
 * that's not sufficient to determine if an entry can be removed,
 * because there is also an original_id. Sometimes a new sync will
 * create a new entry, with the original_id pointing to the older
 * entry.  In this case we can't delete the old entry (despite what
 * its active_to date my indicate)
 *
 * All original_ids point directly to the original (i.e. there isn't a
 * chain), so we don't have to worry about 3 pointing to 2 and 2
 * pointing to 1.
 *
 * A DICT_ELEMENTS entry can be deleted if it's over the age limit and
 * no entry in DICT_ELEMENTS under the age limit has its id as
 * original_id.
 * 
 * All other elements in other tables can be deleted if their id
 * references an entry in DICT_ELEMENTS under the age limit
 */
public class EnrollmentArchiver implements IDataSyncTask {
    private static final int DEFAULT_BATCH_SIZE = 5000;
    
    private static final String ARCHIVE_DICT_LEAF_ELEMENTS_TABLE = "ARCHIVE_DICT_LEAF_ELEMENTS";
    private static final String ARCHIVE_DICT_ELEMENTS_TABLE = "ARCHIVE_DICT_ELEMENTS";
    
    private static final Log log = LogFactory.getLog(EnrollmentArchiver.class.getName());
    
    private static final String SELECT_EXPIRED_DICT_ELEMENTS = "select id from DICT_ELEMENTS WHERE active_to < ?";

    private static final String SELECT_DISTINCT_ORIGINAL_ID_FROM_ACTIVE = "select distinct original_id from DICT_ELEMENTS where active_to >= ?";

    private static final String SELECT_EXPIRED_UNREFERENCED_DICT_ELEMENTS = SELECT_EXPIRED_DICT_ELEMENTS + " and id not in (" + SELECT_DISTINCT_ORIGINAL_ID_FROM_ACTIVE + ")";
    
    private static final String COUNT_EXPIRED_UNREFERENCED_DICT_ELEMENTS = "select count (*) FROM DICT_ELEMENTS where id in (" + SELECT_EXPIRED_UNREFERENCED_DICT_ELEMENTS + ")";

    private static final String INSERT_INTO_ARCHIVE_DICT_ELEMENTS = "insert into " + ARCHIVE_DICT_ELEMENTS_TABLE + " select * from DICT_ELEMENTS where id in (" + SELECT_EXPIRED_UNREFERENCED_DICT_ELEMENTS + ")";
    private static final String DELETE_FROM_DICT_ELEMENTS = "delete from DICT_ELEMENTS where id in (" + SELECT_EXPIRED_UNREFERENCED_DICT_ELEMENTS + ")";
    
    private static final String IN_EXPIRED_DICT_ELEMENTS = " in (" + SELECT_EXPIRED_DICT_ELEMENTS + ")";

    private static final String INSERT_INTO_ARCHIVE_DICT_LEAF_ELEMENTS = "insert into ARCHIVE_DICT_LEAF_ELEMENTS select * from DICT_LEAF_ELEMENTS where element_id " + IN_EXPIRED_DICT_ELEMENTS;
    private static final String DELETE_FROM_DICT_LEAF_ELEMENTS = "delete from DICT_LEAF_ELEMENTS  where element_id " + IN_EXPIRED_DICT_ELEMENTS;
    
    private static final String INSERT_INTO_ARCHIVE_DICT_ENUMERATED_GROUPS = "insert into ARCHIVE_DICT_ENUM_GROUPS select * from DICT_ENUM_GROUPS where element_id " + IN_EXPIRED_DICT_ELEMENTS;
    private static final String DELETE_FROM_DICT_ENUMERATED_GROUPS = "delete from DICT_ENUM_GROUPS where element_id " + IN_EXPIRED_DICT_ELEMENTS;

    private static final String INSERT_INTO_ARCHIVE_DICT_STRUCTURAL_GROUPS = "insert into ARCHIVE_DICT_STRUCT_GROUPS select * from DICT_STRUCT_GROUPS where element_id " + IN_EXPIRED_DICT_ELEMENTS;
    private static final String DELETE_FROM_DICT_STRUCTURAL_GROUPS = "delete from DICT_STRUCT_GROUPS where element_id " + IN_EXPIRED_DICT_ELEMENTS;

    // DICT_ENUM_MEMBERS and DICT_ENUM_GROUP_MEMBERS have their own "active_to" field, so we'll use that
    private static final String INSERT_INTO_ARCHIVE_DICT_ENUM_MEMBERS = "insert into ARCHIVE_DICT_ENUM_MEMBERS select * from DICT_ENUM_MEMBERS where active_to < ?";
    private static final String DELETE_FROM_DICT_ENUM_MEMBERS = "delete from DICT_ENUM_MEMBERS where active_to < ?";
    
    private static final String INSERT_INTO_ARCHIVE_DICT_ENUM_GROUP_MEMBERS = "insert into ARCHIVE_DICT_ENUM_GROUP_MEMBERS select * from DICT_ENUM_GROUP_MEMBERS where active_to < ?";
    private static final String DELETE_FROM_DICT_ENUM_GROUP_MEMBERS = "delete from DICT_ENUM_GROUP_MEMBERS where active_to < ?";
    
    private Connection connection;
    private Session session;
    private PreparedStatement insertDictLeafElementsStmt;
    private PreparedStatement deleteDictLeafElementsStmt;
    private PreparedStatement insertDictElementsStmt;
    private PreparedStatement deleteDictElementsStmt;
    private PreparedStatement insertDictEnumGroupsStmt;
    private PreparedStatement deleteDictEnumGroupsStmt;
    private PreparedStatement insertDictEnumGroupMembersStmt;
    private PreparedStatement deleteDictEnumGroupMembersStmt;
    private PreparedStatement insertDictEnumMembersStmt;
    private PreparedStatement deleteDictEnumMembersStmt;
    private PreparedStatement insertDictStructuralGroupsStmt;
    private PreparedStatement deleteDictStructuralGroupsStmt;
    private PreparedStatement expiredUnreferencedElementsCountStmt;

    public void run(Session session, long timeout, IConfiguration config) {
        try {
            init(session, timeout, config);
            log.info("Starting enrollment data archiving");
            
            long time = System.currentTimeMillis();
            
            archiveEnrollmentData();

            log.info("Finished enrollment data archiving in " + (System.currentTimeMillis() - time)/1000 + " seconds");
        } catch (HibernateException | SQLException e) {
            log.error("Severe error archving enrollment data", e);
        } finally {
            try {
                cleanup();
            } catch (SQLException e) {
                log.error("Severe error on archiving enrollment data cleanup", e);
            }
        }
        
    }
    
    public SyncType getType() {
        return IDataSyncTask.SyncType.ARCHIVE_ENROLLMENT;
    }

    private void init(Session session, long timeout, IConfiguration config) throws HibernateException{
        this.session = session;
        this.connection = session.connection();
        
        Dialect dialect = config.get(IDataSyncTask.DIALECT_CONFIG_PARAMETER);
        Integer cutOffDays = config.get(IDataSyncManager.ENROLLMENT_ARCHIVE_AGE_OF_EXPIRED_ENTRIES);
        if (cutOffDays != null) {
            setCutOffDays(cutOffDays);
        }
    }
    
    private void archiveEnrollmentData() throws HibernateException, SQLException {
        if (getConnection() == null) {
            throw new IllegalStateException("Enrollment archiver was not initialized correctly");
        }
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            
            long startTime = System.currentTimeMillis();

            // Copying/deleting dict_leaf_elements and other tables is simple
            log.info("Inserting into ARCHIVE_DICT_LEAF_ELEMENTS");
            executePreparedStatement(getInsertDictLeafElementsStatement());
            log.info("Deleting from DICT_LEAF_ELEMENTS");
            executePreparedStatement(getDeleteDictLeafElementsStatement());
            log.info("Inserting into ARCHIVE_DICT_ENUM_GROUPS");
            executePreparedStatement(getInsertEnumGroupsStatement());
            log.info("Deleting from DICT_ENUM_GROUPS");
            executePreparedStatement(getDeleteEnumGroupsStatement());
            log.info("Inserting into ARCHIVE_DICT_ENUM_MEMBERS");
            executePreparedStatement(getInsertEnumMembersStatement());
            log.info("Deleting from DICT_ENUM_MEMBERS");
            executePreparedStatement(getDeleteEnumMembersStatement());
            log.info("Inserting into ARCHIVE_DICT_ENUM_GROUP_MEMBERS");
            executePreparedStatement(getInsertEnumGroupMembersStatement());
            log.info("Deleting from DICT_ENUM_GROUP_MEMBERS");
            executePreparedStatement(getDeleteEnumGroupMembersStatement());
            log.info("Inserting into ARCHIVE_DICT_STRUCT_GROUPS");
            executePreparedStatement(getInsertStructuralGroupsStatement());
            log.info("Deleting from DICT_STRUCT_GROUPS");
            executePreparedStatement(getDeleteStructuralGroupsStatement());
            
            // DICT_ELEMENTS is more complex, so we break it up. Also, these prepared statements
            // have the "expired time" appearing twice.

            // Not sure why I have the count in here. Can probably delete in one go
            while (haveEntriesRemaining(getExpiredUnreferencedElementsCount(), 2)) {
                log.info("Inserting into ARCHIVE_DICT_ELEMENTS");
                executePreparedStatement(getInsertDictElementsStatement(), 2);
                log.info("Deleting from DICT_ELEMENTS");
                executePreparedStatement(getDeleteDictElementsStatement(), 2);
            }
            
            log.info("Completed enrollment archiving in " +
                     (System.currentTimeMillis() - startTime)/1000 + " seconds");
        } catch (HibernateException | SQLException e) {
            if (tx != null) {
                tx.rollback();
                tx = null;
                
            }
            throw e;
        } finally {
            if (tx != null) {
                tx.commit();
            }
        }
    }

    private boolean haveEntriesRemaining(PreparedStatement countStatement) throws SQLException {
        return haveEntriesRemaining(countStatement, 1);
    }
    
    private boolean haveEntriesRemaining(PreparedStatement countStatement, int numOldEntryTimestamps) throws SQLException {
        for (int i = 0; i < numOldEntryTimestamps; i++) {
            countStatement.setLong(i+1, getTimestampOldEntries());
        }
        
        ResultSet rs = countStatement.executeQuery();

        try {
            if (rs.next()) {
                return (rs.getInt(1) > 0);
            }
            
            return false;
        } catch (SQLException e) {
            log.error("SQLException getting expired count", e);
            return false;
        } finally {
            close(rs);
        }
    }

    private Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    private PreparedStatement getInsertDictLeafElementsStatement() throws SQLException {
        if (insertDictLeafElementsStmt == null) {
            insertDictLeafElementsStmt = new LoggablePreparedStatement(getConnection(), INSERT_INTO_ARCHIVE_DICT_LEAF_ELEMENTS);
        }
        
        return insertDictLeafElementsStmt;
    }

    private PreparedStatement getDeleteDictLeafElementsStatement() throws SQLException {
        if (deleteDictLeafElementsStmt == null) {
            deleteDictLeafElementsStmt = new LoggablePreparedStatement(getConnection(), DELETE_FROM_DICT_LEAF_ELEMENTS);
        }

        return deleteDictLeafElementsStmt;
    }
    
    private PreparedStatement getInsertDictElementsStatement() throws SQLException {
        if (insertDictElementsStmt == null) {
            insertDictElementsStmt = new LoggablePreparedStatement(getConnection(), INSERT_INTO_ARCHIVE_DICT_ELEMENTS);
        }
        
        return insertDictElementsStmt;
    }
    
    private PreparedStatement getDeleteDictElementsStatement() throws SQLException {
        if (deleteDictElementsStmt == null) {
            deleteDictElementsStmt = new LoggablePreparedStatement(getConnection(), DELETE_FROM_DICT_ELEMENTS);
        }
        
        return deleteDictElementsStmt;
    }
    

    private PreparedStatement getInsertEnumGroupMembersStatement() throws SQLException {
        if (insertDictEnumGroupMembersStmt == null) {
            insertDictEnumGroupMembersStmt = new LoggablePreparedStatement(getConnection(), INSERT_INTO_ARCHIVE_DICT_ENUM_GROUP_MEMBERS);
        }

        return insertDictEnumGroupMembersStmt;
    }
    
    private PreparedStatement getDeleteEnumGroupMembersStatement() throws SQLException {
        if (deleteDictEnumGroupMembersStmt == null) {
            deleteDictEnumGroupMembersStmt = new LoggablePreparedStatement(getConnection(), DELETE_FROM_DICT_ENUM_GROUP_MEMBERS);
        }

        return deleteDictEnumGroupMembersStmt;
    }

    private PreparedStatement getInsertStructuralGroupsStatement() throws SQLException {
        if (insertDictStructuralGroupsStmt == null) {
            insertDictStructuralGroupsStmt = new LoggablePreparedStatement(getConnection(), INSERT_INTO_ARCHIVE_DICT_STRUCTURAL_GROUPS);
        }

        return insertDictStructuralGroupsStmt;
    }

    private PreparedStatement getDeleteStructuralGroupsStatement() throws SQLException {
        if (deleteDictStructuralGroupsStmt == null) {
            deleteDictStructuralGroupsStmt = new LoggablePreparedStatement(getConnection(), DELETE_FROM_DICT_STRUCTURAL_GROUPS);
        }

        return deleteDictStructuralGroupsStmt;
    }
    
    private PreparedStatement getInsertEnumMembersStatement() throws SQLException {
        if (insertDictEnumMembersStmt == null) {
            insertDictEnumMembersStmt = new LoggablePreparedStatement(getConnection(), INSERT_INTO_ARCHIVE_DICT_ENUM_MEMBERS);
        }

        return insertDictEnumMembersStmt;
    }
    
    private PreparedStatement getDeleteEnumMembersStatement() throws SQLException {
        if (deleteDictEnumMembersStmt == null) {
            deleteDictEnumMembersStmt = new LoggablePreparedStatement(getConnection(), DELETE_FROM_DICT_ENUM_MEMBERS);
        }

        return deleteDictEnumMembersStmt;
    }
    
    private PreparedStatement getInsertEnumGroupsStatement() throws SQLException {
        if (insertDictEnumGroupsStmt == null) {
            insertDictEnumGroupsStmt = new LoggablePreparedStatement(getConnection(), INSERT_INTO_ARCHIVE_DICT_ENUMERATED_GROUPS);
        }

        return insertDictEnumGroupsStmt;
    }
    
    private PreparedStatement getDeleteEnumGroupsStatement() throws SQLException {
        if (deleteDictEnumGroupsStmt == null) {
            deleteDictEnumGroupsStmt = new LoggablePreparedStatement(getConnection(), DELETE_FROM_DICT_ENUMERATED_GROUPS);
        }

        return deleteDictEnumGroupsStmt;
    }

    private PreparedStatement getExpiredUnreferencedElementsCount() throws SQLException {
        if (expiredUnreferencedElementsCountStmt == null) {
            expiredUnreferencedElementsCountStmt = new LoggablePreparedStatement(getConnection(), COUNT_EXPIRED_UNREFERENCED_DICT_ELEMENTS);
        }

        return expiredUnreferencedElementsCountStmt;
    }
    
    private void cleanup() throws SQLException {
        closeStatement(insertDictLeafElementsStmt);
        insertDictLeafElementsStmt = null;
        closeStatement(deleteDictLeafElementsStmt);
        deleteDictLeafElementsStmt = null;
        closeStatement(insertDictElementsStmt);
        insertDictElementsStmt = null;
        closeStatement(deleteDictElementsStmt);
        deleteDictElementsStmt = null;
        closeStatement(insertDictEnumGroupsStmt);
        insertDictEnumGroupsStmt = null;
        closeStatement(deleteDictEnumGroupsStmt);
        deleteDictEnumGroupsStmt = null;
        closeStatement(insertDictEnumGroupMembersStmt);
        insertDictEnumGroupMembersStmt = null;
        closeStatement(deleteDictEnumGroupMembersStmt);
        deleteDictEnumGroupMembersStmt = null;
        closeStatement(insertDictEnumMembersStmt);
        insertDictEnumMembersStmt = null;
        closeStatement(deleteDictEnumMembersStmt);
        deleteDictEnumMembersStmt = null;
        closeStatement(insertDictStructuralGroupsStmt);
        insertDictStructuralGroupsStmt = null;
        closeStatement(deleteDictStructuralGroupsStmt);
        deleteDictStructuralGroupsStmt = null;
        
        closeStatement(expiredUnreferencedElementsCountStmt);
        expiredUnreferencedElementsCountStmt = null;
    }

    private void closeStatement(PreparedStatement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("Failed to close ResultSet", e);
            }
        }
    }

    /**
     * Execute the prepared statement with just one "timestamp"
     */
    private void executePreparedStatement(PreparedStatement stmt) throws SQLException {
        executePreparedStatement(stmt, 1);
    }

    /**
     * Execute the prepared statement with the appropriate number of "old entry" timestamps
     *
     * @param stmt
     * @param numOldEntryTimestamps
     * @throws Exception
     */
    private void executePreparedStatement(PreparedStatement stmt, int numOldEntryTimestamps) throws SQLException {
        for (int i = 0; i < numOldEntryTimestamps; i++) {
            // Remember that the first parameter is at index 1
            stmt.setLong(i+1, getTimestampOldEntries());
        }
        
        stmt.execute();
    }
    
    private Integer cutOffDays = 90;  // TODO

    private void setCutOffDays(Integer cutOffDays) {
        this.cutOffDays = cutOffDays;
    }

    private Integer getCutOffDays() {
        return cutOffDays;
    }
    
    // Anything older than this is old
    private Timestamp tsOldEntries;
    
    private Long getTimestampOldEntries() {
        if (tsOldEntries == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 24);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.HOUR, -(getCutOffDays() * 24));
            tsOldEntries = new Timestamp(cal.getTimeInMillis());
        }

        return tsOldEntries.getTime();
    }

    private static String dictLeafElementsColumnNames = "ELEMENT_ID,TYPE_ID," +
        "String00,String01,String02,String03,String04," +
        "String05,String06,String07,String08,String09," +
        "String10,String11,String12,String13,String14," +
        "String15,String16,String17,String18,String19," +
        "String20,String21,String22,String23,String24," +
        "String25,String26,String27,String28,String29," +
        "String30,String31,String32,String33,String34," +
        "String35,String36,String37,String38,String39," +
        "String40,String41,String42,String43,String44," +
        "String45,String46,String47,String48,String49," +
        "Number00,Number01,Number02,Number03,Number04," +
        "Number05,Number06,Number07,Number08,Number09," +
        "Number10,Number11,Number12,Number13,Number14," +
        "Number15,Number16,Number17,Number18,Number19," +
        "Date00,Date01,Date02,Date03,Date04," +
        "Date05,Date06,Date07,Date08,Date09," +
        "Date10,Date11,Date12,Date13,Date14," +
        "Date15,Date16,Date17,Date18,Date19," +
        "NUMARRAY00,NUMARRAY01,NUMARRAY02,NUMARRAY03";

    private synchronized String getDictLeafElementsColumnNames() throws HibernateException {
        return dictLeafElementsColumnNames;
    }
    
    /**
     * Given a class object that corresponds to a Hibernate mediated table in the db, get the
     * columns of the database.
     *
     * @param clazz the classname
     * @return a List of column names
     * @throws HibernateException if anything goes wrong
     * @note this function is not particularly fast. Callers might want to cache the result
     */
    private List<String> getColumnsFromClass(Class clazz) throws HibernateException {
        ArrayList<String> columnNames = new ArrayList<String>();
        Configuration cfg = new Configuration();
        cfg.addClass(clazz);
        
        Iterator i = cfg.getClassMapping(clazz).getTable().getColumnIterator();
        
        while (i.hasNext()) {
            Column col = (Column) i.next();
            columnNames.add(col.getName());
        }
        
        return columnNames;
    }
}
