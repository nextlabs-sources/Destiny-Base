/*
 * Created on Apr 25, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.dictionary.enrollment.controller;


/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/dictionary/enrollment/controller/IColumnData.java#1 $
 */

public interface IColumnData {
    public String getLogicalName();
    public String getType();
    public String getElementType();
    public String getDisplayName();
}
