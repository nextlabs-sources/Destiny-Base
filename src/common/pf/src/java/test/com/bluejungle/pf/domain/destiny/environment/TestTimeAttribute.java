/*
 * Created on Aug 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.pf.domain.destiny.environment;

import junit.framework.TestCase;
import junit.framework.TestResult;

import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;

/**
 * @author sasha
 */

public class TestTimeAttribute extends TestCase {
    /**
     * @see junit.framework.TestCase#run()
     */
    public TestResult run() {
        return super.run();
    }

    public void testTIME(){
        // Some quality, in-depth unit testification going on here
        IRelation rel = TimeAttribute.TIME.buildRelation(RelationOp.GREATER_THAN_EQUALS, "00:00:00");
        assertNotNull(rel);
        EvaluationRequest er = new EvaluationRequest();
        assertTrue(rel.match(er));
    }

}
