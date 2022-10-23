/*
 * Created on Mar 8, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.agentmgr;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/agentmgr/IAgentMgrQuerySpec.java#1 $
 */

public interface IAgentMgrQuerySpec {

    /**
     * Returns an array of the query terms
     * 
     * @return an array of the query terms
     */
    public IAgentMgrQueryTerm[] getSearchSpecTerms();

    /**
     * Returns an array of sort spec terms
     * 
     * @return an array of sort spec terms
     */
    public IAgentMgrSortTerm[] getSortSpecTerms();

    /**
     * Returns the maximum number of records to be fetched. 0 indicates no
     * limit.
     * 
     * @return the maximum number of records to be fetched.
     */
    public int getLimit();
}