/*
 * Created on Apr 10, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.pf.destiny.lib;

import com.bluejungle.framework.expressions.IPredicate;

/**
 * A leaf object search specification
 * 
 * @author sgoldstein
 */

public class LeafObjectSearchSpec {

    private final LeafObjectType leafObjectType;
    private final IPredicate searchPredicate;
    private String namespaceId;
    private final int startIndex;
    private final int maxResults;

    /**
     * Create an instance of LeafObjectSearchSpec
     * 
     * @param leafObjectType
     * @param searchString
     * @param maxResults
     */
    public LeafObjectSearchSpec(LeafObjectType leafObjectType, IPredicate searchPredicate, int maxResults) {
        this(leafObjectType, searchPredicate, null, 0, maxResults);
    }
    
    /**
     * Create an instance of LeafObjectSearchSpec
     * 
     * @param leafObjectType
     * @param searchString
     * @param startIndex
     * @param maxResults
     */
    public LeafObjectSearchSpec(LeafObjectType leafObjectType, IPredicate searchPredicate, int startIndex, int maxResults) {
        this(leafObjectType, searchPredicate, null, startIndex, maxResults);
    }
    
    /**
     * 
     * Create an instance of LeafObjectSearchSpec
     * @param leafObjectType
     * @param searchPredicate
     * @param namespaceId
     * @param maxResults
     */
    public LeafObjectSearchSpec(LeafObjectType leafObjectType, IPredicate searchPredicate, String namespaceId, int maxResults) {
        this(leafObjectType, searchPredicate, namespaceId, 0, maxResults);
    }
    
    /**
     * 
     * Create an instance of LeafObjectSearchSpec
     * @param leafObjectType
     * @param searchPredicate
     * @param namespaceId
     * @param startIndex
     * @param maxResults
     */
    public LeafObjectSearchSpec(LeafObjectType leafObjectType, IPredicate searchPredicate, String namespaceId, int startIndex, int maxResults) {
        super();

        if (leafObjectType == null) {
            throw new NullPointerException("leafObjectType cannot be null.");
        }

        if (searchPredicate == null) {
            throw new NullPointerException("searchPredicate cannot be null.");
        }

        this.leafObjectType = leafObjectType;
        this.searchPredicate = searchPredicate;
        this.namespaceId = namespaceId;
        this.startIndex = startIndex;
        this.maxResults = maxResults;
    }

    /**
     * Retrieve the leafObjectType.
     * 
     * @return the leafObjectType.
     */
    public LeafObjectType getLeafObjectType() {
        return leafObjectType;
    }

    /**
     * Retrieve the maxResults.
     * 
     * @return the maxResults.
     */
    public int getMaxResults() {
        return maxResults;
    }
    
    /**
     * Retrieve the startIndex.
     * 
     * @return the startIndex.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Retrieve the search predicate.
     * 
     * @return the search predicate.
     */
    public IPredicate getSearchPredicate() {
        return searchPredicate;
    }

    
    /**
     * Retrieve the namespaceId.
     * @return the namespaceId.
     */
    public String getNamespaceId() {
        return this.namespaceId;
    }
}
