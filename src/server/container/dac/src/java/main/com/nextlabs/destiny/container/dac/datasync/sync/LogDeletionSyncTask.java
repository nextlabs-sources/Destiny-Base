/*
 * Created on Jan 16, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.dac.datasync.sync;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.PostgreSQLDialect;

import com.bluejungle.framework.comp.IConfiguration;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTask;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTaskUpdate;
import com.nextlabs.destiny.container.dac.datasync.sync.SyncTask;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;

import com.bluejungle.framework.datastore.hibernate.exceptions.DataSourceException;

/**
 * This task is responsible for deleting entiries from
 * policy_activity_log and obligation_log that have been sync'd and
 * from policy_custom_attr that referenc a sync'd log
 * 
 */
public class LogDeletionSyncTask {
    private static final Log LOG = LogFactory.getLog(LogDeletionSyncTask.class);
    
	private Dialect dialect;
	protected IDataSyncTaskUpdate update;

    static String DELETE_POLICY_CUSTOM_ATTR_LOG = "DELETE from " + SharedLib.PA_CUST_ATTR_TABLE +
                                                  " WHERE EXISTS (SELECT " + SharedLib.PA_TABLE + ".id FROM " + SharedLib.PA_TABLE +
                                                  " WHERE " + SharedLib.PA_CUST_ATTR_TABLE + ".policy_log_id = " + SharedLib.PA_TABLE + ".id AND " + SharedLib.PA_TABLE + ".sync_done = %s)";
    static String DELETE_POLICY_TAGS_LOG = "DELETE from POLICY_TAGS " +
                                           " WHERE EXISTS (SELECT " + SharedLib.PA_TABLE + ".id FROM " + SharedLib.PA_TABLE +
                                           " WHERE POLICY_TAGS.policy_log_id = " + SharedLib.PA_TABLE + ".id AND " + SharedLib.PA_TABLE + ".sync_done = %s)";
    static String DELETE_OBLIGATION_LOG = "delete from " + SharedLib.PA_OBLIGATION_TABLE + " where sync_done = %s";
    static String DELETE_POLICY_ACTIVITY_LOG = "delete from " + SharedLib.PA_TABLE + " where sync_done = %s";
                                           
    public boolean run(Session session, Connection connection, IConfiguration config, SyncTask syncTask) {
        getLog().info("LogDeletionSyncTask start");
        
		this.dialect = config.get(IDataSyncTask.DIALECT_CONFIG_PARAMETER);
		this.update = config.get(IDataSyncTask.TASK_UPDATE_PARAMETER);
		this.update.reset();

        try {
            executeSingleCommand(session, connection, String.format(DELETE_POLICY_CUSTOM_ATTR_LOG, dialectSpecificSyncDone()));
            executeSingleCommand(session, connection, String.format(DELETE_POLICY_TAGS_LOG, dialectSpecificSyncDone()));
            executeSingleCommand(session, connection, String.format(DELETE_OBLIGATION_LOG, dialectSpecificSyncDone()));
            executeSingleCommand(session, connection, String.format(DELETE_POLICY_ACTIVITY_LOG, dialectSpecificSyncDone()));
        } catch (DataSourceException dse) {
            getLog().error("Failed to run " + this.getClass().getSimpleName(), dse);

            return false;
        }

        return true;
    }
    
    private void executeSingleCommand(Session session, Connection connection, String sql) throws DataSourceException {
        try {
            Transaction t = session.beginTransaction();
            PreparedStatement statement = null;
            
            try {
                LOG.debug("Preparing statement [" + sql + "]");
                
                statement = connection.prepareStatement(sql);
                
                statement.executeUpdate();
                t.commit();
            } catch(SQLException e) {
                LOG.info("SQLException for [" + sql + "]", e);
                t.rollback();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        getLog().warn("Can't close log delete statement: " + sql, e);
                    }
                }
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
        
    }
    
    private Log getLog() {
        return LOG;
    }
    
    private String dialectSpecificSyncDone() {
    	return (this.dialect instanceof PostgreSQLDialect) ? "true" : "1"; 
    }
}
