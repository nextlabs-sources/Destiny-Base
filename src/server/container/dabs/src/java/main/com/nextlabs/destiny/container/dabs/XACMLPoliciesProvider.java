/*
 * Created on Dec 16, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.dabs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.DCCResourceLocators;
import com.bluejungle.destiny.container.dcc.INamedResourceLocator;
import com.bluejungle.destiny.container.dcc.ServerRelativeFolders;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.heartbeat.IHeartbeatProvider;
import com.bluejungle.framework.utils.SerializationUtils;
import com.bluejungle.framework.utils.UnmodifiableDate;
import com.bluejungle.pf.destiny.lifecycle.DeploymentEntity;
import com.bluejungle.pf.destiny.lifecycle.DeploymentType;
import com.bluejungle.pf.destiny.lifecycle.EntityManagementException;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.bluejungle.pf.destiny.lifecycle.LifecycleManager;
import com.nextlabs.pf.destiny.lib.XACMLPoliciesRequestDTO;
import com.nextlabs.pf.destiny.lib.XACMLPoliciesResponseDTO;

public class XACMLPoliciesProvider implements IHeartbeatProvider {
    private static final Log LOG = LogFactory.getLog(XACMLPoliciesProvider.class);
    private String policiesFolder = null;
    private static final long POLICY_CHANGE_CHECK_INTERVAL_MS = 15 * 1000;
    private long lastPolicyModifiedCheckTime = 0;
    private long lastPolicyModifiedTime = 0;
    private LifecycleManager lm;
    
    public XACMLPoliciesProvider() {
        IComponentManager manager = ComponentManagerFactory.getComponentManager();
        INamedResourceLocator serverResourceLocator = (INamedResourceLocator)manager.getComponent(DCCResourceLocators.SERVER_HOME_RESOURCE_LOCATOR);
        policiesFolder = serverResourceLocator.getFullyQualifiedName(ServerRelativeFolders.XACML_POLICIES_FOLDER.getPath());
        lm = manager.getComponent(LifecycleManager.COMP_INFO);
    }
    
    public Serializable serviceHeartbeatRequest(String name, String requestData) {
        return serviceHeartbeatRequest(name, SerializationUtils.unwrapSerialized(requestData));
    }

    private Serializable serviceHeartbeatRequest(String name, Serializable requestData) {
        if (!(requestData instanceof XACMLPoliciesRequestDTO)) {
            return null;
        }

        XACMLPoliciesRequestDTO dto = (XACMLPoliciesRequestDTO)requestData;

        return getResponseDTO(dto.getTimestamp());
    }

    /**
     * Get the response DTO for the PDP, relative to the PDP's last
     * timestamp. This will either be the latest data or, if the PDP
     * has updated since the data changed, null (indicating that the
     * PDP's data is accurate)

     * @param lastTimestamp the timestamp from the hb request
     * @return dto response. null if the nothing has changed since the last hb
     */
    synchronized private XACMLPoliciesResponseDTO getResponseDTO(Date lastTimestamp) {
        LOG.debug("Getting response for timestamp " + lastTimestamp.getTime());
        
        Date now = new Date();

        // The system has all changes that occurred before 'lastTimestamp', so we are only
        // interested in changes between then and now
        Date lastModifiedTime = getLastModifiedTime(lastTimestamp, now);

        if (lastModifiedTime.after(lastTimestamp)) {
            return buildResponseDTO(now);
        }

        return null;
    }

    /**
     * Construct a new DTO from the contents of the XACML policies folder
     */
    private XACMLPoliciesResponseDTO buildResponseDTO(Date asOf) {
        LOG.debug("Creating new response with modified time " + asOf.getTime());
        XACMLPoliciesResponseDTO dto = new XACMLPoliciesResponseDTO(asOf);

        try {
            List<DeploymentEntity> entities = lm.getAllDeployedEntities(EntityType.XACML_POLICY, asOf, DeploymentType.PRODUCTION);
            
            for (DeploymentEntity entity : entities) {
                dto.addPolicyOrPolicySet(entity.getDevelopmentEntity().getId(), entity.getPql());
            }
        } catch (EntityManagementException e) {
            LOG.error("Unable to get deployed entities. Returning null");
            return null;
        }
        
        return dto;
    }

    /**
     * Determines the most recent update time of the xacml policies data.
     */
    private synchronized Date getLastModifiedTime(Date since, Date asOf) {
        try {
            return lm.getLatestDeploymentTime(EntityType.XACML_POLICY, since == null ? UnmodifiableDate.START_OF_TIME : since, asOf, DeploymentType.PRODUCTION);
        } catch(EntityManagementException e) {
            LOG.error("Unable to find last deployed time for XACML policies", e);
        }

        return UnmodifiableDate.forDate(new Date());
    }
}
