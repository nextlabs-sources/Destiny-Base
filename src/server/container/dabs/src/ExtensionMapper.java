/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:06:07 GMT)
 */
package com.bluejungle.destiny.services.agent;


/**
 *  ExtensionMapper class
 */
@SuppressWarnings({"unchecked",
    "unused"
})
public class ExtensionMapper {
    public static java.lang.Object getTypeObject(
        java.lang.String namespaceURI, java.lang.String typeName,
        javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-units".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeUnits.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "RelationalOpDTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.RelationalOpDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) &&
                "DMSExternalUserQueryResultsDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSExternalUserQueryResultsDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ID".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ID.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentProfileStatusData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentProfileStatusData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) &&
                "SharedFolderAliasesAlias".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderAliasesAlias.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderDataCookie".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderDataCookie.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSRoleData".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSRoleData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ActivityJournalingSettingsDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DeploymentRequest".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DeploymentRequest.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/version/types".equals(namespaceURI) &&
                "Version".equals(typeName)) {
            return com.bluejungle.version.types.Version.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Role".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Role.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "IPAddress".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.IPAddress.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationProcess".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationProcess.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Access".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Access.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryFieldType".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryFieldType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "name".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) &&
                "AgentStartupConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentStartupConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SubjectDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SubjectDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentHeartbeatData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentHeartbeatData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CommProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "ActionTypeDTO".equals(typeName)) {
            return com.bluejungle.domain.types.ActionTypeDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "BaseProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.BaseProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ActivityJournalingSettingsInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CommProfileInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "BaseProfileDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.BaseProfileDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "UserNotification".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.UserNotification.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SystemUser".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SystemUser.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentUpdates".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentUpdates.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-interval-DTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/custom_obligations".equals(
                    namespaceURI) && "CustomObligationsData".equals(typeName)) {
            return com.bluejungle.destiny.types.custom_obligations.CustomObligationsData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CommProfileDTOQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOQueryField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SubjectDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SubjectDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "IDList".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.IDList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "AgentTypeEnum".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.AgentTypeEnum.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "UserNotificationBag".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.UserNotificationBag.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "AgentCapability".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.AgentCapability.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSUserData".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSUserData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) &&
                "DefaultAccessAssignment".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentPluginData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentPluginData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ActivityJournalingSettingsDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ServiceNotReadyFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentProfileInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) &&
                "AgentAttachments".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentAttachments.Factory.parse(reader);
        }
        
        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) &&
                "AgentAttachment".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentAttachment.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) &&
                "DefaultAccessAssignmentList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Principal".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Principal.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "AccessList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.AccessList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "PrincipalType".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.PrincipalType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentProfileDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "BaseProfileInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.BaseProfileInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/custom_obligations".equals(
                    namespaceURI) && "CustomObligation".equals(typeName)) {
            return com.bluejungle.destiny.types.custom_obligations.CustomObligation.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentShutdownData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentShutdownData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSRoleDataList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSRoleDataList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderAliasList".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderAliasList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "title".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CommProfileDTOQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOQueryTermSet.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentPluginDataElement".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentPluginDataElement.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryTermSet.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "AgentTypeDTO".equals(typeName)) {
            return com.bluejungle.domain.types.AgentTypeDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentRegistrationData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentRegistrationData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CommProfileDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderAliases".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderAliases.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Component".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Component.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CommProfileDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) && "AgentStartupData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentStartupData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "ActionTypeDTOList".equals(typeName)) {
            return com.bluejungle.domain.types.ActionTypeDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderData".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/agent/types".equals(
                    namespaceURI) &&
                "AgentUpdateAcknowledgementData".equals(typeName)) {
            return com.bluejungle.destiny.services.agent.types.AgentUpdateAcknowledgementData.Factory.parse(reader);
        }
		
		 if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/deployment/types".equals(
                    namespaceURI) && "AgentDeployResult".equals(typeName)) {
            return com.bluejungle.destiny.services.deployment.types.AgentDeployResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/deployment/types".equals(
                    namespaceURI) && "AgentDeployStatus".equals(typeName)) {
            return com.bluejungle.destiny.services.deployment.types.AgentDeployStatus.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ServiceNotReadyFault.Factory.parse(reader);
        }
		
        if ("http://bluejungle.com/destiny/services/deployment/types".equals(
                    namespaceURI) && "PolicyPushInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.deployment.types.PolicyPushInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/deployment/types".equals(
                    namespaceURI) && "PolicyPushList".equals(typeName)) {
            return com.bluejungle.destiny.services.deployment.types.PolicyPushList.Factory.parse(reader);
        }


        throw new org.apache.axis2.databinding.ADBException("Unsupported type " +
            namespaceURI + " " + typeName);
    }
}
