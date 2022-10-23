/*
 * Created on Jan 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.profilemgr.service;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axis2.databinding.types.Token;
import org.apache.axis2.databinding.types.UnsignedShort;
import org.apache.axis2.databinding.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.shared.profilemgr.IActivityJournalingSettings;
import com.bluejungle.destiny.container.shared.profilemgr.IAgentProfileDO;
import com.bluejungle.destiny.container.shared.profilemgr.ICommProfileDO;
import com.bluejungle.destiny.container.shared.profilemgr.IUserProfileDO;
import com.bluejungle.destiny.framework.types.Name;
import com.bluejungle.destiny.framework.types.TimeIntervalDTO;
import com.bluejungle.destiny.framework.types.TimeUnits;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO;
import com.bluejungle.destiny.services.management.types.AgentProfileDTO;
import com.bluejungle.destiny.services.management.types.CommProfileDTO;
import com.bluejungle.destiny.services.management.types.UserProfileDTO;
import com.bluejungle.domain.types.ActionTypeDTO;
import com.bluejungle.domain.types.AgentTypeDTO;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.domain.types.ActionTypeDTOList;
import com.bluejungle.framework.utils.TimeInterval;
import com.nextlabs.framework.utils.StreamDataSource;

/**
 * A helper class for implementing a web service layer which exposes
 * functionality of the profile manager Currently, it only contains utility
 * methods to tranlating a DO instance to a DTO instance
 * 
 * @author sgoldstein
 */
public class ProfileServiceHelper {

    private static final Log LOG = LogFactory.getLog(ProfileServiceHelper.class.getName());

    private static final ProfileServiceHelper singletonInstance = new ProfileServiceHelper();

    /**
     * Constructor
     *  
     */
    private ProfileServiceHelper() {
        super();
    }

    public static final ProfileServiceHelper getInstance() {
        return singletonInstance;
    }

    /**
     * Retrieve an AgentProfileDTO with the data provided in the specified
     * IAgentProfileDO
     * 
     * @param agentProfile
     *            The AgentProfileDO containing the data to transfer
     * @return The created AgentProfileDTO
     */
    public AgentProfileDTO getAgentProfileDTO(IAgentProfileDO agentProfile) {
        AgentProfileDTO valueToReturn = new AgentProfileDTO();
        valueToReturn.setId(agentProfile.getId().longValue());
        valueToReturn.setCreatedDate(agentProfile.getCreatedDate());
        valueToReturn.setModifiedDate(agentProfile.getModifiedDate());

        Name name = new Name();
        name.setName(new Token(agentProfile.getName()));
        valueToReturn.setName(name);
        valueToReturn.setTrayIconEnabled(agentProfile.isTrayIconEnabled());
        valueToReturn.setLogViewingEnabled(agentProfile.isLogViewingEnabled());
        return valueToReturn;
    }

    /**
     * Retrieve an CommProfileDTO with the data provided in the specified
     * ICommProfileDO
     * 
     * @param commProfile
     *            The ICommProfileDO containing the data to transfer
     * @return The created CommProfileDTO
     */
    public CommProfileDTO getCommProfileDTO(ICommProfileDO commProfile) {
        CommProfileDTO valueToReturn = new CommProfileDTO();
        valueToReturn.setId(commProfile.getId().longValue());
        valueToReturn.setCreatedDate(commProfile.getCreatedDate());
        valueToReturn.setModifiedDate(commProfile.getModifiedDate());

        Name name = new Name();
        name.setName(new Token(commProfile.getName()));
        valueToReturn.setName(name);
        AgentTypeDTO agentTypeDTO = AgentTypeDTO.Factory.fromValue(commProfile.getAgentType().getId());
        valueToReturn.setAgentType(agentTypeDTO);

        try {
            valueToReturn.setDABSLocation(new URI(commProfile.getDABSLocation().toString()));
        } catch (URI.MalformedURIException exception) {
            // Shouldn't happen
            LOG.warn("DABSLocation URI transfer for comm profile failed", exception);
        }

        TimeInterval heartBeatFrequency = commProfile.getHeartBeatFrequency();
        TimeIntervalDTO heartBeatFrequencyToSet = getTimeIntervalDTO(heartBeatFrequency);
        valueToReturn.setHeartBeatFrequency(heartBeatFrequencyToSet);
        valueToReturn.setLogLimit(new UnsignedShort(commProfile.getLogLimit()));
        com.bluejungle.framework.utils.TimeInterval logFrequency = commProfile.getLogFrequency();
        TimeIntervalDTO logFrequencyToSet = getTimeIntervalDTO(logFrequency);
        valueToReturn.setLogFrequency(logFrequencyToSet);
        valueToReturn.setPushEnabled(commProfile.isPushEnabled());
        valueToReturn.setDefaultPushPort(new UnsignedShort(commProfile.getDefaultPushPort()));
        valueToReturn.setDefaultProfile(commProfile.isDefault());

        StreamDataSource dh = new StreamDataSource();
        dh.setInputStream(new ByteArrayInputStream(commProfile.getPasswordHash()));
        dh.setContentType("application/octet-stream");
        dh.setName("password hash");
        valueToReturn.setPasswordHash(new DataHandler(dh));
		
        IActivityJournalingSettings currentActivityJournalingSettings = commProfile.getCurrentJournalingSettings();
        ActivityJournalingSettingsDTO currentActivityJournalingSettingsDTO = getActivityJournalingSettingsDTO(currentActivityJournalingSettings);
        valueToReturn.setCurrentActivityJournalingSettings(currentActivityJournalingSettingsDTO);

        IActivityJournalingSettings customActivityJournalingSettings = commProfile.getCustomJournalingSettings();
        ActivityJournalingSettingsDTO customActivityJournalingSettingsDTO = getActivityJournalingSettingsDTO(customActivityJournalingSettings);
        valueToReturn.setCustomActivityJournalingSettings(customActivityJournalingSettingsDTO);

        return valueToReturn;
    }

