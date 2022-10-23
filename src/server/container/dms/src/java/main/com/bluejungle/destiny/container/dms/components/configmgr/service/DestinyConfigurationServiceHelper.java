/*
 * Created on Feb 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.dms.components.configmgr.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.axis2.databinding.types.NonNegativeInteger;
import org.apache.axis2.databinding.types.PositiveInteger;
import org.apache.axis2.databinding.types.URI;

import com.bluejungle.destiny.server.shared.configuration.IActionConfigDO;
import com.bluejungle.destiny.server.shared.configuration.IActionListConfigDO;
import com.bluejungle.destiny.server.shared.configuration.IApplicationUserConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IAuthenticatorConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IConnectionPoolConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.ICustomObligationArgumentDO;
import com.bluejungle.destiny.server.shared.configuration.ICustomObligationConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.ICustomObligationsConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDABSComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDACComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDCCComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDCSFComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDEMComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDMSComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IDPSComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IExternalDomainConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IFileSystemLogConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IGenericComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IMessageHandlerConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IMessageHandlersConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IMgmtConsoleComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IRegularExpressionConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IReporterComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IActivityJournalSettingConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IRepositoryConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IResourceAttributeConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.ITrustedDomainsConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IUserAccessConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.IUserRepositoryConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.impl.DaysOfMonthDO;
import com.bluejungle.destiny.server.shared.configuration.impl.DaysOfWeekDO;
import com.bluejungle.destiny.services.management.types.ActionConfig;
import com.bluejungle.destiny.services.management.types.ActionListConfig;
import com.bluejungle.destiny.services.management.types.ActionShortNameType;
import com.bluejungle.destiny.services.management.types.ActivityJournalSettingConfiguration;
import com.bluejungle.destiny.services.management.types.ApplicationUserConfiguration;
import com.bluejungle.destiny.services.management.types.ArchiveOperation;
import com.bluejungle.destiny.services.management.types.AuthenticationMode;
import com.bluejungle.destiny.services.management.types.AuthenticatorConfiguration;
import com.bluejungle.destiny.services.management.types.ConnectionPoolConfigurationList;
import com.bluejungle.destiny.services.management.types.ConnectionPoolConfiguration;
import com.bluejungle.destiny.services.management.types.CustomAttributes;
import com.bluejungle.destiny.services.management.types.CustomObligation;
import com.bluejungle.destiny.services.management.types.CustomObligationArgument;
import com.bluejungle.destiny.services.management.types.CustomObligationArgumentValue;
import com.bluejungle.destiny.services.management.types.CustomObligationArguments;
import com.bluejungle.destiny.services.management.types.CustomObligationChoice_type0;
import com.bluejungle.destiny.services.management.types.CustomObligationRunAt;
import com.bluejungle.destiny.services.management.types.CustomObligationRunBy;
import com.bluejungle.destiny.services.management.types.CustomObligations;
import com.bluejungle.destiny.services.management.types.CriticalUpdateLagTime;
import com.bluejungle.destiny.services.management.types.DABSConfiguration;
import com.bluejungle.destiny.services.management.types.DACConfiguration;
import com.bluejungle.destiny.services.management.types.DCCConfiguration;
import com.bluejungle.destiny.services.management.types.DCSFConfiguration;
import com.bluejungle.destiny.services.management.types.DEMConfiguration;
import com.bluejungle.destiny.services.management.types.DMSConfiguration;
import com.bluejungle.destiny.services.management.types.DPSConfiguration;
import com.bluejungle.destiny.services.management.types.DaysOfMonth;
import com.bluejungle.destiny.services.management.types.ExternalDomainConfiguration;
import com.bluejungle.destiny.services.management.types.FileSystemLogConfiguration;
import com.bluejungle.destiny.services.management.types.GenericConfiguration;
import com.bluejungle.destiny.services.management.types.IndexesRebuildOperation;
import com.bluejungle.destiny.services.management.types.MessageHandler;
import com.bluejungle.destiny.services.management.types.MessageHandlers;
import com.bluejungle.destiny.services.management.types.MgmtConsoleConfiguration;
import com.bluejungle.destiny.services.management.types.Property;
import com.bluejungle.destiny.services.management.types.PropertyList;
import com.bluejungle.destiny.services.management.types.RegularExpressions;
import com.bluejungle.destiny.services.management.types.RegularExpressionDef;
import com.bluejungle.destiny.services.management.types.ReporterConfiguration;
import com.bluejungle.destiny.services.management.types.RepositoryConfiguration;
import com.bluejungle.destiny.services.management.types.RepositoryConfigurationChoice_type0;
import com.bluejungle.destiny.services.management.types.RepositoryConfigurationList;
import com.bluejungle.destiny.services.management.types.ResourceAttributeDef;
import com.bluejungle.destiny.services.management.types.SyncOperation;
import com.bluejungle.destiny.services.management.types.TrustedDomainsConfiguration;
import com.bluejungle.destiny.services.management.types.UserAccessConfiguration;
import com.bluejungle.destiny.services.management.types.UserRepositoryConfiguration;
import com.bluejungle.framework.utils.CollectionUtils;
import com.bluejungle.framework.utils.StringUtils;

/**
 * This is a utility class that converts DOs to DTOs to be sent over a
 * webservice.
 * 
 * @author safdar
 */

