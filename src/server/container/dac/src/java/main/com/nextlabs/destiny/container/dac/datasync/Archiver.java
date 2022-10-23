/*
 * All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
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

import net.sf.hibernate.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.SQLServerDialect;
import net.sf.hibernate.mapping.Column;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.utils.StringUtils;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncManager;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTask;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTaskUpdate;
import com.nextlabs.destiny.container.shared.inquirymgr.LoggablePreparedStatement;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.ReportActivityLogDO;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.ReportTrackingActivityLogDO;
import com.nextlabs.destiny.container.shared.inquirymgr.ReportDataHolderManager;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;

/**
 * Archives the Reporter Logs. Inserts into the archive tables and deletes
 * from the source tables in batches.
 *
 */
public class Archiver implements IDataSyncTask {
    
    private static final int DEFAULT_CUTOFF_DAYS_FROM_PRESENT = 90;
    
    private int cutOffDays = DEFAULT_CUTOFF_DAYS_FROM_PRESENT;
    
    private Log log = LogFactory.getLog(Archiver.class.getName());
    
    private static final int DEFAULT_BATCH_SIZE = 5000;
    
    private static final String SELECT_PA_LOG_COUNT_FOR_ARCHIVAL_SQL = 
        "select count(*) from " + Constants.REPORT_POLICY_ACTIVITY_LOG_TABLE +
        " where time < ? ";
    
    private static final String SELECT_TA_LOG_COUNT_FOR_ARCHIVAL_SQL = 
            "select count(*) from " + SharedLib.REPORT_TA_TABLE +
            " where time < ? ";
    
    private static final String INSERT_TA_SQL_FRAGMENT = 
            "insert into " + SharedLib.ARCHIVE_TA_TABLE + " "; 
    
    private static final String SELECT_PA_SQL_SUFFIX =
            "select * from " + Constants.REPORT_POLICY_ACTIVITY_LOG_TABLE +
            " where time < ? order by id";
    
    private static final String INSERT_TA_SQL_SUFFIX =
        "select * from " + SharedLib.REPORT_TA_TABLE +
        " where time < ? order by id";
    
    private static final String SELECT_TOP_N_FROM_REPORT_PA_LOG =
        "select id from " + Constants.REPORT_POLICY_ACTIVITY_LOG_TABLE + 
        " where time < ? order by id";
    
    private static final String SELECT_TOP_N_FROM_REPORT_TA_LOG =
            "select id from " + SharedLib.REPORT_TA_TABLE + 
            " where time < ? order by id";
    
    private static final String INSERT_PA_CUST_ATTR_SQL_FRAGMENT = 
        "insert into " + Constants.ARCHIVE_POLICY_CUSTOM_ATTR_TABLE + 
        "(policy_log_id, attr_id, attr_value)" +
        "select policy_log_id, attr_id, attr_value from " + 
        Constants.REPORT_POLICY_CUSTOM_ATTR_TABLE + " where policy_log_id in (";

    private static final String INSERT_TA_CUST_ATTR_SQL_FRAGMENT = 
        "insert into " + SharedLib.ARCHIVE_TA_CUST_ATTR_TABLE + 
        " select id, tracking_log_id, attribute_name, attribute_value from " + 
        SharedLib.REPORT_TA_CUST_ATTR_TABLE + " where tracking_log_id in (";
    
    private static final String INSERT_PA_OBL_SQL_FRAGMENT = 
        "insert into " + SharedLib.ARCHIVE_PA_OBLIGATION_TABLE + " " +
        "select * from " + SharedLib.REPORT_PA_OBLIGATION_TABLE + 
        " where ref_log_id in (";
    
    private static final String DELETE_PA_SQL_FRAGMENT = 
        "delete from " + Constants.REPORT_POLICY_ACTIVITY_LOG_TABLE + " where id in (";

    private static final String DELETE_TA_SQL_FRAGMENT = 
            "delete from " + SharedLib.REPORT_TA_TABLE + " where id in (";
    
