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
import java.util.Set;

import com.bluejungle.framework.utils.TimeRelation;

public interface IPDPPluginEntity {
    Long getId();

    String getName();

    String getDescription();

    String getStatus();

    TimeRelation getTimeRelation();

    Date getCreatedDate();

    Date getModifiedDate();

    Set getPluginFiles();
}