    /**
     * Translate a IActivityJournalingSettings to a ActivityJournalingSettingsDTO
     * 
     * @param activityJournalingSettings
     *            the IActivityJournalingSettings instance to translate
     * @return the created ActivityJournalingSettingsDTO
     */
    public ActivityJournalingSettingsDTO getActivityJournalingSettingsDTO(IActivityJournalingSettings activityJournalingSettings) {
        String currentActivityJournalingSettingsName = activityJournalingSettings.getName();

        Set loggedActions = activityJournalingSettings.getLoggedActions();
        ActionTypeDTO[] loggedActionsDTO = new ActionTypeDTO[loggedActions.size()];
        Iterator loggedActionsIterator = loggedActions.iterator();
        for (int i = 0; loggedActionsIterator.hasNext(); i++) {
            ActionEnumType nextAction = (ActionEnumType) loggedActionsIterator.next();

            ActionTypeDTO actionTypeDTO = ActionTypeDTO.Factory.fromValue(nextAction.getName());
            loggedActionsDTO[i] = actionTypeDTO;
        }

        ActionTypeDTOList agentActivityList = new ActionTypeDTOList();
        agentActivityList.setAction(loggedActionsDTO);

        ActivityJournalingSettingsDTO dto = new ActivityJournalingSettingsDTO();
        dto.setName(currentActivityJournalingSettingsName);
        dto.setLoggedActivities(agentActivityList);

        return dto;
    }

    /**
     * Translate a TimeInterval to a TimeInterval DTO
     * 
     * @param timeInterval
     *            the time interval to translate
     * @return the tranlated time interval
     */
    private TimeIntervalDTO getTimeIntervalDTO(TimeInterval timeInterval) {
        int time = timeInterval.getTime();
        TimeInterval.TimeUnit timeUnit = timeInterval.getTimeUnit();
        TimeIntervalDTO timeIntervalToReturn = new TimeIntervalDTO();
        timeIntervalToReturn.setTime(new UnsignedShort(time));

        timeIntervalToReturn.setTimeUnit(TimeUnits.Factory.fromValue(timeUnit.getName()));
        return timeIntervalToReturn;
    }

    /**
     * Retrieve a UserProfileDTO with the data provided in the specified
     * IUserProfileDO
     * 
     * @param userProfile
     *            The IUserProfileDO containing the data to transfer
     * @return The created UserProfileDTO
     */
    public UserProfileDTO getUserProfileDTO(IUserProfileDO userProfile) {
        UserProfileDTO valueToReturn = new UserProfileDTO();
        valueToReturn.setId(userProfile.getId().longValue());
        valueToReturn.setCreatedDate(userProfile.getCreatedDate());
        valueToReturn.setModifiedDate(userProfile.getModifiedDate());

        Name name = new Name();
        name.setName(new Token(userProfile.getName()));
        valueToReturn.setName(name);
        
        return valueToReturn;
    }
}