    private static final String DELETE_PA_CUST_ATTR_SQL_FRAGMENT = 
        "delete from " + Constants.REPORT_POLICY_CUSTOM_ATTR_TABLE + 
        " where policy_log_id in (";
    
    private static final String DELETE_TA_CUST_ATTR_SQL_FRAGMENT = 
        "delete from " + SharedLib.REPORT_TA_CUST_ATTR_TABLE + 
        " where tracking_log_id in (";
    
    private static final String DELETE_PA_OBL_SQL_FRAGMENT = 
        "delete from " + SharedLib.REPORT_PA_OBLIGATION_TABLE + 
        " where ref_log_id in (";
    
    private static final String FIND_PA_ARCHIVE_COUNT_SQL_FRAGMENT = 
        "select count(*) from " + Constants.REPORT_POLICY_ACTIVITY_LOG_TABLE + 
        " where time < ?";
    
    private static final String FIND_TA_ARCHIVE_COUNT_SQL_FRAGMENT = 
        "select count(*) from " + SharedLib.REPORT_TA_TABLE + 
        " where time < ?";
    
    /**
     * Set of prepared statements for various operations of PA logs.
     */
    private PreparedStatement selectPALogCountForArchivalStmt;
    private PreparedStatement selectPAMinTimeStmt;
    private PreparedStatement insertPAStmt;
    private PreparedStatement insertPACustAttrStmt;
    private PreparedStatement insertPAOblStmt;
    private PreparedStatement deletePAStmt;
    private PreparedStatement deletePACustAttrStmt;
    private PreparedStatement deletePAOblStmt;
    private PreparedStatement findPaLogCountStmt;
    private PreparedStatement findTaLogCountStmt;
    
    /**
     * SQL that are used to create the PA PreparedStatements
     */
    private String insertPaSql;
    private String insertPaAttrSql;
    private String insertPaOblSql;
    private String deletePaSql;
    private String deletePaAttrSql;
    private String deletePaOblSql;
    
    /**
     * Set of prepared statements for various operations of TA logs.
     */
   
    private PreparedStatement selectTALogCountForArchivalStmt;
    private PreparedStatement selectNextTAMinTimeStmt;
    private PreparedStatement insertTAStmt;
    private PreparedStatement insertTACustAttrStmt;
    private PreparedStatement deleteTAStmt;
    private PreparedStatement deleteTACustAttrStmt;
    
    /**
     * SQL that are used to create the PA PreparedStatements
     */
    private String insertTaSql;
    private String insertTaAttrSql;
    private String deleteTaSql;
    private String deleteTaAttrSql;
    
    private Connection connection;
    private Session session;
    
    private Timestamp cutOffTimestamp;
    private long timeout; // not used at present
    private IConfiguration config;
    private Timestamp curLogTime;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private ReportDataHolderManager reportDataMgr;
    private IDataSyncTaskUpdate update;
    
    /**
     * This is set from the dialect - at present this is set to true
     * if the dialect is SQLServerDialect. The reason for this exposure to the
     * type of Dialect at this level is that the bindLimitParametersFirst
     * methods of the dialect does not return the correct result - it returns
     * false for SQLServer which is incorrect.
     */
    private boolean isSQLServer; 

    /**
     * This is used by the scheduler to run the archive operations at the
     * specified time. If the default constructor is used to create an instance
     * of this class, then this is the first point of entry i.e. no other 
     * methods should be called before this is called since it sets the 
     * initialization parameters.
     */
    public void run(Session session, long timeout, IConfiguration config) {
        long time = 0;
        try {
            init(session, timeout, config);
            if (log.isInfoEnabled()) {
              log.info("Starting archiving Policy Logs");
              time = System.currentTimeMillis();
            }
            
            archivePolicyLogs();
            
            if (log.isInfoEnabled()) {
                log.info("Completed archiving Policy Logs. Time taken: " +
                        (System.currentTimeMillis() - time)/1000 + " secs");
                log.info("Starting archiving Tracking Logs.");
                time = System.currentTimeMillis();
              }
            
        } catch (SQLException | HibernateException ex) {
            if (log.isErrorEnabled()) {
                log.error("Severe error on archiving task", ex);
            }
        } finally { 
            try {
                cleanup();
            } catch (SQLException ex) {
                if (log.isErrorEnabled()) {
                    log.error("Severe error on archiving task cleanup", ex);
                }
            }
        }
    }
    
