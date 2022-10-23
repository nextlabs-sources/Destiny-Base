/*
 * Created on Mov 8, 2004
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */

package com.bluejungle.destiny.container.dms;

import com.bluejungle.destiny.container.shared.agentmgr.IAgentDO;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentManager;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentQueryResults;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentStatistics;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentType;
import com.bluejungle.destiny.container.shared.agentmgr.InvalidIDException;
import com.bluejungle.destiny.container.shared.agentmgr.InvalidQuerySpecException;
import com.bluejungle.destiny.container.shared.agentmgr.PersistenceException;
import com.bluejungle.destiny.container.shared.agentmgr.service.AgentServiceHelper;
import com.bluejungle.destiny.container.shared.profilemgr.ProfileNotFoundException;
import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.services.management.AgentServiceIF;
import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentDTOList;
import com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.services.management.types.AgentStatistics;
import com.bluejungle.destiny.services.management.types.AgentTypeDTO;
import com.bluejungle.destiny.services.management.types.AgentTypeDTOList;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This is the Agent web service implementation class.
 * 
 * @author safdar, ihanen
 */
public class DMSAgentServiceImpl implements AgentServiceIF {

    private static final Log LOG = LogFactory.getLog(DMSAgentServiceImpl.class.getName());

    private AgentServiceHelper agentWSHelper;

    /**
     * Constructor
     */
    public DMSAgentServiceImpl() {
        super();
        this.agentWSHelper = AgentServiceHelper.getInstance();
    }

    
    /**
     * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentTypes()
     */
    public AgentTypeDTOList getAgentTypes() throws ServiceNotReadyFault {
        IAgentManager agentManager = getAgentManager();
        List<IAgentType> agentTypes = agentManager.getAgentTypes();
        AgentTypeDTO[] agentTypeDTOs = new AgentTypeDTO[agentTypes.size()];
        Iterator<IAgentType> agentTypesIterator = agentTypes.iterator();
        for (int i=0; agentTypesIterator.hasNext(); i++) {
            IAgentType nextAgentType = agentTypesIterator.next();
            agentTypeDTOs[i] = AgentServiceHelper.getInstance().extractAgentType(nextAgentType);
        }

        AgentTypeDTOList dto = new AgentTypeDTOList();
        dto.setAgentTypes(agentTypeDTOs);

        return dto;
    }


    /**
     * Unregister an agent. {@see IAgentManager#unregisterAgent(Long)}
     * 
     * @param agentId
     *            the id of the agent to unregister
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     */
    public void unregisterAgent(ID agentId) throws CommitFault, ServiceNotReadyFault, UnknownEntryFault {
        final IAgentManager agentManager = getAgentManager();
        try {
            agentManager.unregisterAgent(agentId.getID().longValue());
        } catch (PersistenceException exception) {
            getLog().error("Failed to unregister agent with id, " + agentId, exception);
            throw new CommitFault();
        } catch (InvalidIDException exception) {
            getLog().error("Failed to unregister agent with id, " + agentId + ".  No agent with this id found.", exception);
            throw new UnknownEntryFault();
        }
    }

    /**
     * Returns the agent matching the query specification
     * 
     * @param wsAgentQuery
     *            agent query specification
     * @return
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     */
    public AgentQueryResultsDTO getAgents(AgentDTOQuerySpec wsAgentQuery) throws BadArgumentFault, ServiceNotReadyFault {
        final AgentQueryResultsDTO wsQueryResults = new AgentQueryResultsDTO();
        final AgentDTOList wsAgentList = new AgentDTOList();
        try {
            final IAgentQueryResults queryResults = getAgentManager().getAgents(WebServiceHelper.convertAgentMgrQuerySpecToInternalType(wsAgentQuery, this.agentWSHelper.getLatestPolicyDeploymentTimestamp()));
            IAgentDO[] agents = queryResults.getAgents();
            if (agents != null) {
                wsAgentList.setAgents(this.agentWSHelper.extractAgentArrayData(queryResults.getAgents()));
            }
            wsQueryResults.setAgentList(wsAgentList);
            wsQueryResults.setPolicyStatus(this.agentWSHelper.extractPolicyAssemblyStatusData());
        } catch (PersistenceException e) {
            // Not the right exception
            throw new ServiceNotReadyFault(e.getLocalizedMessage());
        } catch (InvalidQuerySpecException e) {
            throw new BadArgumentFault(e.getLocalizedMessage());
        }

        return wsQueryResults;
    }

