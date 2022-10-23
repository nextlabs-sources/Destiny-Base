/*
 * Created on Jun 15, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.container.dac.datasync.sync;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.datastore.hibernate.BatchWriter;
import com.nextlabs.destiny.container.dac.datasync.log.ReportObligationLog;
import com.nextlabs.destiny.container.dac.datasync.log.ReportObligationLogWriter;
import com.nextlabs.destiny.container.dac.datasync.sync.SyncTaskBase;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;

/**
 * @author hchan
 */

public class ObligationLogSyncTask extends SyncTaskBase<ReportObligationLog> {
    private static final Log LOG = LogFactory.getLog(PolicyActivityLogSyncTask.class);

    
    /* policy obligation log    */
    private static final String ID_COLUMN = "id";
    private static final String REF_LOG_ID_COLUMN = "ref_log_id";
    private static final String NAME_COLUMN = "name";
    private static final String ATTR_ONE_COLUMN = "attr_one";
    private static final String ATTR_TWO_COLUMN = "attr_two";
    private static final String ATTR_THREE_COLUMN = "attr_three";
    
    
    private static final String SELECT_LOG_QUERY;
    
    static {
        // Nothing about our code requires or benefits from an "order by", but the DB2 hibernate
        // dialect requires it when using batch reads, so we have to have it
        SELECT_LOG_QUERY = "select "
                           + ID_COLUMN + "," +
                           REF_LOG_ID_COLUMN + "," +
                           NAME_COLUMN + "," +
                           ATTR_ONE_COLUMN + "," +
                           ATTR_TWO_COLUMN + "," +
                           ATTR_THREE_COLUMN +
                           " from " + SharedLib.PA_OBLIGATION_TABLE +
                           " where sync_done is null order by " + REF_LOG_ID_COLUMN + " ASC";
    }

    private PreparedStatement selectLogStatement = null;
    
    public ObligationLogSyncTask(){
        super(ReportObligationLog.class, String.format(COUNT_QUERY_TEMPLATE, SharedLib.PA_OBLIGATION_TABLE));
    }
    
    @Override
    protected void start() throws Exception {
        super.start();
        update.setPrefix("Obligation Log Synchronization");
        String sql = getDialect().getLimitString(SELECT_LOG_QUERY, false, getBatchSize());
        LOG.trace(sql);
        selectLogStatement = connection.prepareStatement(sql);
    }
    
    @Override
    protected int parse() throws Exception {
        transform = new HashMap<Number, ReportObligationLog>(getBatchSize());
        
        selectLogStatement.setQueryTimeout(getRemainingTime());
        if (getDialect().supportsVariableLimit()) {
            selectLogStatement.setInt(1, getBatchSize());
        }
        
        ResultSet r = null;
        try {
            LOG.trace("START select " + SharedLib.PA_OBLIGATION_TABLE);
            r = selectLogStatement.executeQuery();
            LOG.trace("END select " + SharedLib.PA_OBLIGATION_TABLE);

            while (r.next()) {
                ReportObligationLog t = new ReportObligationLog();
                t.id = r.getLong(ID_COLUMN);
                t.refLogId = r.getLong(REF_LOG_ID_COLUMN);
                t.name = r.getString(NAME_COLUMN);
                t.attrOne = r.getString(ATTR_ONE_COLUMN);
                t.attrTwo = r.getString(ATTR_TWO_COLUMN);
                t.attrThree = r.getString(ATTR_THREE_COLUMN);

                transform.put(t.id, t);
            }
        } finally {
            close(r);
        }

        return transform.size();
    }
    
    @Override
    protected void done() throws Exception {
        if (selectLogStatement != null) {
            selectLogStatement.close();
        }

        super.done();
    }
    
    @Override
    protected int getResultCheckIndex() {
        return ReportObligationLogWriter.INSERT_LOG_QUERY_INDEX;
    }

	@Override
	protected BatchWriter<ReportObligationLog> getWriter() {
		BatchWriter<ReportObligationLog> writer = new ReportObligationLogWriter();
		writer.setLog(LogFactory.getLog(ReportObligationLogWriter.class));
		return writer;
	}

	@Override
	public void generateInsertQuery(int numberOfExtendedAttrColumns,
			String attrPrefix) {
		//do nothing
	}
}