public class DestinyConfigurationServiceHelper {

    private static final DestinyConfigurationServiceHelper SINGLETON_INSTANCE = new DestinyConfigurationServiceHelper();

    /**
     * Constructor
     *  
     */
    private DestinyConfigurationServiceHelper() {
        super();
    }

    /**
     * Returns a singleton instance
     * 
     * @return singleton
     */
    public static final DestinyConfigurationServiceHelper getInstance() {
        return SINGLETON_INSTANCE;
    }

    /**
     * Extracts DCC component configuration from the DO
     * 
     * @param configuration
     * @return dcc component configuration DTO
     */
    public DCCConfiguration extractDCCComponentConfigurationData(IDCCComponentConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        final DCCConfiguration data;
        if (configuration instanceof IDCSFComponentConfigurationDO) {
            data = extractDCSFComponentConfigurationData((IDCSFComponentConfigurationDO)configuration);
        } else if (configuration instanceof IDMSComponentConfigurationDO) {
            data = extractDMSComponentConfigurationData((IDMSComponentConfigurationDO)configuration);
        } else if (configuration instanceof IDABSComponentConfigurationDO) {
            data = extractDABSComponentConfigurationData((IDABSComponentConfigurationDO)configuration);
        } else if (configuration instanceof IDACComponentConfigurationDO) {
            data = extractDACComponentConfigurationData((IDACComponentConfigurationDO)configuration);
        } else if (configuration instanceof IDEMComponentConfigurationDO) {
            data = extractDEMComponentConfigurationData((IDEMComponentConfigurationDO)configuration);
        } else if (configuration instanceof IDPSComponentConfigurationDO) {
            data = extractDPSComponentConfigurationData((IDPSComponentConfigurationDO)configuration);
        } else if (configuration instanceof IMgmtConsoleComponentConfigurationDO) {
            data = extractMgmtConsoleComponentConfigurationData((IMgmtConsoleComponentConfigurationDO)configuration);
        } else if (configuration instanceof IReporterComponentConfigurationDO) {
            data = extractReporterComponentConfigurationData((IReporterComponentConfigurationDO)configuration);
        } else if (configuration instanceof IGenericComponentConfigurationDO) {
            data = extractGenericComponentConfigurationData((IGenericComponentConfigurationDO)configuration);
        }else{
            throw new IllegalArgumentException("Unrecongized class " + configuration.getClass());
        }

        CriticalUpdateLagTime lagTime = new CriticalUpdateLagTime();
        lagTime.setCriticalUpdateLagTime(getPositiveInteger(configuration.getHeartbeatInterval()));
        data.setHeartbeatRate(lagTime);
        data.setProperties(extractPropertyListData(configuration.getProperties()));
        return data;
    }
    
    private static PositiveInteger getPositiveInteger(int i){
        return new PositiveInteger(new Integer(i).toString());
     
    }
    
    private DCSFConfiguration extractDCSFComponentConfigurationData(
        IDCSFComponentConfigurationDO configuration) {
        return new DCSFConfiguration();
    }
    
    private DMSConfiguration extractDMSComponentConfigurationData(
        IDMSComponentConfigurationDO configuration) {
        return new DMSConfiguration();
    }

