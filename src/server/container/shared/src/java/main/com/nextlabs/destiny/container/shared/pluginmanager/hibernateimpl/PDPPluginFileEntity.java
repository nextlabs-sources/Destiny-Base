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

import com.nextlabs.destiny.container.shared.pluginmanager.IPDPPluginFileEntity;

public class PDPPluginFileEntity implements IPDPPluginFileEntity {
    private Long id;
    
    private PDPPluginEntity plugin;
    
    private String name;
    
    private String type;
    
    private byte[] content;
    
    private Date modifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PDPPluginEntity getPlugin() {
        return plugin;
    }

    public void setPlugin(PDPPluginEntity plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin cannot be null");
        }
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        this.type = type;
    }
    
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        this.content = content;
    }
        
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        if (modifiedDate == null) {
            throw new NullPointerException("modifiedDate cannot be null");
        }
        this.modifiedDate = modifiedDate;
    }
        
}