   /**
    * Must be called before any operation.
    * 
    * @param session
    * @param timeout
    * @param config
    * @throws SQLException
    */
    private void init(Session session, long timeout, IConfiguration config)
        throws SQLException, HibernateException {
        setSession(session);
        setConnection(session.connection());
        setTimeout(timeout);
        setConfig(config);
        reportDataMgr = ComponentManagerFactory.getComponentManager().getComponent(
                ReportDataHolderManager.class);
        
        if (config != null) {
            Integer cutOffDays = config.get(IDataSyncManager.ARCHIVE_DAYS_OF_DATA_TO_KEEP);
            if (cutOffDays != null) {
                setCutOffDays(cutOffDays);
            }
        }       
         Dialect dialect = config.get(IDataSyncTask.DIALECT_CONFIG_PARAMETER);
         
         // needed to set the correct parameter for prepared statements
         isSQLServer = dialect instanceof SQLServerDialect; 
         
         initSqlWithLimit(dialect);
         
         update = config.get(IDataSyncTask.TASK_UPDATE_PARAMETER);
         
         getConnection().setAutoCommit(false);

         if (log.isDebugEnabled()) {
             log.debug("Archiver.init: insertPaSql: " + insertPaSql);
             log.debug("Archiver.init: insertPaAttrSql: " + insertPaAttrSql);
             log.debug("Archiver.init: insertPaOblSql: " + insertPaOblSql);
             log.debug("Archiver.init: insertTaSql: " + insertTaSql);
             log.debug("Archiver.init: insertTaAttrSql: " + insertTaAttrSql);
             log.debug("Archiver.init: deletePaSql: " + deletePaSql);
             log.debug("Archiver.init: deletePaAttrSql: " + deletePaAttrSql);
             log.debug("Archiver.init: deletePaOblSql: " + deletePaOblSql);
         }
    }
    
    private void initSqlWithLimit(Dialect dialect) throws HibernateException {
        int limitValue = getBatchSize();
          
        // Need to limit the queries by the batch size
        String selectInSqlForPa = "select id from (" + dialect.getLimitString(
                SELECT_TOP_N_FROM_REPORT_PA_LOG, false, limitValue) + ") patopn )";
        String selectInSqlForTa = "select id from (" +  dialect.getLimitString(
                SELECT_TOP_N_FROM_REPORT_TA_LOG, false, limitValue) + ") tatopn )";

        // RPA_LOG and APA_LOG must have the same columns in the same
        // order. At one point they got out of sync. A change fixed
        // that, and the upgrade process made it automatic, but the
        // column *order* was different (something that you normally
        // don't care about). The raw "insert into" broke, so we have
        // to specify column order.
        //
        // Note that we are inserting into APA_LOG, but using the
        // column names from RPA_LOG. This is correct. The data comes
        // from RPA_LOG, so the column order must come from there, and
        // if the column names differ for the two tables then we are
        // in trouble anyway.
        //
        // TODO - If we determine that the column order for RPA_LOG
        // and APA_LOG is the same (probably indicates a fresh install
        // rather than an upgrade) then we could skip this.  Not sure
        // if it's worth it
        //
        // There is an extra layer of select here. DB2+hibernate will
        // add an extra column to do limits and this is the only way I
        // can think of to remove the column
        insertPaSql = "insert into "
                      + Constants.ARCHIVE_POLICY_ACTIVITY_LOG_TABLE
                      + "(" + getPaColumnNames() + ")"
                      + " select " + getPaColumnNames() + " from ("
                      + dialect.getLimitString(SELECT_PA_SQL_SUFFIX, false, limitValue) + ") pacols";
        
        insertTaSql =  INSERT_TA_SQL_FRAGMENT + "select " + getTaColumnNames() + " from (" + dialect.getLimitString(INSERT_TA_SQL_SUFFIX, false, limitValue) + ") tacols ";
        
        insertPaAttrSql = INSERT_PA_CUST_ATTR_SQL_FRAGMENT + selectInSqlForPa;
        insertPaOblSql = INSERT_PA_OBL_SQL_FRAGMENT + selectInSqlForPa;
        
        insertTaAttrSql = INSERT_TA_CUST_ATTR_SQL_FRAGMENT + selectInSqlForTa;
        
        deletePaSql = DELETE_PA_SQL_FRAGMENT + selectInSqlForPa;

        deletePaAttrSql = DELETE_PA_CUST_ATTR_SQL_FRAGMENT + selectInSqlForPa;
        deletePaOblSql = DELETE_PA_OBL_SQL_FRAGMENT + selectInSqlForPa;
        
        deleteTaSql = DELETE_TA_SQL_FRAGMENT + selectInSqlForTa;
        deleteTaAttrSql = DELETE_TA_CUST_ATTR_SQL_FRAGMENT + selectInSqlForTa;
    }
    