    private DABSConfiguration extractDABSComponentConfigurationData(
        IDABSComponentConfigurationDO configuration) {
        DABSConfiguration data = new DABSConfiguration();
        data.setTrustedDomainsConfiguration(extractTrustedDomainsConfigurationData(configuration.getTrustedDomainsConfiguration()));
        data.setFileSystemLogConfiguration(extractFileSystemLogConfigurationData(configuration.getFileSystemLogConfiguration()));

        IRegularExpressionConfigurationDO[] regexps = configuration.getRegularExpressions();
        RegularExpressionDef[] regexpsDefs = new RegularExpressionDef[regexps.length];
        for (int i = 0 ; i != regexps.length ; i++) {

            RegularExpressionDef def = new RegularExpressionDef();
            def.setName(regexps[i].getName());
            def.setValue(regexps[i].getValue());

            regexpsDefs[i] = def;
        }

        RegularExpressions regularExpressions = new RegularExpressions();
        regularExpressions.setRegexp(regexpsDefs);
        
        data.setRegexps(regularExpressions);
        return data;
    }
 
    private DACConfiguration extractDACComponentConfigurationData(
        IDACComponentConfigurationDO configuration) {
        DACConfiguration data = new DACConfiguration();
  
        //set reporter sync configuration
        ActivityJournalSettingConfiguration reporterSyncConfig = new ActivityJournalSettingConfiguration();
        IActivityJournalSettingConfigurationDO reporterSyncConfigDo = configuration.getActivityJournalSettingConfiguration();

        //temp variable
        Long l;
        Calendar c;
        DaysOfWeekDO daysOfWeekDO;
        DaysOfMonthDO daysOfMonthDO;

        //set sync operation
        SyncOperation syncOperation = new SyncOperation();
        if ((l = reporterSyncConfigDo.getSyncTimeInterval()) != null) {
            syncOperation.setTimeInterval(Long.toString(l));
        }
        if ((c = reporterSyncConfigDo.getSyncTimeOfDay()) != null) {
            syncOperation.setTimeOfDay(Long.toString(c.getTimeInMillis()));
        }
        syncOperation.setTimeoutInMinutes(reporterSyncConfigDo.getSyncTimeoutInMinutes());
        syncOperation.setDeleteAfterSync(reporterSyncConfigDo.getDeleteAfterSync());
        
        reporterSyncConfig.setSyncOperation(syncOperation);

        //set index rebuild operation
        IndexesRebuildOperation indexesRebuildOperation = new IndexesRebuildOperation();
        if ((c = reporterSyncConfigDo.getIndexRebuildTimeOfDay()) != null) {
            indexesRebuildOperation.setTimeOfDay(Long.toString(c.getTimeInMillis()));
        }
        if ((daysOfWeekDO = reporterSyncConfigDo.getIndexRebuildDaysOfWeek()) != null) {
            indexesRebuildOperation.setDaysOfWeek(convertDaysOfWeek(daysOfWeekDO));
        }
        if ((daysOfMonthDO = reporterSyncConfigDo.getIndexRebuildDaysOfMonth()) != null) {
            indexesRebuildOperation.setDaysOfMonth(convertDaysOfMonth(daysOfMonthDO));
        }
        indexesRebuildOperation.setAutoRebuildIndexes(reporterSyncConfigDo.getIndexRebuildAutoRebuildIndexes());
        indexesRebuildOperation.setTimeoutInMinutes(reporterSyncConfigDo.getIndexRebuildTimeoutInMinutes());
        reporterSyncConfig.setIndexesRebuildOperation(indexesRebuildOperation);

        //set archive operation
        ArchiveOperation archiveOperation = new ArchiveOperation();
        if ((c = reporterSyncConfigDo.getArchiveTimeOfDay()) != null) {
            archiveOperation.setTimeOfDay(Long.toString(c.getTimeInMillis()));
        }
        if ((daysOfWeekDO = reporterSyncConfigDo.getArchiveDaysOfWeek()) != null) {
            archiveOperation.setDaysOfWeek(convertDaysOfWeek(daysOfWeekDO));
        }
        if ((daysOfMonthDO = reporterSyncConfigDo.getArchiveDaysOfMonth()) != null) {
            archiveOperation.setDaysOfMonth(convertDaysOfMonth(daysOfMonthDO));
        }
        archiveOperation.setDaysOfDataToKeep(reporterSyncConfigDo.getArchiveDaysOfDataToKeep());
        archiveOperation.setAutoArchive(reporterSyncConfigDo.getArchiveAutoArchive());
        archiveOperation.setTimeoutInMinutes(reporterSyncConfigDo.getIndexRebuildTimeoutInMinutes());
        reporterSyncConfig.setArchiveOperation(archiveOperation);

        data.setActivityJournalSettingConfiguration(reporterSyncConfig);

        return data;
    }
 