    /**
     * Returns the information for a given agent
     * 
     * @param id
     *            id of the agent
     * @return the information for a given agent
     * @throws ServiceNotReadyFault
     *             if the service is not ready
     * @throws UnknownEntryFault
     *             if the agent id does not exist
     * @throws RemoteException
     *             if some other error occurs.
     */
    public AgentDTO getAgentById(ID id) throws UnknownEntryFault, ServiceNotReadyFault {
        if (id == null) {
            throw new UnknownEntryFault();
        }

        AgentDTO agentDTO = null;

        try {
            IAgentDO agentDO = this.getAgentManager().getAgentById(id.getID().longValue());
            agentDTO = this.agentWSHelper.extractAgentData(agentDO);
        } catch (PersistenceException e) {
            throw new ServiceNotReadyFault(e);
        }

        return agentDTO;
    }

    /**
     * Sets the communication profile name for the agent
     * 
     * @param id
     *            id of the agent
     * @param profileName
     *            name of the profile to use
     * @throws RemoteException
     *             if the assignment fails
     * @throws UnknownEntryFault
     *             if the agent id is unknown
     * @throws ServiceNotReadyFault
     *             if the backing component has not yet been initialized
     */
    public void setCommProfile(ID id, String profileName) throws UnknownEntryFault, ServiceNotReadyFault {
        IAgentManager agentMgr = this.getAgentManager();
        if (id == null) {
            throw new UnknownEntryFault();
        }

        try {
            agentMgr.setCommProfile(id.getID().longValue(), profileName);
        } catch (ProfileNotFoundException e) {
            throw new UnknownEntryFault(e);
        } catch (InvalidIDException e) {
            throw new UnknownEntryFault(e);
        } catch (PersistenceException e) {
            throw new UnknownEntryFault(e);
        }
    }

    /**
     * Sets the communication profile for the specified list of agents
     * 
     * @param agentIDList
     *            ids of the list of agents
     * @param profileId
     *            the id of the profile to assign to the agents
     * @throws RemoteException
     *             if the assignment fails
     * @throws UnknownEntryFault
     *             if an agent id or the profile id is unknown
     * @throws ServiceNotReadyFault
     *             if the backing component has not yet been initialized
     */
    public void setCommProfileForAgents(IDList agentIDList, ID profileId) throws UnknownEntryFault, ServiceNotReadyFault {
        if (agentIDList == null) {
            throw new NullPointerException("agentIDList cannot be null.");
        }

        if (profileId == null) {
            throw new NullPointerException("profileId cannot be null.");
        }

        IAgentManager agentMgr = this.getAgentManager();

        ID[] agentIds = agentIDList.getIDList();
        if (agentIds != null) {
            Set agentIDsAsLongs = new HashSet(agentIds.length);
            for (int i = 0; i < agentIds.length; i++) {
                agentIDsAsLongs.add(agentIds[i].getID().longValue());
            }

            try {
                agentMgr.setCommProfileForAgents(agentIDsAsLongs, profileId.getID().longValue());
            } catch (ProfileNotFoundException e) {
                throw new UnknownEntryFault(e);
            } catch (PersistenceException e) {
                throw new UnknownEntryFault(e);
            }
        }
    }

    /**
     * Sets the agent profile name for the agent
     * 
     * @param id
     *            id of the agent
     * @param profileName
     *            name of the profile to use
     * @throws RemoteException
     *             if the assignment fails
     * @throws UnknownEntryFault
     *             if the agent id is unknown
     * @throws ServiceNotReadyFault
     *             if the backing component has not yet been initialized
     */
    public void setAgentProfile(ID id, String profileName) throws ServiceNotReadyFault, UnknownEntryFault {
        IAgentManager agentMgr = this.getAgentManager();
        if (id == null) {
            throw new UnknownEntryFault();
        }

        try {
            agentMgr.setAgentProfile(id.getID().longValue(), profileName);
        } catch (ProfileNotFoundException e) {
            throw new UnknownEntryFault(e);
        } catch (InvalidIDException e) {
            throw new UnknownEntryFault(e);
        } catch (PersistenceException e) {
            throw new UnknownEntryFault(e);
        }
    }

    /**
     * Returns an instance of the IAgentManager implementation class
     * (AgentManager) after obtaining it from the component manager.
     * 
     * @return IAgentManager
     */
    protected IAgentManager getAgentManager() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (!compMgr.isComponentRegistered(IAgentManager.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (IAgentManager) compMgr.getComponent(IAgentManager.COMP_NAME);
    }

    /**
     * Returns statistics about the agents in the system
     * 
     * @return statistics about the agents in the system
     * @throws RemoteException
     *             if the service call fails
     */
    public AgentStatistics getAgentStatistics() throws ServiceNotReadyFault {
        IAgentManager agentMgr = this.getAgentManager();
        try {
            IAgentStatistics stats = agentMgr.getAgentStatistics();
            return (this.agentWSHelper.extractAgentStatisticsData(stats));
        } catch (PersistenceException e) {
            return new AgentStatistics();
        }
    }

    /**
     * Retrieve a Logger
     * 
     * @return a Logger
     */
    private Log getLog() {
        return LOG;
    }
}
