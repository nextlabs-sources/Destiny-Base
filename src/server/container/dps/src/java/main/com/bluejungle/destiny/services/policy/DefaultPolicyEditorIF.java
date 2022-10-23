package com.bluejungle.destiny.services.policy;

/*
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 * 
 * @author sergey
 */

import java.util.Calendar;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.services.policy.CircularReferenceFault;
import com.bluejungle.destiny.services.policy.InvalidPasswordFault;
import com.bluejungle.destiny.services.policy.PolicyEditorIF;
import com.bluejungle.destiny.services.policy.PolicyServiceFault;
import com.bluejungle.destiny.services.policy.ServiceNotReadyFault;
import com.bluejungle.destiny.services.policy.types.Access;
import com.bluejungle.destiny.services.policy.types.AccessList;
import com.bluejungle.destiny.services.policy.types.AgentStatusDescriptorDTO;
import com.bluejungle.destiny.services.policy.types.AgentStatusList;
import com.bluejungle.destiny.services.policy.types.AttributeDescriptorList;
import com.bluejungle.destiny.services.policy.types.ComponentList;
import com.bluejungle.destiny.services.policy.types.DeploymentHistoryList;
import com.bluejungle.destiny.services.policy.types.DeploymentRecordDTO;
import com.bluejungle.destiny.services.policy.types.DeploymentRecordList;
import com.bluejungle.destiny.services.policy.types.DomainObjectEnum;
import com.bluejungle.destiny.services.policy.types.DomainObjectEnumList;
import com.bluejungle.destiny.services.policy.types.DomainObjectStateEnum;
import com.bluejungle.destiny.services.policy.types.DomainObjectUsageListDTO;
import com.bluejungle.destiny.services.policy.types.EntityDescriptorDTO;
import com.bluejungle.destiny.services.policy.types.EntityDescriptorList;
import com.bluejungle.destiny.services.policy.types.EntityDigestList;
import com.bluejungle.destiny.services.policy.types.ExternalDataSourceConnectionInfo;
import com.bluejungle.destiny.services.policy.types.LeafObjectDTO;
import com.bluejungle.destiny.services.policy.types.LeafObjectList;
import com.bluejungle.destiny.services.policy.types.LeafObjectSearchSpecDTO;
import com.bluejungle.destiny.services.policy.types.ListOfIds;
import com.bluejungle.destiny.services.policy.types.LockRequestType;
import com.bluejungle.destiny.services.policy.types.ObligationDescriptorList;
import com.bluejungle.destiny.services.policy.types.PolicyActionsDescriptorList;
import com.bluejungle.destiny.services.policy.types.PolicyEditorRoles;
import com.bluejungle.destiny.services.policy.types.RealmList;
import com.bluejungle.destiny.services.policy.types.ResourceTreeNode;
import com.bluejungle.destiny.services.policy.types.ResourceTreeNodeList;
import com.bluejungle.destiny.services.policy.types.StringList;

/**
 * This class provides default implementation of the PolicyEditorIF interface.
 * This eliminates the compile-time dependency on the generated code from
 * code in other libraries that must implement the interface.
 */
public class DefaultPolicyEditorIF implements PolicyEditorIF {

    /**
     * @see PolicyEditorIF#getConfigValue(String)
     */
    @Override
    public String getConfigValue( String name ) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getNumDeployedPolicies()
     */
    @Override
    public int getNumDeployedPolicies() throws ServiceNotReadyFault, PolicyServiceFault {
        return 0;
    }