    private void cleanup() throws SQLException {
        closeStatement(insertPAStmt);
        insertPAStmt = null;
        closeStatement(insertPACustAttrStmt);
        insertPACustAttrStmt = null;
        closeStatement(insertPAOblStmt);
        insertPAOblStmt = null;
        closeStatement(deletePAStmt);
        deletePAStmt = null;
        closeStatement(deletePACustAttrStmt);
        deletePACustAttrStmt = null;
        closeStatement(deletePAOblStmt);
        deletePAOblStmt = null;
        closeStatement(selectPALogCountForArchivalStmt);
        selectPALogCountForArchivalStmt = null;
        closeStatement(selectPAMinTimeStmt);
        selectPAMinTimeStmt = null;
        closeStatement(findPaLogCountStmt);
        findPaLogCountStmt = null;
        closeStatement(insertTAStmt);
        insertTAStmt = null;
        closeStatement(insertTACustAttrStmt);
        insertTACustAttrStmt = null;
        closeStatement(deleteTAStmt);
        deleteTAStmt = null;
        closeStatement(deleteTACustAttrStmt);
        deleteTACustAttrStmt = null;
        closeStatement(selectTALogCountForArchivalStmt);
        selectTALogCountForArchivalStmt = null;
        closeStatement(selectNextTAMinTimeStmt);
        selectNextTAMinTimeStmt = null;
        closeStatement(findTaLogCountStmt);
        findTaLogCountStmt = null;
        cutOffTimestamp = null;
        setSession(null); //we are closing session in the caller
        setConnection(null);
        
    }
    
    private void closeStatement(PreparedStatement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }
    
    public SyncType getType() {
        return IDataSyncTask.SyncType.ARCHIVE;
    }
    
