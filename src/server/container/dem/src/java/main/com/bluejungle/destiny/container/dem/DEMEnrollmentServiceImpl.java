/*
 * Created on Mar 2, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.dem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.DuplicateEntryException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollerCreationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentSyncException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentThreadException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EntryNotFoundException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.InvalidConfigurationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.ReservedAttributeNameException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.EnrollmentTypeEnumType;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IColumnData;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IEnrollmentManager;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IRealmData;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.defaultimpl.EnrollmentManagerImpl;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.service.ServiceHelper;
import com.bluejungle.destiny.services.enrollment.DictionaryFault;
import com.bluejungle.destiny.services.enrollment.DuplicatedFault;
import com.bluejungle.destiny.services.enrollment.EnrollmentFailedFault;
import com.bluejungle.destiny.services.enrollment.EnrollmentIF;
import com.bluejungle.destiny.services.enrollment.EnrollmentInternalFault;
import com.bluejungle.destiny.services.enrollment.InvalidConfigurationFault;
import com.bluejungle.destiny.services.enrollment.NotFoundFault;
import com.bluejungle.destiny.services.enrollment.ReservedAttributeNameFault;
import com.bluejungle.destiny.services.enrollment.ServiceNotReadyFault;
import com.bluejungle.destiny.services.enrollment.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.enrollment.types.Column;
import com.bluejungle.destiny.services.enrollment.types.ColumnList;
import com.bluejungle.destiny.services.enrollment.types.EnrollmentProperty;
import com.bluejungle.destiny.services.enrollment.types.EntityType;
import com.bluejungle.destiny.services.enrollment.types.Profile;
import com.bluejungle.destiny.services.enrollment.types.Realm;
import com.bluejungle.destiny.services.enrollment.types.RealmList;
import com.bluejungle.dictionary.Dictionary;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.IElementField;
import com.bluejungle.dictionary.IEnrollment;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;

/**
 * This is the DEM enrollment service implementation.
 * 
 * @author ihanen
 * @version $Id:
 *
 */

public class DEMEnrollmentServiceImpl implements EnrollmentIF {
    private static final Log log = LogFactory.getLog(DEMEnrollmentServiceImpl.class);

    private IEnrollmentManager enrollmentManager;

    public DEMEnrollmentServiceImpl() {
        super();
    }
    
    private IEnrollmentManager getEnrollmentManager() throws ServiceNotReadyFault {
        if (this.enrollmentManager == null) {
            IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
            if (componentManager.isComponentRegistered(Dictionary.COMP_INFO.getName())) {
                this.enrollmentManager =
                        componentManager.getComponent(EnrollmentManagerImpl.class);
            } else {
                throw new ServiceNotReadyFault();
            }
        }
        return this.enrollmentManager;
    }
    
    /**
     * log all unknown, unexpected, rarely happen exception
     */
    private void log(Throwable t){
        log.error("", t);
    }
    
    /**
     * log exception, the stacktrace is only available when log level, INFO, is on
     * @param t
     */
    private void logLite(Throwable t) {
        if (log.isInfoEnabled()) {
            log.error("", t);
        } else {
            log.error(t.getMessage());
        }
    }
    
