/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:06:07 GMT)
 */
package com.bluejungle.destiny.services.report.v1;


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
        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "DocumentActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "AccessDeniedFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Threshold".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Threshold.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortTermList.Factory.parse(reader);
        }

        if ("http://nextlabs.com/destiny/types/custom_attr/v1".equals(
                    namespaceURI) && "CustomAttributeList".equals(typeName)) {
            return com.nextlabs.destiny.types.custom_attr.v1.CustomAttributeList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "User".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.User.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClass".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClass.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "TimeUnits".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.TimeUnits.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/effects/v1".equals(
                    namespaceURI) && "EffectList".equals(typeName)) {
            return com.bluejungle.destiny.types.effects.v1.EffectList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/actions/v1".equals(
                    namespaceURI) && "ActionList".equals(typeName)) {
            return com.bluejungle.destiny.types.actions.v1.ActionList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Name".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "SummaryResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.SummaryResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/obligations/v1".equals(
                    namespaceURI) && "ObligationList".equals(typeName)) {
            return com.bluejungle.destiny.types.obligations.v1.ObligationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "LogDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.LogDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "PersistenceFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.PersistenceFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClass".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClass.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "PolicyActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Id".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Id.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/actions/v1".equals(
                    namespaceURI) && "ActionType".equals(typeName)) {
            return com.bluejungle.destiny.types.actions.v1.ActionType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "DocumentActivityCustomResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DocumentActivityCustomResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/obligations/v1".equals(
                    namespaceURI) && "ObligationType".equals(typeName)) {
            return com.bluejungle.destiny.types.obligations.v1.ObligationType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "Policy".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.Policy.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "PropertyValues".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.PropertyValues.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Property".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Property.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportSummaryResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportSummaryResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortSpec".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortSpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "SortDirection".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.SortDirection.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportState".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportState.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Properties".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Properties.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSummaryType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSummaryType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "TimeIntervalDTO".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortSpec".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortSpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportVisibilityType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportVisibilityType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/effects/v1".equals(
                    namespaceURI) && "EffectType".equals(typeName)) {
            return com.bluejungle.destiny.types.effects.v1.EffectType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportTargetType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportTargetType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Title".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "DetailResultList".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DetailResultList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "SummaryResultList".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.SummaryResultList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "StringList".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.StringList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "PolicyActivityCustomResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.PolicyActivityCustomResult.Factory.parse(reader);
        }

        if ("http://nextlabs.com/destiny/types/custom_attr/v1".equals(
                    namespaceURI) && "CustomAttribute".equals(typeName)) {
            return com.nextlabs.destiny.types.custom_attr.v1.CustomAttribute.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "Report".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.Report.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortFieldName.Factory.parse(reader);
        }
		
		if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "DocumentActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "AccessDeniedFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Threshold".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Threshold.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortTermList.Factory.parse(reader);
        }

        if ("http://nextlabs.com/destiny/types/custom_attr/v1".equals(
                    namespaceURI) && "CustomAttributeList".equals(typeName)) {
            return com.nextlabs.destiny.types.custom_attr.v1.CustomAttributeList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "User".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.User.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClass".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClass.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "TimeUnits".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.TimeUnits.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/effects/v1".equals(
                    namespaceURI) && "EffectList".equals(typeName)) {
            return com.bluejungle.destiny.types.effects.v1.EffectList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/actions/v1".equals(
                    namespaceURI) && "ActionList".equals(typeName)) {
            return com.bluejungle.destiny.types.actions.v1.ActionList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Name".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "SummaryResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.SummaryResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/obligations/v1".equals(
                    namespaceURI) && "ObligationList".equals(typeName)) {
            return com.bluejungle.destiny.types.obligations.v1.ObligationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "LogDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.LogDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "PersistenceFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.PersistenceFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClass".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClass.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "PolicyActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Id".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Id.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/actions/v1".equals(
                    namespaceURI) && "ActionType".equals(typeName)) {
            return com.bluejungle.destiny.types.actions.v1.ActionType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ExecutionFault".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ExecutionFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "InvalidArgumentFault".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.InvalidArgumentFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "DocumentActivityCustomResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DocumentActivityCustomResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/obligations/v1".equals(
                    namespaceURI) && "ObligationType".equals(typeName)) {
            return com.bluejungle.destiny.types.obligations.v1.ObligationType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "Policy".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.Policy.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "PropertyValues".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.PropertyValues.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Property".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Property.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportSummaryResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportSummaryResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortSpec".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortSpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "SortDirection".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.SortDirection.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportState".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportState.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Properties".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Properties.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSummaryType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSummaryType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "TimeIntervalDTO".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortSpec".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortSpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportVisibilityType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportVisibilityType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/effects/v1".equals(
                    namespaceURI) && "EffectType".equals(typeName)) {
            return com.bluejungle.destiny.types.effects.v1.EffectType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportTargetType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportTargetType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Title".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "DetailResultList".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DetailResultList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "SummaryResultList".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.SummaryResultList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "StringList".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.StringList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "PolicyActivityCustomResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.PolicyActivityCustomResult.Factory.parse(reader);
        }

        if ("http://nextlabs.com/destiny/types/custom_attr/v1".equals(
                    namespaceURI) && "CustomAttribute".equals(typeName)) {
            return com.nextlabs.destiny.types.custom_attr.v1.CustomAttribute.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "Report".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.Report.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortFieldName.Factory.parse(reader);
        }
		
		if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "DocumentActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "AccessDeniedFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Threshold".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Threshold.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostSortTermList.Factory.parse(reader);
        }

        if ("http://nextlabs.com/destiny/types/custom_attr/v1".equals(
                    namespaceURI) && "CustomAttributeList".equals(typeName)) {
            return com.nextlabs.destiny.types.custom_attr.v1.CustomAttributeList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "User".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.User.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClass".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClass.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) &&
                "UniqueConstraintViolationFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.UniqueConstraintViolationFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "TimeUnits".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.TimeUnits.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/effects/v1".equals(
                    namespaceURI) && "EffectList".equals(typeName)) {
            return com.bluejungle.destiny.types.effects.v1.EffectList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/actions/v1".equals(
                    namespaceURI) && "ActionList".equals(typeName)) {
            return com.bluejungle.destiny.types.actions.v1.ActionList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Name".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Name.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "SummaryResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.SummaryResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/obligations/v1".equals(
                    namespaceURI) && "ObligationList".equals(typeName)) {
            return com.bluejungle.destiny.types.obligations.v1.ObligationList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "LogDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.LogDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "PersistenceFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.PersistenceFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClass".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClass.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "PolicyActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Id".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Id.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/actions/v1".equals(
                    namespaceURI) && "ActionType".equals(typeName)) {
            return com.bluejungle.destiny.types.actions.v1.ActionType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "Host".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.Host.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostList".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "DocumentActivityCustomResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DocumentActivityCustomResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/obligations/v1".equals(
                    namespaceURI) && "ObligationType".equals(typeName)) {
            return com.bluejungle.destiny.types.obligations.v1.ObligationType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "UnknownEntryFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.UnknownEntryFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "Policy".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.Policy.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "PropertyValues".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.PropertyValues.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Property".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Property.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportSummaryResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportSummaryResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortSpec".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortSpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "SortDirection".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.SortDirection.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportState".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportState.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Properties".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Properties.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSummaryType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSummaryType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "TimeIntervalDTO".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.TimeIntervalDTO.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortSpec".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortSpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportVisibilityType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportVisibilityType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) && "ResourceClassQuerySpec".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassQuerySpec.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicySortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicySortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserSortTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserSortTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/hosts/v1".equals(namespaceURI) &&
                "HostSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.hosts.v1.HostSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/policies/v1".equals(
                    namespaceURI) && "PolicyList".equals(typeName)) {
            return com.bluejungle.destiny.types.policies.v1.PolicyList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/effects/v1".equals(
                    namespaceURI) && "EffectType".equals(typeName)) {
            return com.bluejungle.destiny.types.effects.v1.EffectType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic_faults/v1".equals(
                    namespaceURI) && "ServiceNotReadyFault".equals(typeName)) {
            return com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportTargetType".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportTargetType.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "Title".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.Title.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "DetailResultList".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.DetailResultList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportSortFieldName.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ReportResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ReportResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "SummaryResultList".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.SummaryResultList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/basic/v1".equals(namespaceURI) &&
                "StringList".equals(typeName)) {
            return com.bluejungle.destiny.types.basic.v1.StringList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) && "ActivityDetailResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.ActivityDetailResult.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "ReportQueryTerm".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.ReportQueryTerm.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report_result/v1".equals(
                    namespaceURI) &&
                "PolicyActivityCustomResult".equals(typeName)) {
            return com.bluejungle.destiny.types.report_result.v1.PolicyActivityCustomResult.Factory.parse(reader);
        }

        if ("http://nextlabs.com/destiny/types/custom_attr/v1".equals(
                    namespaceURI) && "CustomAttribute".equals(typeName)) {
            return com.nextlabs.destiny.types.custom_attr.v1.CustomAttribute.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/users/v1".equals(namespaceURI) &&
                "UserClassQueryTermList".equals(typeName)) {
            return com.bluejungle.destiny.types.users.v1.UserClassQueryTermList.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/report/v1".equals(namespaceURI) &&
                "Report".equals(typeName)) {
            return com.bluejungle.destiny.types.report.v1.Report.Factory.parse(reader);
        }

        if ("http://bluejungle.com/destiny/types/resources/v1".equals(
                    namespaceURI) &&
                "ResourceClassSortFieldName".equals(typeName)) {
            return com.bluejungle.destiny.types.resources.v1.ResourceClassSortFieldName.Factory.parse(reader);
        }

        throw new org.apache.axis2.databinding.ADBException("Unsupported type " +
            namespaceURI + " " + typeName);
    }
}
