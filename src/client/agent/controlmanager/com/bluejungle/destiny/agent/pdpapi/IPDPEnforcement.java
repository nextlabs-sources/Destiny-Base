package com.bluejungle.destiny.agent.pdpapi;

/*
 * Created on Jan 19, 2011
 *
 * All sources, binaries and HTML pages (C) copyright 2011 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/client/agent/controlmanager/com/bluejungle/destiny/agent/pdpapi/IPDPEnforcement.java#1 $:
 */

public interface IPDPEnforcement
{
    public String getResult();

    public String[] getObligations();
}
