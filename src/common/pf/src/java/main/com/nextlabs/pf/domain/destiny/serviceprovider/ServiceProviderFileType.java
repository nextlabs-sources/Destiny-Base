/*
 * Created on Nov 19, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.pf.domain.destiny.serviceprovider;

import com.bluejungle.framework.patterns.EnumBase;


public class ServiceProviderFileType extends EnumBase {
    public static final ServiceProviderFileType MAIN_JAR = new ServiceProviderFileType("main");
    public static final ServiceProviderFileType SUPPORTING_JAR = new ServiceProviderFileType("supporting");
    public static final ServiceProviderFileType PROPERTIES_FILE = new ServiceProviderFileType("properties");
    public static final ServiceProviderFileType MISC_FILE = new ServiceProviderFileType("misc");

    private ServiceProviderFileType(String name) {
        super(name);
    }

    public static ServiceProviderFileType getServiceProviderFileType(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        return getElement(name, ServiceProviderFileType.class);
    }
}
