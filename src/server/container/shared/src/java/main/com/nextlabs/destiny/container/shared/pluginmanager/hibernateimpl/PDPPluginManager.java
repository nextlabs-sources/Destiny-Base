/*
 * Created on Jan 07, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.pluginmanager.hibernateimpl;

import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;

import org.apache.commons.logging.Log;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.datastore.hibernate.usertypes.DateToLongUserType;

import com.nextlabs.destiny.container.shared.pluginmanager.IPDPPluginManager;
import com.nextlabs.destiny.container.shared.pluginmanager.PDPPluginManagementException;

public class PDPPluginManager implements IPDPPluginManager,
                                         ILogEnabled,
                                         IHasComponentInfo<PDPPluginManager>,
                                         IInitializable {
    public static final ComponentInfo<PDPPluginManager> COMP_INFO =
        new ComponentInfo<>(IPDPPluginManager.COMP_NAME,
                            PDPPluginManager.class.getName(),
                            IPDPPluginManager.class.getName(),
                            LifestyleType.SINGLETON_TYPE);
    
    private IHibernateRepository dataSource;
    private Log log;
    
    public PDPPluginManager() {
    }

    /**
     * Initializes the object.
     * 
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    @Override
    public void init() {
        getLog().debug("Initializing PDPPluginManager");
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();

        try {
            this.dataSource = (IHibernateRepository) compMgr.getComponent(DestinyRepository.MANAGEMENT_REPOSITORY.getName());
        } catch (RuntimeException e) {
            throw new RuntimeException("Required datasource not initialized for PDPPluginManager", e);
        }

        getLog().debug("Successfully initialized PDPPluginManager");
    }

    @Override
    public Collection<PDPPluginEntity> getModifiedPlugins(Date asOf) throws PDPPluginManagementException {
        if (asOf == null) {
            throw new NullPointerException("asOf is null");
        }

        getLog().info("Recieved query for plugins modified as of " + asOf);

        Session s = null;
        Transaction tx = null;
        Throwable originalException = null;
        
        try {
            s = getDataSource().getCountedSession();
            tx = s.beginTransaction();
            Query q = s.createQuery("select p from PDPPluginEntity as p " +
                                    "where p.timeRelation.activeFrom > :asOf " +
                                    "  and p.timeRelation.activeTo >= :now " +
                                    "  and p.status = :status ");
            q.setParameter("asOf", asOf, DateToLongUserType.TYPE);
            q.setParameter("now", new Date(), DateToLongUserType.TYPE);
            q.setParameter("status", "DEPLOYED");
            Collection<PDPPluginEntity> modifiedPlugins = q.list();
            tx.commit();
            return modifiedPlugins;
        } catch (HibernateException e) {
            HibernateUtils.rollbackTransation(tx, getLog());
            throw new PDPPluginManagementException(e);
        } finally {
            HibernateUtils.closeSession(getDataSource(), getLog());
        }
    }
    
    @Override
    public Collection<PDPPluginEntity> getDeletedPlugins(Date asOf) throws PDPPluginManagementException {
        if (asOf == null) {
            throw new NullPointerException("asOf is null");
        }
        
        getLog().info("Recieved query for plugins deleted as of " + asOf);

        Session s = null;
        Transaction tx = null;
        HibernateException originalException = null;
        
        try {
            s = getDataSource().getCountedSession();
            tx = s.beginTransaction();
            Query q = s.createQuery("select p from PDPPluginEntity as p " +
                                    "where p.timeRelation.activeFrom > :asOf " +
                                    "  and p.timeRelation.activeTo >= :now " +
                                    "  and p.status = :status ");
            q.setParameter("asOf", asOf, DateToLongUserType.TYPE);
            q.setParameter("now", new Date(), DateToLongUserType.TYPE);
            q.setParameter("status", "DELETED");
            Collection<PDPPluginEntity> deletedPlugins = q.list();
            tx.commit();
            return deletedPlugins;
        } catch (HibernateException e) {
            HibernateUtils.rollbackTransation(tx, getLog());
            throw new PDPPluginManagementException(e);
        } finally {
            HibernateUtils.closeSession(getDataSource(), getLog());
        }
        
    }

    @Override
    public long getLastModifiedTime() throws PDPPluginManagementException {
        // Find the latest modifed date of the plugin files and of the plugins themselves
        Session s = null;
        Transaction tx = null;
        HibernateException originalException = null;
        
        try {
            s = getDataSource().getCountedSession();
            tx = s.beginTransaction();
            Query q = s.createQuery("select max(p.modifiedDate) " +
                                    "from PDPPluginEntity p ");
            Date latest = (Date) q.uniqueResult();

            tx.commit();
            
            if (latest != null) {
                return latest.getTime();
            }
        } catch (HibernateException e) {
            HibernateUtils.rollbackTransation(tx, getLog());
            throw new PDPPluginManagementException(e);
        } finally {
            HibernateUtils.closeSession(getDataSource(), getLog());
        }

        return -1L;
    }
    
    /**
     * Sets the logger
     * 
     * @param logger
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(org.apache.commons.logging.Log)
     */
    @Override
    public void setLog(Log logger) {
        this.log = logger;
    }

    /**
     * Returns the logger
     * 
     * @return Log
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    @Override
    public Log getLog() {
        return this.log;
    }
    
    /**
     * @return ComponentInfo to help creating an instance with Component Manager
     * 
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    @Override
    public ComponentInfo<PDPPluginManager> getComponentInfo() {
        return COMP_INFO;
    }
    
    /**
     * Returns the data source object
     * 
     * @return the data source object
     */
    private IHibernateRepository getDataSource() {
        return this.dataSource;
    }
}
