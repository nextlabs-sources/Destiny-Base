/*
 * Created on Jan 06, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.pluginmanager.hibernateimpl;

import java.util.Date;
import java.util.Set;

import com.bluejungle.framework.utils.ObjectHelper;
import com.bluejungle.framework.utils.TimeRelation;
import com.nextlabs.destiny.container.shared.pluginmanager.IPDPPluginEntity;

public class PDPPluginEntity implements IPDPPluginEntity {
    private Long id;

    private String name;

    private String description;

    private String status;

    private TimeRelation timeRelation;

    private Date createdDate;

    private Date modifiedDate;

    private Set pluginFiles;

    /**
     * Private constructor for hibernate
     */
    PDPPluginEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TimeRelation getTimeRelation() {
        return timeRelation;
    }

    public void setTimeRelation(TimeRelation timeRelation) {
        this.timeRelation = timeRelation;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Set getPluginFiles() {
        return pluginFiles;
    }

    public void setPluginFiles(Set pluginFiles) {
        if (pluginFiles == null) {
            throw new NullPointerException("pluginFiles cannot be null");
        }

        this.pluginFiles = pluginFiles;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PDPPluginEntity)) {
            return false;
        }

        if (other == this) {
            return true;
        }

        // Comparing 
        PDPPluginEntity entity = (PDPPluginEntity)other;
        return ObjectHelper.nullSafeEquals(id, entity.id) &&
            ObjectHelper.nullSafeEquals(name, entity.name) &&
            ObjectHelper.nullSafeEquals(description, entity.description) &&
            ObjectHelper.nullSafeEquals(status, entity.status) &&
            ObjectHelper.nullSafeEquals(timeRelation, entity.timeRelation) &&
            ObjectHelper.nullSafeEquals(createdDate, entity.createdDate) &&
            ObjectHelper.nullSafeEquals(modifiedDate, entity.modifiedDate) &&
            ObjectHelper.nullSafeEquals(pluginFiles, entity.pluginFiles);
    }
}
