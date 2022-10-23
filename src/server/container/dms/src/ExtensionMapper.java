/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:06:07 GMT)
 */
package com.bluejungle.destiny.services.management;


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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ActionTypeDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActionTypeDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "RelationalOpDTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.RelationalOpDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ID".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ID.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOSortTermField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOSortTermField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ServiceNotReadyFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ActionTypeDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActionTypeDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ActivityJournalingAuditLevelDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActivityJournalingAuditLevelDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentQueryResultsDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentTypeDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentTypeDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "title".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ORCompositeAgentDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ORCompositeAgentDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "AgentTypeDTO".equals(typeName)) {
            return com.bluejungle.domain.types.AgentTypeDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentTypeDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentTypeDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentPolicyAssemblyStatusDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentPolicyAssemblyStatusDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "name".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ConcreteAgentDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ConcreteAgentDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-interval-DTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentCount".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentCount.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "RegistrationFailedException".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.RegistrationFailedException.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentStatistics".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentStatistics.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "IDList".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.IDList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentDTOQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentDTOQueryField.Factory.parse(reader);
        }
		
		if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-units".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeUnits.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management".equals(
                    namespaceURI) && "UserProfileDTOQuery".equals(typeName)) {
            return com.bluejungle.destiny.services.management.UserProfileDTOQuery.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ID".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ID.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnknownEntryFault.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ActivityJournalingSettingsDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserProfileInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AgentProfileDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "BaseProfileInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.BaseProfileInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management".equals(
                    namespaceURI) && "CommProfileDTOQuery".equals(typeName)) {
            return com.bluejungle.destiny.services.management.CommProfileDTOQuery.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserProfileDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserProfileDTOQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileDTOQueryTermSet.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryTermSet.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AgentProfileDTOQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AgentProfileDTOQueryField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "AgentTypeDTO".equals(typeName)) {
            return com.bluejungle.domain.types.AgentTypeDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CommProfileDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationProcess".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationProcess.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserProfileDTOQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileDTOQueryField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CommProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "BaseProfileDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.BaseProfileDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "ActionTypeDTO".equals(typeName)) {
            return com.bluejungle.domain.types.ActionTypeDTO.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserProfileDTOQueryFieldType".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileDTOQueryFieldType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CommProfileDTOQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-interval-DTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management".equals(
                    namespaceURI) && "AgentProfileDTOQuery".equals(typeName)) {
            return com.bluejungle.destiny.services.management.AgentProfileDTOQuery.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CommProfileDTOQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CommProfileDTOQueryField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserProfileDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserProfileDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "IDList".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.IDList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/domain/types".equals(namespaceURI) &&
                "ActionTypeDTOList".equals(typeName)) {
            return com.bluejungle.domain.types.ActionTypeDTOList.Factory.parse(reader);
        }
		
		if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "pushList_type0".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.PushList_type0.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DeploymentInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DeploymentInfo.Factory.parse(reader);
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
		
		if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "IndexesRebuildOperation".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.IndexesRebuildOperation.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserRepositoryConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserRepositoryConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CustomObligation".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligation.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ActionListConfig".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActionListConfig.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ConnectionPoolConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ConnectionPoolConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ExternalDomainConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ExternalDomainConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "RepositoryConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.RepositoryConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CustomAttributes".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomAttributes.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AuthenticatorConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AuthenticatorConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "GenericConfigurations".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.GenericConfigurations.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "RegularExpressionDef".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.RegularExpressionDef.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CustomObligationRunBy".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligationRunBy.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DEMConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DEMConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/version/types".equals(namespaceURI) &&
                "Version".equals(typeName)) {
            return com.bluejungle.version.types.Version.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CustomObligationArgument".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligationArgument.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CriticalUpdateLagTime".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CriticalUpdateLagTime.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DCCConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DCCConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "EventRegistrationInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.EventRegistrationInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ConnectionPoolConfigurationList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ConnectionPoolConfigurationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "AuthenticationMode".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AuthenticationMode.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ActionShortNameType".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActionShortNameType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "RepositoryConfigurationList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.RepositoryConfigurationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DCCRegistrationStatus".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DCCRegistrationStatus.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CustomObligations".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligations.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "RegistrationFailedException".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.RegistrationFailedException.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ActionConfig".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActionConfig.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "Cookie".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.Cookie.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DayOfMonth".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DayOfMonth.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "MessageHandlers".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.MessageHandlers.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "GenericConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.GenericConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CustomObligationArgumentValue".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligationArgumentValue.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ResourceAttributeDef".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ResourceAttributeDef.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "Property".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.Property.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DestinyConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DestinyConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "Component".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.Component.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "RegularExpressions".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.RegularExpressions.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ServiceNotReadyFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DMSConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DMSConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ComponentHeartbeatInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ComponentHeartbeatInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ApplicationUserConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationUserConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ComponentList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ComponentList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ComponentHeartbeatUpdate".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ComponentHeartbeatUpdate.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderAliasList".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderAliasList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "DCCRegistrationInformation".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DCCRegistrationInformation.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "SyncOperation".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.SyncOperation.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "MessageHandler".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.MessageHandler.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "FileSystemLogConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.FileSystemLogConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ReporterConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ReporterConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ApplicationResourceList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationResourceList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DABSConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DABSConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "PropertyList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.PropertyList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ActivityJournalSettingConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ActivityJournalSettingConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "BaseConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.BaseConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderAliases".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderAliases.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "CustomObligationRunAt".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligationRunAt.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DMSRegistrationOutcome".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DMSRegistrationOutcome.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "DCCRegistrationInfoList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DCCRegistrationInfoList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "TrustedDomainsConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.TrustedDomainsConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ArchiveOperation".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ArchiveOperation.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "MgmtConsoleConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.MgmtConsoleConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DaysOfMonth".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DaysOfMonth.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserAccessConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserAccessConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DCSFConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DCSFConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DACConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DACConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "CustomObligationArguments".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.CustomObligationArguments.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "DPSConfiguration".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DPSConfiguration.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/shared_folder".equals(
                    namespaceURI) && "SharedFolderData".equals(typeName)) {
            return com.bluejungle.destiny.types.shared_folder.SharedFolderData.Factory.parse(reader);
        }
		
		if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationContextInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationContextInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationInfoList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationInfoList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationInfoQuery".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationInfoQuery.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ApplicationInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ApplicationCollectionInfoList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationCollectionInfoList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ApplicationCollectionInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ApplicationCollectionInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ServiceNotReadyFault.Factory.parse(reader);
        }
		
		if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-units".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeUnits.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "AgentTypeEnum".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.AgentTypeEnum.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "AgentCapability".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.AgentCapability.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSUserData".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSUserData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ID".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ID.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) &&
                "DefaultAccessAssignment".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserManagementMetadata".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserManagementMetadata.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) &&
                "DefaultAccessAssignmentList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSRoleData".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSRoleData.Factory.parse(reader);
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
                    namespaceURI) &&
                "AuthenticationModeEnumDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AuthenticationModeEnumDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DeploymentRequest".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DeploymentRequest.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSRoleDataList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSRoleDataList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "title".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Role".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Role.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserRoleServiceException".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserRoleServiceException.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Access".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Access.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserDTOQueryResults".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserDTOQueryResults.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "name".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SubjectDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SubjectDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserQueryTermSet.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Component".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Component.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SystemUser".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SystemUser.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-interval-DTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SubjectDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SubjectDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "DuplicateLoginNameException".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DuplicateLoginNameException.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "IDList".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.IDList.Factory.parse(reader);
        }
		
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

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "CommitFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.CommitFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ID".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ID.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserManagementMetadata".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserManagementMetadata.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ExternalUserGroup".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ExternalUserGroup.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSRoleData".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSRoleData.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "AuthenticationModeEnumDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.AuthenticationModeEnumDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DeploymentRequest".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DeploymentRequest.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UnauthorizedCallerFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UnauthorizedCallerFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Role".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Role.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "UserRoleServiceException".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserRoleServiceException.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Access".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Access.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "name".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "BadArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.BadArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SubjectDTOList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SubjectDTOList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserQueryTermSet.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SystemUser".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SystemUser.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "time-interval-DTO".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupReduced".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupReduced.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupQueryField".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupQueryField.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "ExternalUserGroupList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ExternalUserGroupList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "SubjectDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.SubjectDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "IDList".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.IDList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "AgentTypeEnum".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.AgentTypeEnum.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupDTO".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupDTO.Factory.parse(reader);
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
                    namespaceURI) && "UserGroupQueryResults".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupQueryResults.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) &&
                "DefaultAccessAssignment".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.ServiceNotReadyFault.Factory.parse(reader);
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

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "DMSRoleDataList".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.DMSRoleDataList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupReducedList".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupReducedList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "ExternalUserGroupQueryResults".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.ExternalUserGroupQueryResults.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/framework/types".equals(namespaceURI) &&
                "title".equals(typeName)) {
            return com.bluejungle.destiny.framework.types.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserDTOQueryResults".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserDTOQueryResults.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupQueryTermSet".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupQueryTermSet.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/policy/types".equals(
                    namespaceURI) && "Component".equals(typeName)) {
            return com.bluejungle.destiny.services.policy.types.Component.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupInfo".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupInfo.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) && "UserGroupQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.UserGroupQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/services/management/types".equals(
                    namespaceURI) &&
                "DuplicateLoginNameException".equals(typeName)) {
            return com.bluejungle.destiny.services.management.types.DuplicateLoginNameException.Factory.parse(reader);
        }

        throw new org.apache.axis2.databinding.ADBException("Unsupported type " +
            namespaceURI + " " + typeName);
    }
}
