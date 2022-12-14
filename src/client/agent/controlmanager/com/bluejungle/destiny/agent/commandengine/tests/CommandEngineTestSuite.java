/*
 * Created on Dec 13, 2004
 * All sources, binaries and HTML pages 
 * (C) copyright 2004 by Blue Jungle Inc., Redwood City CA, 
 * Ownership remains with Blue Jungle Inc, 
 * All rights reserved worldwide. 
 */
package com.bluejungle.destiny.agent.commandengine.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author fuad
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/client/agent/controlmanager/com/bluejungle/destiny/agent/commandengine/tests/CommandEngineTestSuite.java#1 $:
 */

public class CommandEngineTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.agent.commandengine.tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(LogCommandTest.class);
        //suite.addTestSuite(CommandExecutorTest.class);
        //$JUnit-END$
        return suite;
    }
}