    private void archivePolicyLogs() throws SQLException, HibernateException {
        update.reset();
        update.setPrefix("Archive Policy Logs");
        update.setTotalSize(getPALogsToArchiveCount());
        if (getConnection() == null) {
            throw new IllegalStateException(
                "The Archiver Object was not initialize properly.");
        }
        Transaction tx = null;
        long paRemainingLogCount = 0;
        long startTime = 0;
        try {
            paRemainingLogCount = getRemainingCountAndSetCurLogTime(
                 getSelectPALogCountForArchivalStmt(), getSelectPAMinTimeStmt());
            while (paRemainingLogCount > 0) {
                if (tx == null) {
                    tx = session.beginTransaction();
                }
                
                if (log.isDebugEnabled()) {
                    startTime = System.currentTimeMillis();
                }
                insertIntoPAArchiveTables();
                
                if (log.isDebugEnabled()) {
                    log.info("Completed inserting batch of Policy Logs. Time taken: " +
                            (System.currentTimeMillis() - startTime)/1000 + " secs");
                    startTime = System.currentTimeMillis();
                }
                
                deleteFromPASourceTables();
                
                if (log.isDebugEnabled()) {
                    log.info("Completed deleting batch of Policy Logs. Time taken: " +
                            (System.currentTimeMillis() - startTime)/1000 + " secs");
                    startTime = System.currentTimeMillis();
                }
                
                paRemainingLogCount = getRemainingCountAndSetCurLogTime(
                  getSelectPALogCountForArchivalStmt(), getSelectPAMinTimeStmt());
                
                if (log.isDebugEnabled()) {
                    log.info("Completed getFirstLogIdInRange call for Policy Logs. Time taken: " +
                            (System.currentTimeMillis() - startTime)/1000 + " secs");
                    startTime = System.currentTimeMillis();
                }
                
                if (tx != null) {
                    reportDataMgr.setMinPolicyActivityReportDate(this.curLogTime);
                    tx.commit();    
                    tx = null;
                }
                update.addSuccess(getBatchSize());
            }
        } catch (SQLException e) {
            if (log.isInfoEnabled()) {
                log.info("Error while attempting to archive activity logs.", e);
            }
            if (tx != null) tx.rollback();
            throw e;
        } 
    }
    
    private void archiveTrackingLogs() throws SQLException, HibernateException {
        update.reset();
        update.setPrefix("Archive Tracking Logs");
        update.setTotalSize(getTALogsToArchiveCount());
        if (getConnection() == null) {
            throw new IllegalStateException(
                "The Archiver Object was not initialized properly.");
        }
        Transaction tx = null;
        long taRemainingLogCount = 0;
        try {
            taRemainingLogCount = getRemainingCountAndSetCurLogTime(
              getSelectTALogCountForArchivalStmt(), getSelectTAMinTimeStmt());
            
            while (taRemainingLogCount > 0) {
                if (tx == null) {
                    tx = session.beginTransaction();
                }
                insertIntoTAArchiveTables();
                deleteFromTASourceTables();
                taRemainingLogCount = getRemainingCountAndSetCurLogTime(
                   getSelectTALogCountForArchivalStmt(), getSelectTAMinTimeStmt());
                if (tx != null) {
                    reportDataMgr.setMinTrackingActivityReportDate(curLogTime);
                    tx.commit();
                    tx = null;
                }
                update.addSuccess(getBatchSize());
            }
        } catch (SQLException e) {
            if (log.isInfoEnabled()) {
                log.info("Error while attempting to archive tracking logs.", e);
            }
            if (tx != null) tx.rollback();
            throw e;
        } 
    }
    
    private void insertIntoPAArchiveTables() throws SQLException {
        executePreparedStatement(getInsertPAStmt());
        executePreparedStatement(getInsertPACustAttrStmt());
        executePreparedStatement(getInsertPAOblStmt());
    }
    
    private void deleteFromPASourceTables() throws SQLException {
        executePreparedStatement(getDeletePACustAttrStmt());
        executePreparedStatement(getDeletePAOblStmt());
        executePreparedStatement(getDeletePAStmt());
    }
    
    private void insertIntoTAArchiveTables() throws SQLException {
        executePreparedStatement(getInsertTAStmt());
        executePreparedStatement(getInsertTACustAttrStmt());
    }
    
    private void deleteFromTASourceTables() throws SQLException {
        executePreparedStatement(getDeleteTACustAttrStmt());
        executePreparedStatement(getDeleteTAStmt());
    }