    private String convertDaysOfWeek(DaysOfWeekDO daysOfWeekDo){
        BitSet bits = daysOfWeekDo.getDaysOfWeek();
        List<String> daysOfWeek = new ArrayList<String>(7); 
        for (int i = 1; i <= 7; i++) {
            if (bits.get(i-1)) {
                daysOfWeek.add(Integer.toString(i));
            }
        }

        return StringUtils.join(daysOfWeek, ",");
    }
 
    private String convertDaysOfMonth(DaysOfMonthDO daysOfMonthDo){
        BitSet bits = daysOfMonthDo.getDaysOfMonth();
        List<String> daysOfMonth = new ArrayList<String>(31); 
        for (int i = 1; i <= 31; i++) {
            if (bits.get(i-1)) {
                daysOfMonth.add(Byte.toString((byte)i));
            }
        }

        return StringUtils.join(daysOfMonth, ",");
    }
 
    private DEMConfiguration extractDEMComponentConfigurationData(
        IDEMComponentConfigurationDO configuration) {
        DEMConfiguration data = new DEMConfiguration();
        Integer refreshRate = configuration.getReporterCacheRefreshRate();
        if (refreshRate != null) {
            data.setReporterCacheRefreshRate(getPositiveInteger(refreshRate));
        }
     
        return data;
    }
 
    private DPSConfiguration extractDPSComponentConfigurationData(
        IDPSComponentConfigurationDO configuration) {
        DPSConfiguration data = new DPSConfiguration();
        int iValue = configuration.getLifecycleManagerGraceWindow();
        String sValue = (new Integer(iValue)).toString();
        data.setLifecycleManagerGraceWindow(new BigInteger(sValue));
        data.setDeploymentTime(configuration.getDeploymentTime());

        IResourceAttributeConfigurationDO[] attr = configuration.getCustomResourceAttributes();
        ResourceAttributeDef[] defs = new ResourceAttributeDef[attr.length];
        for (int i = 0 ; i != defs.length ; i++) {

            ResourceAttributeDef resAttrDef = new ResourceAttributeDef();
            resAttrDef.setGroup(attr[i].getGroupName());
            resAttrDef.setDisplayName(attr[i].getDisplayName());
            resAttrDef.setName(attr[i].getPqlName());
            resAttrDef.setType(attr[i].getTypeName());
            resAttrDef.setAttribute(attr[i].getAttributes().toArray(new String[attr[i].getAttributes().size()]));
            resAttrDef.setValue( attr[i].getEnumeratedValues().toArray(new String[attr[i].getEnumeratedValues().size()]));
            
            defs[i] = resAttrDef;
        }

        CustomAttributes customAttributes = new CustomAttributes();
        customAttributes.setResourceAttribute(defs);
        
        data.setCustomAttributes(customAttributes);
        return data;
    }
 
    private MgmtConsoleConfiguration extractMgmtConsoleComponentConfigurationData(
        IMgmtConsoleComponentConfigurationDO configuration) {
        return new MgmtConsoleConfiguration();
    }
 
    private ReporterConfiguration extractReporterComponentConfigurationData(IReporterComponentConfigurationDO configuration) {
        ReporterConfiguration data = new ReporterConfiguration();
        
        Number tempNum;
        if ((tempNum = configuration.getReportGenerationFrequency()) != null) {
            data.setReportGenerationFrequency(new NonNegativeInteger(tempNum.toString()));
        }
        return data;
    }
 
    private GenericConfiguration extractGenericComponentConfigurationData(
        IGenericComponentConfigurationDO configuration
                                                                          ) {
        GenericConfiguration data = new GenericConfiguration();
        data.setName(configuration.getComponentType().getName());
        return data;
    }