    @Override
    public RealmList getRealms(String name) throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            NotFoundFault {
        log.trace("Received call to list Realm in enrollment service");
        
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            Collection<IEnrollment> realms;
            if (name == null) {
                realms = enrollmentMgr.getRealms();
            } else {
                IEnrollment enrollment = enrollmentMgr.getRealm(name);
                realms = Collections.singleton(enrollment);
            }
             
            Realm[] realmArr = new Realm[realms.size()];
            int i = 0;
            for (IEnrollment enrollment : realms) {
                Realm realm = ServiceHelper.extractWSRealmFromDO(enrollment);
                realmArr[i++] = realm;
            }
            RealmList realmList = new RealmList();
            realmList.setRealms(realmArr);
            return realmList;
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            //name == null is already handled, it should not throw InvalidConfigurationException
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e ) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }       
    }

    @Override
    public void createRealm(Realm realm) throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            DuplicatedFault,
            InvalidConfigurationFault {
        log.trace("Received call to createRealm in enrollment service");
        
        if (realm == null) {
            throw new InvalidConfigurationFault("Received null enrollment info");
        }
        
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            enrollmentMgr.createRealm(new RealmData(realm));
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (DuplicateEntryException e) {
            logLite(e);
            throw new DuplicatedFault(e.getMessage());
        } catch (EnrollmentValidationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollerCreationException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (EnrollmentThreadException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }
    
    @Override
    public void updateRealm(Realm realm) throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            NotFoundFault,
            InvalidConfigurationFault {
        log.trace("Received call to updateRealm in enrollment service");

        if (realm == null) {
            throw new InvalidConfigurationFault("Received null enrollment info");
        }

        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            enrollmentMgr.updateRealm(new RealmData(realm));
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            logLite(e);
            throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollerCreationException e) {
            log(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentValidationException e) {
            log(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentThreadException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }

    @Override
    public void cancelAutoSyncForRealm(String name) throws
        ServiceNotReadyFault,
        UnauthorizedCallerFault,
        DictionaryFault,
        EnrollmentInternalFault,
        NotFoundFault,
        InvalidConfigurationFault {
        log.trace("Received call to cancelAutoSyncForRealm in enrollment service");

        if (name == null) {
            throw new InvalidConfigurationFault("name was null");
        }

        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        
        try {
            IEnrollment enrollment = enrollmentMgr.getRealm(name);
            
            Realm realm = ServiceHelper.extractWSRealmFromDO(enrollment);
            
            enrollmentMgr.cancelAutoSyncForRealm(new RealmData(realm));
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            logLite(e);
            throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollerCreationException e) {
            log(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentValidationException e) {
            log(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentThreadException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }
    
    @Override
    public void deleteRealmByName(String name) throws
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            InvalidConfigurationFault,
            NotFoundFault {
        if (name == null) {
            throw new InvalidConfigurationFault("name was null");
        }

        final IEnrollmentManager enrollmentMgr = getEnrollmentManager();

        try {
            IEnrollment enrollment = enrollmentMgr.getRealm(name);

            deleteRealm(ServiceHelper.extractWSRealmFromDO(enrollment));
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }
    
    @Override
    public void deleteRealm(Realm realm) throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            InvalidConfigurationFault,
            NotFoundFault {
        log.trace("Received call to deleteRealm in enrollment service");
        
        if (realm == null) {
            throw new InvalidConfigurationFault("Received null enrollment info");
        }
        
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            enrollmentMgr.deleteRealm(new RealmData(realm));
        } catch (DictionaryException e) {
            log(e);
        	throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            logLite(e);
        	throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentThreadException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e ) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }

    @Override
    public void enrollRealmAsync(String name) throws
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            EnrollmentFailedFault,
            InvalidConfigurationFault,
            NotFoundFault {
        log.trace("Recieved call to enrollRealmByName in enrollment service");

        if (name == null) {
            throw new InvalidConfigurationFault("name was null");
        }
        
        final IEnrollmentManager enrollmentMgr = getEnrollmentManager();

        try {
            IEnrollment enrollment = enrollmentMgr.getRealm(name);

            final Realm realm = ServiceHelper.extractWSRealmFromDO(enrollment);

            new Thread() {
                public void run() {
                    try {
                        enrollmentMgr.enrollRealm(new RealmData(realm));
                    } catch (Exception e) {
                        logLite(e);
                    }
                }
            }.start();
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e ) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }
    
    @Override
    public void enrollRealm(Realm realm) throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            EnrollmentFailedFault,
            InvalidConfigurationFault,
            NotFoundFault {
        log.trace("Received call to enrollRealm in enrollment service");
        
        if (realm == null) {
            throw new InvalidConfigurationFault("Received null enrollment info");
        }
        
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            enrollmentMgr.enrollRealm( new RealmData(realm) );
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            logLite(e);
        	throw new NotFoundFault(e.getMessage());
        } catch (EnrollmentValidationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentSyncException e) {
            log(e);
            throw new EnrollmentFailedFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (EnrollmentThreadException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        } catch (RuntimeException e ) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }


    @Override
    public ColumnList getColumns()throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault {
        log.trace("Received call to getColumns in enrollment service");
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            Collection<IElementField> fields = enrollmentMgr.getColumns();
            Collection<Column> columns = new ArrayList<Column>();
            for (IElementField field : fields) {
                Column column = ServiceHelper.extractWSColumnFromDO(field);
                columns.add(column);
            }
            ColumnList columnList = new ColumnList();
            columnList.setColumns(columns.toArray(new Column[0]));
            return columnList;
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }
    
    @Override
    public void addColumn(Column column) throws
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            DictionaryFault,
            EnrollmentInternalFault,
            DuplicatedFault,
            InvalidConfigurationFault{
        log.trace("Received call to addColumn in enrollment service");
        
        if (column == null) {
            throw new InvalidConfigurationFault("Received null column info");
        }
        
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            enrollmentMgr.addColumn(new ColumnData(column));
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (DuplicateEntryException e) {
            logLite(e);
            throw new DuplicatedFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }

    @Override
    public void delColumn(String logicalName, EntityType elementType) throws 
            ServiceNotReadyFault,
            UnauthorizedCallerFault,
            ReservedAttributeNameFault,
            DictionaryFault,
            EnrollmentInternalFault,
            InvalidConfigurationFault,
            NotFoundFault {
        log.trace("Received call to delColumn in enrollment service");
        if (logicalName == null) {
            throw new InvalidConfigurationFault("Received null logical name for column");
        }
        if (elementType == null) {
            throw new InvalidConfigurationFault("Received null elment type");
        }
        IEnrollmentManager enrollmentMgr = getEnrollmentManager();
        try {
            enrollmentMgr.delColumn(logicalName, elementType.getValue());
        } catch (DictionaryException e) {
            log(e);
            throw new DictionaryFault(e.getMessage());
        } catch (EntryNotFoundException e) {
            logLite(e);
            throw new NotFoundFault(e.getMessage());
        } catch (InvalidConfigurationException e) {
            logLite(e);
            throw new InvalidConfigurationFault(e.getMessage());
        } catch (ReservedAttributeNameException e) {
            logLite(e);
            throw new ReservedAttributeNameFault(e.getMessage());
        } catch (RuntimeException e) {
            log(e);
            throw new EnrollmentInternalFault(e.getMessage());
        }
    }


    /**
     * Realm data adapter
     * 
     * @author safdar
     */
    private class RealmData implements IRealmData {

        private String name;
        private EnrollmentTypeEnumType type;
        private Map<String, String[]> properties = new HashMap<String, String[]>();

        public RealmData(Realm wsRealm) {
            this.name = wsRealm.getName();
            this.type = EnrollmentTypeEnumType.getByName(wsRealm.getType().getValue());
            Profile profile = wsRealm.getProfile();
            if (profile != null) {
                EnrollmentProperty[] propArr = profile.getProperties();
                if (propArr != null) {
                    for (int i = 0; i < propArr.length; i++) {
                        EnrollmentProperty prop = propArr[i];
                      //Handling of empty properties being pass from portal enrollment deletion
                        if(prop!=null)
                        	properties.put(prop.getKey(), prop.getValue());
                    }
                }
            }
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IRealmData#getName()
         */
        public String getName() {
            return this.name;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IRealmData#getProperties()
         */
        public Map<String, String[]> getProperties() {
            return this.properties;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IRealmData#getType()
         */
        public EnrollmentTypeEnumType getType() {
            return this.type;
        }
    }

    /**
     * Column data adapter
     * 
     * @author safdar
     */
    private class ColumnData implements IColumnData {

        private String displayName;
        private String elementType;
        private String logicalName;
        private String type;

        public ColumnData(Column data) {
            this.displayName = data.getDisplayName();
            this.elementType = data.getParentType().getValue();
            this.logicalName = data.getLogicalName();
            this.type = data.getType().getValue();
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IColumnData#getDisplayName()
         */
        public String getDisplayName() {
            return this.displayName;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IColumnData#getElementType()
         */
        public String getElementType() {
            return this.elementType;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IColumnData#getLogicalName()
         */
        public String getLogicalName() {
            return this.logicalName;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.dictionary.enrollment.controller.IColumnData#getType()
         */
        public String getType() {
            return this.type;
        }
    }
}
