/*
 * Created on Oct 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import com.bluejungle.framework.patterns.EnumBase;

public class PerformObligationsEnumType extends EnumBase {
    public static final PerformObligationsEnumType ALL = new PerformObligationsEnumType("all");
    public static final PerformObligationsEnumType NONE = new PerformObligationsEnumType("none");
    public static final PerformObligationsEnumType PEP_ONLY = new PerformObligationsEnumType("pep");

    private PerformObligationsEnumType(String name) {
        super(name);
    }

    public static PerformObligationsEnumType getByName(String name) {
        return getElement(name, PerformObligationsEnumType.class);
    }
}
