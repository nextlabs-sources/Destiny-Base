/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.inquirymgr.hqlbuilder;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/inquirymgr/hqlbuilder/IConditionElement.java#1 $
 */

public interface IConditionElement {

    /**
     * Returns the condition expression
     * 
     * @return the condition expression
     */
    public String getExpression();
}