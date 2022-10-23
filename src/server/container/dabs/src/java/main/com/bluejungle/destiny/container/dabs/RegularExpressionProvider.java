/*
 * Created on Nov 3, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.dabs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.server.shared.configuration.IRegularExpressionConfigurationDO;
import com.bluejungle.framework.heartbeat.IHeartbeatProvider;
import com.bluejungle.framework.utils.NameBasedObjectInputStream;
import com.bluejungle.pf.destiny.lib.RegularExpressionDTO;
import com.bluejungle.pf.destiny.lib.RegularExpressionRequestDTO;

/**
 * This heartbeat information provider prepares regexp information
 * for sending to the agents.
 *
 * @author Alan Morgan
 */
public class RegularExpressionProvider implements IHeartbeatProvider {
    private static final Log LOG = LogFactory.getLog(RegularExpressionProvider.class);
    
    /**
     * The DTO
     */
    private RegularExpressionDTO dto = null;
    
    public RegularExpressionProvider(IRegularExpressionConfigurationDO[] regexps) {
        dto = new RegularExpressionDTO(new Date());

        for (IRegularExpressionConfigurationDO regexp : regexps) {
            dto.addExpression(regexp.getName(),
                              regexp.getValue());
        }
    }

    public Serializable serviceHeartbeatRequest(String name, String data) {
        return serviceHeartbeatRequest(name, unwrapSerialized(data));
    }

    /*
     * Normally we'd use SerializationUtils.unwrapSerialized, but we need to play tricks
     * with the ObjectInputStream, so that won't work (see RegularExpressionRequestDTO
     * for the ugly details).
     */
    private Serializable unwrapSerialized(String str) {
        try {
            ObjectInputStream ois = new NameBasedObjectInputStream(new ByteArrayInputStream(Base64.getMimeDecoder().decode(str)),
                                                                   RegularExpressionRequestDTO.class);
            return (Serializable)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOG.warn("Exception when deserializing regular expression request", e);
        }

        return null;
    }
    
    public Serializable serviceHeartbeatRequest(String name, Serializable requestData) {
        if (!(requestData instanceof RegularExpressionRequestDTO)
            || dto == null) {
            return null;
        }

        RegularExpressionRequestDTO req = (RegularExpressionRequestDTO)requestData;

        if (req.getTimestamp().before(dto.getBuildTime())) {
            return dto;
        }

        return null;
    }
}