    /**
     *  The timestamp index is always 1 since for SQL server the limit is 
     *  already set at this point and for non-SQL server, the limit index is 2
     * @param stmt
     * @throws SQLException
     */
    private void executePreparedStatement(PreparedStatement stmt) throws SQLException {
        try {
            stmt.setTimestamp(1, getCutOffTimestamp());
            
            if (!isSQLServer) {
                stmt.setInt(2, getBatchSize());
            }
            
            stmt.execute();
        } catch (SQLException e) {
            log.error("Error in prepared statement: " + stmt, e);
            throw e;
        }
    }

    private long getRemainingCountAndSetCurLogTime(PreparedStatement findCountStmt, PreparedStatement findTimeStmt) throws SQLException {
        long recordCount = 0;
        findCountStmt.setTimestamp(1, getCutOffTimestamp());
        ResultSet rs = findCountStmt.executeQuery();
        if (rs.next()) {
            recordCount = rs.getLong(1);
        }
        rs = findTimeStmt.executeQuery();
        if (rs.next()) {
            curLogTime = rs.getTimestamp(1);
        }
        close(rs);
        return recordCount;
    }
    
    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                if (log.isErrorEnabled())
                    log.error("failed to close ResultSet", e);
            }
        }
    }
    
    private Timestamp getCutOffTimestamp() {
        if (cutOffTimestamp == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 24);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.HOUR, -(getCutOffDays() * 24));
            Timestamp tstamp = new Timestamp(cal.getTimeInMillis());
            cutOffTimestamp = tstamp;
            log.info("Archiver.getTimestampBeforeDays: " + cutOffTimestamp);
        }
        return cutOffTimestamp;
    }
    
    private Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    private PreparedStatement getInsertPAStmt() throws SQLException {
        if (insertPAStmt == null) {
            insertPAStmt =  new LoggablePreparedStatement(
                    getConnection(), insertPaSql);
        }
        return insertPAStmt;
    }

    private PreparedStatement getInsertPACustAttrStmt()
        throws SQLException {
        if (insertPACustAttrStmt == null) {
            insertPACustAttrStmt =  new LoggablePreparedStatement(
                    getConnection(), insertPaAttrSql);
        }
        return insertPACustAttrStmt;
    }

    private PreparedStatement getInsertPAOblStmt()
    throws SQLException {
        if (insertPAOblStmt == null) {
            insertPAOblStmt =  new LoggablePreparedStatement(
                    getConnection(), insertPaOblSql);
        }
        return insertPAOblStmt;
    }
    
    private PreparedStatement getDeletePAStmt() throws SQLException {
        if (deletePAStmt == null) {
            deletePAStmt = new LoggablePreparedStatement(
                                    getConnection(), deletePaSql);
        }
        return deletePAStmt;
    }

    private PreparedStatement getDeletePACustAttrStmt()
        throws SQLException {
        if (deletePACustAttrStmt == null) {
            deletePACustAttrStmt = new LoggablePreparedStatement(
                    getConnection(), deletePaAttrSql);
        }
        return deletePACustAttrStmt;
    }

    private PreparedStatement getDeletePAOblStmt()
    throws SQLException {
        if (deletePAOblStmt == null) {
            deletePAOblStmt = new LoggablePreparedStatement(
                    getConnection(), deletePaOblSql);
        }
        return deletePAOblStmt;
    }
    
    private PreparedStatement getInsertTAStmt() throws SQLException {
        if (insertTAStmt == null) {
            insertTAStmt = new LoggablePreparedStatement(
                    getConnection(), insertTaSql);
        }
        return insertTAStmt;
    }

    private PreparedStatement getInsertTACustAttrStmt()
        throws SQLException {
        if (insertTACustAttrStmt == null) {
            insertTACustAttrStmt = new LoggablePreparedStatement(
                    getConnection(), insertTaAttrSql);
        }
        return insertTACustAttrStmt;
    }

    private PreparedStatement getDeleteTAStmt() throws SQLException {
        if (deleteTAStmt == null) {
            deleteTAStmt = new LoggablePreparedStatement(
                    getConnection(), deleteTaSql);
        }
        return deleteTAStmt;
    }

    private PreparedStatement getDeleteTACustAttrStmt()
        throws SQLException {
        if (deleteTACustAttrStmt == null) {
            deleteTACustAttrStmt =new LoggablePreparedStatement(
                    getConnection(), deleteTaAttrSql);
        }
        return deleteTACustAttrStmt;
    }

    private PreparedStatement getSelectPALogCountForArchivalStmt() throws SQLException {
        if (selectPALogCountForArchivalStmt == null) {
            selectPALogCountForArchivalStmt = new LoggablePreparedStatement(
                    getConnection(), SELECT_PA_LOG_COUNT_FOR_ARCHIVAL_SQL);
        }
        return selectPALogCountForArchivalStmt;
    }

    private PreparedStatement getSelectPAMinTimeStmt() throws SQLException {
        if (selectPAMinTimeStmt == null) {
            selectPAMinTimeStmt = new LoggablePreparedStatement(
                    getConnection(), SharedLib.SELECT_PA_MIN_TIME_SQL);
        }
        return selectPAMinTimeStmt;
    }
    
    private PreparedStatement getSelectTALogCountForArchivalStmt() throws SQLException {
        if (selectTALogCountForArchivalStmt == null) {
            selectTALogCountForArchivalStmt = new LoggablePreparedStatement(
                    getConnection(),  SELECT_TA_LOG_COUNT_FOR_ARCHIVAL_SQL);
        }
        return selectTALogCountForArchivalStmt;
    }

    private PreparedStatement getSelectTAMinTimeStmt() throws SQLException {
        if (selectNextTAMinTimeStmt == null) {
            selectNextTAMinTimeStmt = new LoggablePreparedStatement(
                    getConnection(), SharedLib.SELECT_TA_MIN_TIME_SQL);
        }
        return selectNextTAMinTimeStmt;
    }
    
    private PreparedStatement getFindPaLogCountStmt() throws SQLException{
        if (findPaLogCountStmt == null) {
            findPaLogCountStmt = new LoggablePreparedStatement(
                    getConnection(), FIND_PA_ARCHIVE_COUNT_SQL_FRAGMENT);
        }
        return findPaLogCountStmt;
    }
    
    private int getPALogsToArchiveCount() throws SQLException {
        int count = 0;
        PreparedStatement stmt = getFindPaLogCountStmt();
        stmt.setTimestamp(1, getCutOffTimestamp());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        close(rs);
        return count;
    }
    
    private PreparedStatement getFindTaLogCountStmt() throws SQLException{
        if (findTaLogCountStmt == null) {
            findTaLogCountStmt = new LoggablePreparedStatement(
                    getConnection(), FIND_TA_ARCHIVE_COUNT_SQL_FRAGMENT);
        }
        return findTaLogCountStmt;
    }
    
    private int getTALogsToArchiveCount() throws SQLException {
        int count = 0;
        PreparedStatement stmt = getFindTaLogCountStmt();
        stmt.setTimestamp(1, getCutOffTimestamp());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        close(rs);
        return count;
    }

    private String paColumnNames = null;
    
    private synchronized String getPaColumnNames() throws HibernateException {
        if (paColumnNames == null) {
            paColumnNames = StringUtils.join(getColumnsFromClass(ReportActivityLogDO.class), ", ");
        }

        return paColumnNames;
    }

    private String taColumnNames = null;
    
    private synchronized String getTaColumnNames() throws HibernateException {
        if (taColumnNames == null) {
            taColumnNames = StringUtils.join(getColumnsFromClass(ReportTrackingActivityLogDO.class), ", ");
        }

        return taColumnNames;
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
    
    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public IConfiguration getConfig() {
        return config;
    }

    public void setConfig(IConfiguration config) {
        this.config = config;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;        
    }
    
    public int getCutOffDays() {
        return cutOffDays;
    }

    public void setCutOffDays(int cutOffDays) {
        this.cutOffDays = cutOffDays;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    
}