    /**
     * @see PolicyEditorIF#getLatestDeploymentTime()
     */
    @Override
    public Calendar getLatestDeploymentTime() throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#objectLock(java.math.BigInteger, LockRequestType)
     */
    @Override
    public String objectLock(ID id, LockRequestType type) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDescriptorsForNameAndType(java.lang.String, DomainObjectEnumList)
     */
    @Override
    public EntityDescriptorList getDescriptorsForNameAndType(String nameTemplate, DomainObjectEnumList domainObjectEnumList) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getEntitiesForNamesAndType(StringList, DomainObjectEnum)
     */
    @Override
    public String getEntitiesForNamesAndType(StringList nameList, DomainObjectEnum domainObjectType) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getEntitiesForDescriptors(EntityDescriptorList)
     */
    @Override
    public String getEntitiesForDescriptors(EntityDescriptorList descriptorListReq) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDODDigests(StringList)
     */
    @Override
    public EntityDigestList getDODDigests(StringList types) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDODDigestsByeIds(ListOfIds)
     */
    @Override
    public EntityDigestList getDODDigestsByIds(ListOfIds ids) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDescriptorsByIds(ListOfIds)
     */
    @Override
    public EntityDescriptorList getDescriptorsByIds(ListOfIds ids) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#saveEntities(java.lang.String)
     */
    @Override
    public EntityDescriptorList saveEntities(String pql) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#saveEntitiesDigest(java.lang.String)
     */
    @Override
    public EntityDigestList saveEntitiesDigest(String pql) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getReferringObjectsForGroup(java.lang.String, DomainObjectEnum, DomainObjectEnum, boolean)
     */
    @Override
    public EntityDescriptorList getReferringObjectsForGroup(String nameTemplate, DomainObjectEnum domainObjectEnum, DomainObjectEnum referringType, boolean onlyDirect) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getReferringObjectsAsOf(EntityDescriptorList, java.util.Calendar, boolean)
     */
    @Override
    public EntityDescriptorList getReferringObjectsAsOf(EntityDescriptorList descriptors, Calendar theDate, boolean onlyDirect) throws ServiceNotReadyFault, CircularReferenceFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDependencies(EntityDescriptorList)
     */
    @Override
    public EntityDescriptorList getDependencies(EntityDescriptorList descriptorListReq, boolean directOnly) throws ServiceNotReadyFault, CircularReferenceFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDependenciesDigest(EntityDigestList)
     */
    @Override
    public EntityDigestList getDependenciesDigest(EntityDigestList descriptorListReq, boolean directOnly) throws ServiceNotReadyFault, CircularReferenceFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDescriptorsForState(DomainObjectStateEnum,String,long)
     */
    @Override
    public EntityDescriptorList getDescriptorsForState(DomainObjectStateEnum objectState) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getSubjectAttributes(DomainObjectEnum)
     */
    @Override
    public AttributeDescriptorList getSubjectAttributes(DomainObjectEnum domainObjectType) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getResourceAttributes()
     */
    @Override
    public AttributeDescriptorList getResourceAttributes(String subtypeName) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getResourceAttributes()
     */
    @Override
    public AttributeDescriptorList getCustomResourceAttributes(String subtypeName) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDeployedObjectDescriptors(EntityDescriptorList, java.util.Calendar)
     */
    @Override
    public EntityDescriptorList getDeployedObjectDescriptors(EntityDescriptorList descriptors, Calendar theDate) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDeployedObjects(EntityDescriptorList, java.util.Calendar)
     */
    @Override
    public String getDeployedObjects(EntityDescriptorList descriptors, Calendar theDate) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDependenciesAsOf(EntityDescriptorList, java.util.Calendar)
     */
    @Override
    public EntityDescriptorList getDependenciesAsOf(EntityDescriptorList descriptors, Calendar theDate) throws ServiceNotReadyFault, CircularReferenceFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#descriptorsForDeploymentRecord(DeploymentRecordDTO)
     */
    @Override
    public EntityDescriptorList descriptorsForDeploymentRecord(DeploymentRecordDTO record) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getDeploymentHistory(EntityDescriptorDTO)
     */
    @Override
    public DeploymentHistoryList getDeploymentHistory(EntityDescriptorDTO descriptor) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#scheduleDeployment(EntityDescriptorList, java.util.Calendar)
     */
    @Override
    public DeploymentRecordDTO scheduleDeployment(EntityDescriptorList descriptors, Calendar theDate) throws ServiceNotReadyFault, PolicyServiceFault {
    	return null;
    }

    /**
     * @see PolicyEditorIF#scheduleUndeployment(EntityDescriptorList, java.util.Calendar)
     */
    @Override
    public DeploymentRecordDTO scheduleUndeployment(EntityDescriptorList descriptors, Calendar theDate) throws ServiceNotReadyFault, PolicyServiceFault {
    	return null;
    }

    /**
     * @return 
     * @see PolicyEditorIF#cancelScheduledDeployment(DeploymentRecordDTO)
     */
    @Override
    public void cancelScheduledDeployment(DeploymentRecordDTO record) throws ServiceNotReadyFault, PolicyServiceFault {
    }