    private static TrustedDomainsConfiguration extractTrustedDomainsConfigurationData( ITrustedDomainsConfigurationDO configuration ) {
        if (configuration == null) {
            return null;
        }

        TrustedDomainsConfiguration trusted = new TrustedDomainsConfiguration();
        trusted.setMutuallyTrusted(configuration.getTrustedDomains());

        return trusted;
    }
    
    private static FileSystemLogConfiguration extractFileSystemLogConfigurationData(
        IFileSystemLogConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        } 
  
        FileSystemLogConfiguration data = new FileSystemLogConfiguration();
        Number tempNum;
        if ((tempNum = configuration.getThreadPoolMaximumSize()) != null) {
            data.setThreadPoolMaximumSize(new PositiveInteger(tempNum.toString()));
        }
        if ((tempNum = configuration.getThreadPoolKeepAliveTime()) != null) {
            data.setThreadPoolKeepAliveTime(new NonNegativeInteger(tempNum.toString()));
        }
        if ((tempNum = configuration.getLogInsertTaskIdleTime()) != null) {
            data.setLogInsertTaskIdleTime(new NonNegativeInteger(tempNum.toString()));
        }
        if ((tempNum = configuration.getLogTimeout()) != null) {
            data.setLogTimeout(new NonNegativeInteger(tempNum.toString()));
        }
        if ((tempNum = configuration.getTimeoutCheckerFrequency()) != null) {
            data.setTimeoutCheckerFrequency(new NonNegativeInteger(tempNum.toString()));
        }
        if ((tempNum = configuration.getQueueManagerUploadSize()) != null) {
            data.setQueueManagerUploadSize(new PositiveInteger(tempNum.toString()));
        }
        if ((tempNum = configuration.getMaxHandleFileSizePerThread()) != null) {
            data.setMaxHandleFileSizePerThread(new PositiveInteger(tempNum.toString()));
        }
        return data;
  
    }
    
    /**
     * Extracts auth configuration from the DO
     * 
     * @param configuration
     * @return auth configuration DTO
     */
    public ApplicationUserConfiguration extractApplicationUserConfigurationData(IApplicationUserConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        ApplicationUserConfiguration data = new ApplicationUserConfiguration();
        data.setAuthenticationMode(AuthenticationMode.Factory.fromValue(configuration.getAuthenticationMode()));

        UserRepositoryConfiguration userRepositoryConfiguration = extractUserRepositoryConfigurationData(configuration.getUserRepositoryConfiguration());
        data.setUserRepositoryConfiguration(userRepositoryConfiguration);

        ExternalDomainConfiguration externalDomainConfiguration = extractExternalDomainConfigurationData(configuration.getExternalDomainConfiguration());
        data.setExternalDomainConfiguration(externalDomainConfiguration);
        return data;
    }

    private PropertyList extractPropertyListData(Properties props) {
        if (props == null) {
            return null;
        }
        int size = props.size();
        Property[] propArray = new Property[size];
        int i = 0;
        for (Map.Entry<Object,Object> e : props.entrySet()) {
            Property property = new Property();
            property.setName((String)e.getKey());
            property.setValue((String)e.getValue());

            propArray[i++] = property;
        }
        PropertyList properties = new PropertyList();
        properties.setProperty(propArray);
        return properties;
    }

    private UserRepositoryConfiguration extractUserRepositoryConfigurationData(IUserRepositoryConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        UserRepositoryConfiguration data = new UserRepositoryConfiguration();
        data.setProviderClassName(configuration.getProviderClassName());
        data.setProperties(extractPropertyListData(configuration.getProperties()));
        return data;
    }

    private ExternalDomainConfiguration extractExternalDomainConfigurationData(IExternalDomainConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        ExternalDomainConfiguration data = new ExternalDomainConfiguration();
        data.setDomainName(configuration.getDomainName());
        data.setAuthenticatorConfiguration(extractAuthenticatorConfigurationData(configuration.getAuthenticatorConfiguration()));
        data.setUserAccessConfiguration(extractUserAccessConfigurationData(configuration.getUserAccessConfiguration()));
        return data;
    }

    private AuthenticatorConfiguration extractAuthenticatorConfigurationData(IAuthenticatorConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        AuthenticatorConfiguration data = new AuthenticatorConfiguration();
        data.setAuthenticatorClassName(configuration.getAuthenticatorClassName());
        data.setProperties(extractPropertyListData(configuration.getProperties()));
        return data;
    }

    public UserAccessConfiguration extractUserAccessConfigurationData(IUserAccessConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        UserAccessConfiguration data = new UserAccessConfiguration();
        data.setUserAccessProviderClassName(configuration.getProviderClassName());
        data.setProperties(extractPropertyListData(configuration.getProperties()));
        return data;
    }
    
    /**
     * Extracts a repository configuration list from the corresponding DO set
     * 
     * @param configurations
     * @return repository list DTO
     */
    public RepositoryConfigurationList extractRepositoryList(Set<? extends IRepositoryConfigurationDO> configurations) {
        if (configurations == null) {
            return null;
        }
     
        RepositoryConfigurationList list = new RepositoryConfigurationList();
        RepositoryConfiguration[] dsArray = new RepositoryConfiguration[configurations.size()];
        int i = 0;
        for (IRepositoryConfigurationDO dsConfig : configurations) {
            dsArray[i++] = this.extractRepositoryData(dsConfig);
        }
        list.setRepository(dsArray);
        return list;
    }

    /**
     * Extracts a repository configuration from the corresponding DO
     * 
     * @param configuration
     * @return
     */
    public RepositoryConfiguration extractRepositoryData(IRepositoryConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        RepositoryConfiguration data = new RepositoryConfiguration();
        data.setName(configuration.getName());

        RepositoryConfigurationChoice_type0 poolChoice = new RepositoryConfigurationChoice_type0();
        poolChoice.setConnectionPoolConfiguration(extractConnectionPoolData(configuration.getConnectionPoolConfiguration()));

        data.setRepositoryConfigurationChoice_type0(poolChoice);
        
        // Set the properties here:
        PropertyList propOverridesList = null;
        Properties props = configuration.getProperties();
        if (props != null) {
            int size = props.size();
            Property[] propOverridesArr = new Property[size];
            propOverridesList = new PropertyList();
            int i = 0;
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                Property property = new Property();
                property.setName((String)e.getKey());
                property.setValue((String)e.getValue());
                propOverridesArr[i++] = property;
            }
            propOverridesList.setProperty(propOverridesArr);
        }
        data.setProperties(propOverridesList);
        return data;
    }

    public CustomObligations extractCustomObligationsConfigurationData(ICustomObligationsConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
        CustomObligations data = new CustomObligations();
        ICustomObligationConfigurationDO[] obligations = configuration.getCustomObligations();
        CustomObligation[] extractedObligations = null;

        if (obligations != null) {
            extractedObligations = new CustomObligation[obligations.length];
            
            for (int i = 0; i < obligations.length; i++) {
                extractedObligations[i] = extractCustomObligationData(obligations[i]);
            }
        }

        data.setObligation(extractedObligations);
        return data;
    }

    public CustomObligation extractCustomObligationData(ICustomObligationConfigurationDO obligation) {
        if (obligation == null) {
            return null;
        }
     
        CustomObligation data = new CustomObligation();
        data.setDisplayName(obligation.getDisplayName());
        data.setRunBy(CustomObligationRunBy.Factory.fromValue(obligation.getRunBy()));
        data.setRunAt(CustomObligationRunAt.Factory.fromValue(obligation.getRunAt()));
        
        CustomObligationChoice_type0 sub = new CustomObligationChoice_type0();
        sub.setName(obligation.getInvocationString());
        sub.setExecPath(obligation.getInvocationString());
        data.setCustomObligationChoice_type0(sub);
        
        data.setArguments(extractCustomObligationArguments(obligation.getArguments()));
        return data;
    }

    public CustomObligationArguments extractCustomObligationArguments(ICustomObligationArgumentDO[] arguments) {
        if (arguments == null) {
            return null;
        }
        CustomObligationArgument[] extractedArgs = new CustomObligationArgument[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            ICustomObligationArgumentDO argument = arguments[i];

            String[] values = argument.getValues();
            String defaultValue = argument.getDefaultValue();
            CustomObligationArgumentValue[] extractedValues = null;

            if (values != null) {
                extractedValues = new CustomObligationArgumentValue[values.length];
                
                for (int j = 0; j < values.length; j++) {
                    CustomObligationArgumentValue value = new CustomObligationArgumentValue();
                    value.setString(values[j]);
                    extractedValues[j] = value;
                    if (values[j].equals(defaultValue)) {
                        extractedValues[j].set_default(true);
                    }
                }
            }

            CustomObligationArgument customObligationArgument = new CustomObligationArgument();
            customObligationArgument.setName(argument.getName());
            customObligationArgument.setValue(extractedValues);
            customObligationArgument.setUsereditable(argument.isArgumentUserEditable());
            customObligationArgument.setHidden(argument.isArgumentHidden());
            
            extractedArgs[i] = customObligationArgument;
        }

        CustomObligationArguments customObligationArguments = new CustomObligationArguments();
        customObligationArguments.setArgument(extractedArgs);

        return customObligationArguments;
    }

    /**
     * Extracts a connection pool configuration from the corresponding DO
     * 
     * @param configuration
     * @return
     */
    private ConnectionPoolConfiguration extractConnectionPoolData(IConnectionPoolConfigurationDO configuration) {
        if (configuration == null) {
            return null;
        }
     
        ConnectionPoolConfiguration data = new ConnectionPoolConfiguration();
        data.setName(configuration.getName());
        data.setUsername(configuration.getUserName());
        data.setPassword(configuration.getPassword());
        data.setDriverClassName(configuration.getDriverClassName());
        data.setConnectString(configuration.getJDBCConnectString());

        String maxPoolSizeStr = new Integer(configuration.getMaxPoolSize()).toString();
        data.setMaxPoolSize(new PositiveInteger(maxPoolSizeStr));

        // Set the properties here:
        PropertyList propsList = null;
        Properties props = configuration.getProperties();
        if (props != null) {
            int size = props.size();
            Property[] propsArr = new Property[size];
            propsList = new PropertyList();
            int i = 0;
            for (Map.Entry<Object,Object> e : props.entrySet()) {
                Property property = new Property();
                property.setName((String)e.getKey());
                property.setValue((String)e.getValue());
                propsArr[i++] = property;
            }
            propsList.setProperty(propsArr);
        }
        data.setProperties(propsList);
        return data;
    }
    
    public ActionConfig extractActionConfigData(IActionConfigDO inAction) {
        ActionConfig data = null;
     
        if (inAction != null) {
            data = new ActionConfig();
            data.setName(inAction.getName());
            data.setDisplayName(inAction.getDisplayName());
            ActionShortNameType shortName = new ActionShortNameType();
            shortName.setActionShortNameType(inAction.getShortName());
            data.setShortName(shortName);
            data.setCategory(inAction.getCategory());
            return data;
        }
     
        return null;
    }
    public ActionListConfig extractActionListConfigData(IActionListConfigDO config) {
        ActionListConfig data = null;
        if (config != null) {
            data = new ActionListConfig();
            IActionConfigDO[] actions = config.getActions();
            ActionConfig[] extractedActions = null;

            if (actions != null) {
                extractedActions = new ActionConfig[actions.length];
                
                for (int i = 0; i < actions.length; i++) {
                    extractedActions[i] = extractActionConfigData(actions[i]);
                }
            }

            data.setAction(extractedActions);
        }
        return data;
    }

    private MessageHandler extractMessageHandlerData(IMessageHandlerConfigurationDO config){
        if (config == null) {
            return null;
        }
        
        MessageHandler data = new MessageHandler();
        data.setName(config.getName());
        data.setClassName(config.getClassName());
        data.setProperties(extractPropertyListData(config.getProperties()));
        
        return data;
    }
    
    public MessageHandlers extractMessageHandlersConfigurationData(IMessageHandlersConfigurationDO config){
        if (config == null) {
            return null;
        }
        
        MessageHandlers data = new MessageHandlers();

        List<IMessageHandlerConfigurationDO> handlersData = config.getHandlerConfigs();
        if (handlersData != null) {
            MessageHandler[] handlerArray = new MessageHandler[handlersData.size()];
            int i =0;
            for(IMessageHandlerConfigurationDO handler : handlersData){
                handlerArray[i++] = extractMessageHandlerData(handler);
            }
            data.setMessageHandler(handlerArray);
        }
        
        return data;
    }
}
