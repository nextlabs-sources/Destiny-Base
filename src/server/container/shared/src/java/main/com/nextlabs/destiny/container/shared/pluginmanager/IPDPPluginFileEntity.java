/*
 * Created on Jan 06, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.pluginmanager;

import java.util.Date;

import com.nextlabs.destiny.container.shared.pluginmanager.hibernateimpl.PDPPluginEntity;

public interface IPDPPluginFileEntity {
    Long getId();

    PDPPluginEntity getPlugin();

    String getName();

    String getType();

    byte[] getContent();

    Date getModifiedDate();
}