    /**
     * @see PolicyEditorIF#getDeploymentRecords(long, long)
     */
    @Override
    public DeploymentRecordList getDeploymentRecords(long from, long to) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getLeafObjectsByIds(ListOfIds,ListOfIds,ListOfIds)
     */
    @Override
    public LeafObjectList getLeafObjectsByIds(ListOfIds elements, ListOfIds userGroups, ListOfIds hostGroups) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#runLeafObjectQuery(LeafObjectSearchSpecDTO)
     */
    @Override
	public LeafObjectList runLeafObjectQuery(LeafObjectSearchSpecDTO searchSpec) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }
    
    /**
     * @see PolicyEditorIF#countLeafObjectQuery(LeafObjectSearchSpecDTO)
     */
    @Override
	public int countLeafObjectQuery(LeafObjectSearchSpecDTO searchSpec) throws ServiceNotReadyFault, PolicyServiceFault {
        return 0;
    }
		
    /**
     * @see PolicyEditorIF#getMatchingSubjects(EntityDescriptorDTO)
     */
    @Override
    public LeafObjectList getMatchingSubjects(EntityDescriptorDTO descriptor) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getMatchingActions(EntityDescriptorDTO)
     */
    @Override
    public LeafObjectList getMatchingActions(EntityDescriptorDTO descriptor) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getFullyResolvedEntity(EntityDescriptorDTO)
     */
    @Override
    public String getFullyResolvedEntity(EntityDescriptorDTO descriptor) throws ServiceNotReadyFault, CircularReferenceFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#prepareForTests()
     */
    @Override
    public void prepareForTests() throws ServiceNotReadyFault, PolicyServiceFault {
    }

    /**
     * @see PolicyEditorIF#ensureOperationIsAllowed(EntityDescriptorList, Access)
     */
    @Override
    public EntityDescriptorList ensureOperationIsAllowed(EntityDescriptorList descriptorListReq, Access desiredAccess) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#allowedActions(EntityDescriptorList)
     */
    @Override
    public AccessList allowedActions(EntityDescriptorList descriptorListReq) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getAgentList()
     */
    @Override
    public AgentStatusList getAgentList() throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getAgentsForDeployedObject(EntityDescriptorDTO, java.util.Calendar)
     */
    @Override
    public AgentStatusList getAgentsForDeployedObject(EntityDescriptorDTO descriptor, Calendar theDate) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#deploymentStatusForAgent(AgentStatusDescriptorDTO)
     */
    @Override
    public EntityDescriptorList deploymentStatusForAgent(AgentStatusDescriptorDTO descriptor) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getSuperUser()
     */
    @Override
    public LeafObjectDTO getSuperUser() throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }
    
    /**
     * @return 
     * @see PolicyEditorIF#changePassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changePassword(String uniqueName, String oldPassword, String newPassword) throws InvalidPasswordFault {
    }

    /**
     * @see PolicyEditorIF#getObligationDescriptors()
     */
    @Override
    public ObligationDescriptorList getObligationDescriptors() throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getPolicyActionsDescriptors()
     */
    @Override
    public PolicyActionsDescriptorList getPolicyActionsDescriptors() throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getAccessGroupsForUser()
     */
    @Override
    public LeafObjectList getAccessGroupsForUser (ID id) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }

    /**
     * @see PolicyEditorIF#getAllowedEntitiesForUser()
     */
    @Override
    public ComponentList getAllowedEntitiesForUser (ID id) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }
    
    @Override
    public void updateComputersWithAgents() {
        
    }
        
    @Override
    public boolean hasObjectsToDeploy() {
        return false;
    }

    @Override
    public DomainObjectUsageListDTO getUsageList( EntityDescriptorList descriptorListReq ) throws ServiceNotReadyFault {
        return null;
    }
    
    @Override
    public StringList getDictionaryEnrollmentNames( PolicyEditorRoles roles ) throws ServiceNotReadyFault {
        return null;
    }

    @Override
    public RealmList getDictionaryEnrollmentRealms( PolicyEditorRoles roles ) throws ServiceNotReadyFault {
        return null;
    }

    @Override
    public StringList getPortalURLList() {
        return null;
    }
  
    @Override
    public int createExternalDataSource(ExternalDataSourceConnectionInfo info) {
        return 0;
    }

    @Override
    public ResourceTreeNodeList getResourceTreeNodeChildren(int sourceID, ResourceTreeNode node) {
        return null;
    }
    
    @Override
    public ResourceTreeNodeList getMatchingPortalResource(int arg0, EntityDescriptorDTO arg1) throws ServiceNotReadyFault, PolicyServiceFault {
        return null;
    }
    
    @Override
    public void executePush(){
    	//nothing
    }
    
    @Override
    public void schedulePush(Calendar scheduleTime){
    	//nothing
    }

    @Override
    public void updateDictionaryConsistentTime() {
        // nothing
    }
}
