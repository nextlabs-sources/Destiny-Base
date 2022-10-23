/*
 * Created on Jan 07, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.pluginmanager;

import java.util.Collection;
import java.util.Date;

import com.nextlabs.destiny.container.shared.pluginmanager.hibernateimpl.PDPPluginEntity;

public interface IPDPPluginManager {
    public static final String COMP_NAME = "PDP Plugin Manager";

    long getLastModifiedTime() throws PDPPluginManagementException;
    
    Collection<PDPPluginEntity> getModifiedPlugins(Date asOf) throws PDPPluginManagementException;
    
    Collection<PDPPluginEntity> getDeletedPlugins(Date asOf) throws PDPPluginManagementException;
}
