package com.bluejungle.dictionary;

/*
 * All sources, binaries and HTML pages (C) Copyright 2006 by Blue Jungle Inc,
 * Redwood City, CA. Ownership remains with Blue Jungle Inc.
 * All rights reserved worldwide.
 *
 * @author sergey
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;

import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.usertypes.BinaryAsString;
import com.bluejungle.framework.datastore.hibernate.usertypes.DateToLongUserType;
import com.bluejungle.framework.utils.ObjectHelper;

/**
 * This class implements the IEnrollment interface of the dictionary
 * component.
 */
class Enrollment implements IEnrollment {
    /** This constant is used to set parameters of Enrollment type
     * in the HQLFormatVisitor class.
     */
    public static final Type TYPE = Hibernate.entity(Enrollment.class);

    /** The ID of this enrollment for use by Hibernate.  */
    Long id;

    /** The version of this enrollment for use by Hibernate. */
    int version;

    /** The description of this enrollment */
    private String description;

    /** The domain name associated with this enrollment. */
    private String domainName;

    /** The type of this enrollment
     * (e.g. Active Directory import, LDIF, etc.
     * Data import application use this field for their purposes.
     */
    private String enrollmentType;
    
    private Calendar nextSyncTime;

    /**
     * The id of the user who created this enrollment
     */
    private Long createdBy;

    /**
     * The creation date of the enrollment
     */
    private Date createdDate;

    /**
     * The id of the user who last updated this enrollment
     */
    private Long lastUpdatedBy;

    /**
     * The latest update date of the enrollment
     */
    private Date lastUpdated;
    
    /**
     * This <code>Map</code> holds the properties of this enrollment.
     * Properties are pieces of arbitrary data the enrollment applications
     * need to associate with this particular enrollment.
     */
    private Map<String,IEnrollmentProperty> properties = new HashMap<String,IEnrollmentProperty>();

    /**
     * This <code>Map</code> holds the external mappings defined for fields
     * of user types for use in this enrollment.
     * The information from this <code>Map</code> is fully duplicated in the
     * mappingsByType <code>Map</code>; however, these maps serve different
     * purposes: this <code>Map</code> is for external use by Hibernate
     * (and therefore may be slow for searches by type), while the other one
     * is harder to save but easier to search.
     * Most methods modifying the maps update both of them.
     */
    private Map<IElementField,ExternalFieldMapping> externalMappings = new HashMap<IElementField,ExternalFieldMapping>();

    /**
     * This <code>Map</code> holds external mappings of fields for this
     * enrollment; the <code>Map</code> is organized
     * by <code>ElementType</code>.
     */
    private Map<IElementType,MappingsForType> mappingsByType = new HashMap<IElementType,MappingsForType>();

    /**
     * The <code>Dictionary</code> object for reference from
     * this <code>Enrollment</code> object (<code>Dictionary</code>
     * objects are not persistent; they have singleton lifecycle).
     *
     * This field is set when the <code>Dictionary</code> retrieves
     * the <code>Enrollment</code> from the Hibernate datastore.
     * After that, the field does not change.
     */
    private Dictionary dictionary;

    /**
     * This field defines whether enrollment is recurring
     * if enrollment is recurring, server will automatically pull the data
     * periodically, otherwise, enrollment will be invoked by external command
     */
    private boolean isRecurring = false;

    /**
     * This field defines whether enrollment is active
     */
    private boolean isActive = true;

    /**
     * This field specifies whether an enrollment is currently in the process of being sync'd
     */
    private boolean isSyncing = false;

    /**
     * Package-private constructor for Hibernate.
     */
    Enrollment() {
    }

    /**
     * Package-private constructor for the Dictionary.
     * @param domainName the domain name for this enrollment.
     */
    Enrollment(String domainName, Dictionary dictionary) {
        this.domainName = domainName;
        this.dictionary = dictionary;

        Date now = new Date();
        createdDate = now;
        createdBy = -1L;
        lastUpdated = now;
        lastUpdatedBy = -1L;
    }

    /**
     * @see IEnrollment#getDescription()
     */
    @Override
    public String getDescription () {
        return description;
    }

    /**
     * @see IEnrollment#setDescription(String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @see IEnrollment#getCreatedBy()
     */
    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * @see IEnrollment#setCreatedBy(Long)
     */
    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    /**
     * @see IEnrollment#getLastUpdatedBy()
     */
    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @see IEnrollment#setLastUpdatedBy(Long)
     */
    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @see IEnrollment#getCreatedDate()
     */
    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @see IEnrollment#setCreatedDate(Date)
     */
    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    /**
     * @see IEnrollment#getLastUpdated()
     */
    @Override
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @see IEnrollment#setLastUpdated(Date)
     */
    @Override
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * @see IEnrollment#getDomainName()
     */
    @Override
    public String getDomainName() {
        return domainName;
    }

    /**
     * @see IEnrollment#setDomainName(String)
     */
    @Override
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * @see IEnrollment#deleteProperty(String)
     */
    @Override
    public void deleteProperty(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        properties.remove(name);
    }

    /**
     * @see com.bluejungle.dictionary.IEnrollment#deleteAllProperties()
     */
    @Override
    public void deleteAllProperties() {
    	properties.clear();
    }

    /**
     * @see IEnrollment#getStrPropertyNames()
     */
    @Override
    public String[] getStrPropertyNames() {
        return getPropertyNames(EnrollmentPropertyType.STRING);
    }

    /**
     * @see IEnrollment#getStrProperty(String);
     */
    @Override
    public String getStrProperty(String name) {
        return getSpecificProperty(name, EnrollmentPropertyType.STRING, false).getString();
    }

    /**
     * @see IEnrollment#setStrProperty(String, String)
     */
    @Override
    public void setStrProperty(String name, String val) {
        getSpecificProperty(name, EnrollmentPropertyType.STRING, true).setString(val);
    }

    /**
     * @see IEnrollment#getStrArrayPropertyNames()
     */
    @Override
    public String[] getStrArrayPropertyNames() {
        return getPropertyNames(EnrollmentPropertyType.STRING_ARRAY);
    }

    /**
     * @see IEnrollment#getStrArrayProperty(String)
     */
    @Override
    public String[] getStrArrayProperty(String name) {
        return getSpecificProperty(name, EnrollmentPropertyType.STRING_ARRAY, false).getStrArray();
    }

    /**
     * @see IEnrollment#setStrArrayProperty(String, String[])
     */
    @Override
    public void setStrArrayProperty(String name, String[] val) {
        getSpecificProperty(name, EnrollmentPropertyType.STRING_ARRAY, true).setStrArray(val);
    }

    /**
     * @see IEnrollment#getBinPropertyNames()
     */
    @Override
    public String[] getBinPropertyNames() {
        return getPropertyNames(EnrollmentPropertyType.BINARY);
    }

    /**
     * @see IEnrollment#getBinProperty(String)
     */
    @Override
    public byte[] getBinProperty(String name) {
        return getSpecificProperty(name, EnrollmentPropertyType.BINARY, false).getBinary();
    }

    /**
     * @see IEnrollment#setBinProperty(String, byte[])
     */
    @Override
    public void setBinProperty(String name, byte[] val) {
        getSpecificProperty(name, EnrollmentPropertyType.BINARY, true).setBinary(val);
    }

    /**
     * @see IEnrollment#getNumPropertyNames()
     */
    @Override
    public String[] getNumPropertyNames() {
        return getPropertyNames(EnrollmentPropertyType.NUMBER);
    }

    /**
     * @see IEnrollment#getNumProperty(String)
     */
    @Override
    public long getNumProperty(String name) {
        return getSpecificProperty(name, EnrollmentPropertyType.NUMBER, false).getNumber();
    }

    /**
     * @see IEnrollment#setNumProperty(String, long)
     */
    @Override
    public void setNumProperty(String name, long val) {
        getSpecificProperty(name, EnrollmentPropertyType.NUMBER, true).setNumber(val);
    }

    /**
     * @see IEnrollment#lookupField(IElementType, String)
     */
    @Override
    public IElementField[] lookupField(IElementType type, String externalName) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (externalName == null) {
            throw new NullPointerException("externalName");
        }
        MappingsForType perTypeMappings = mappingsByType.get(type);
        if (perTypeMappings == null) {
            throw new IllegalArgumentException("type: " + type + ", externalName: " + externalName);
        }
        return perTypeMappings.getField(externalName);
    }

    /**
     * @see IEnrollment#setExternalName(IElementField, String)
     */
    @Override
    public synchronized void setExternalName(IElementField fieldObj, String externalName) {
        if (fieldObj == null) {
            throw new NullPointerException("field");
        }
        if (!(fieldObj instanceof ElementField)) {
            throw new IllegalArgumentException("field");
        }
        if (externalName == null) {
            throw new NullPointerException("externalName");
        }
        ElementField field = (ElementField) fieldObj;
        MappingsForType perTypeMappings = mappingsByType.get(field.getParentType());
        if (perTypeMappings == null) {
            perTypeMappings = new MappingsForType();
            mappingsByType.put(field.getParentType(), perTypeMappings);
        }
        perTypeMappings.addMapping(field, externalName);
    }

    /**
     * @see IEnrollment#clearExternalName(IElementField)
     */
    @Override
    public synchronized void clearExternalName(IElementField fieldObj) {
        if (fieldObj == null) {
            throw new NullPointerException("field");
        }
        if (!(fieldObj instanceof ElementField)) {
            throw new IllegalArgumentException("field");
        }
        ElementField field = (ElementField) fieldObj;
        MappingsForType perTypeMappings = mappingsByType.get(field.getParentType());
        if (perTypeMappings == null) {
            return;
        }
        perTypeMappings.removeMapping(field);
    }

    /**
     * @see IEnrollment#clearAllExternalNames()
     */
    @Override
    public synchronized void clearAllExternalNames() {
        externalMappings.clear();
        mappingsByType.clear();
    }

    /**
     * @see IEnrollment#getExternalName(IElementField)
     */
    @Override
    public synchronized String getExternalName(IElementField fieldObj) {
        if (fieldObj == null) {
            throw new NullPointerException("field");
        }
        if (!(fieldObj instanceof ElementField)) {
            throw new IllegalArgumentException("field");
        }
        ElementField field = (ElementField) fieldObj;
        MappingsForType perTypeMappings = mappingsByType.get(field.getParentType());
        if (perTypeMappings == null) {
            return null;
        }
        return perTypeMappings.getFieldName(field);
    }

    /**
     * @see IEnrollment#getExternalNames(IElementType)
     */
    @Override
    public synchronized String[] getExternalNames(IElementType type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (!(type instanceof ElementType)) {
            throw new IllegalArgumentException("type");
        }
        MappingsForType mappings = mappingsByType.get(type);
        String[] res = (mappings != null) ? mappings.getExternalNames() : new String[0];
        Arrays.sort(res);
        return res;
    }

    /**
     * @see IEnrollment#clearExternalNames(IElementType)
     */
    @Override
    public synchronized void clearExternalNames(IElementType type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (!(type instanceof ElementType)) {
            throw new IllegalArgumentException("type");
        }
        for (IElementField field : type.getFields()) {
            externalMappings.remove(field);
        }
        mappingsByType.remove(type);
    }

    /**
     * @see IEnrollment#createSession()
     */
    @Override
    public IEnrollmentSession createSession() throws DictionaryException {
        if (dictionary == null) {
            throw new IllegalStateException("enrollment.dictionary is not set");
        }
        if (id == null) {
            throw new IllegalStateException(
                    "enrollment must be saved before you can create a session.");
        }
        return new EnrollmentSession(dictionary, dictionary.getCountedSession(), this);
    }

    /**
     * @see IEnrollment#makeNewElement(IElementType, IDictionaryKey)
     */
    @Override
    public IMElement makeNewElement(DictionaryPath path, IElementType type, DictionaryKey key) {
        return new LeafElement(path, type, key, this);
    }

    /**
     * @see IEnrollment#getByKey(DictionaryKey, Date)
     */
    @Override
    public IMElementBase getByKey(DictionaryKey key, Date asOf) throws DictionaryException {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (asOf == null) {
            asOf = new Date();
        }
        Session hs = null;
        Transaction tx = null;
        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();
            DictionaryElementBase res = (DictionaryElementBase)hs.createQuery(
                "from DictionaryElementBase b "
            +   "where b.keyData=:keyData "
            +   "and b.timeRelation.activeTo > :asOf "
            +   "and b.timeRelation.activeFrom <= :asOf "
            +   "and b.enrollment = :enrollment"
            )
            .setParameter("keyData", key.getKey(), BinaryAsString.TYPE)
            .setParameter("asOf", asOf, DateToLongUserType.TYPE)
            .setParameter("enrollment", this)
            .uniqueResult();
            if ( res != null ) {
            	((Enrollment)res.getEnrollment()).setDictionary(dictionary);
            }
            return res;
        } catch (HibernateException cause) {
            throw new DictionaryException("Unable to create provisional references.", cause);
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }

    /**
     * @see IEnrollment#getElement(DictionaryKey, Date)
     */
    @Override
    public IMElement getElement(DictionaryKey key, Date asOf) throws DictionaryException {
        IMElementBase res = getByKey(key, asOf);
        if (res == null) {
            return null;
        }
        if (res instanceof IMElement) {
            return (IMElement) res;
        } else {
            throw new DictionaryException("Dictionary key does not correspond to an element: " + key);
        }
    }

    /**
     * @see IEnrollment#makeNewEnumeratedGroup(DictionaryPath, DictionaryKey)
     */
    @Override
    public IMGroup makeNewEnumeratedGroup(DictionaryPath path, DictionaryKey key) {
        return new EnumeratedGroup(path, key, this);
    }

    /**
     * @see IEnrollment#makeNewStructuralGroup(DictionaryPath, DictionaryKey)
     */
    @Override
    public IMGroup makeNewStructuralGroup(DictionaryPath path, DictionaryKey key) {
        return new StructuralGroup(path, key, this);
    }

    /**
     * @see IEnrollment#getGroup(DictionaryKey, Date)
     */
    @Override
    public IMGroup getGroup(DictionaryKey key, Date asOf) throws DictionaryException {
        IMElementBase res = getByKey(key, asOf);
        if (res == null) {
            return null;
        }
        if (res instanceof IMGroup) {
            return (IMGroup)res;
        } else {
            throw new DictionaryException("Dictionary key does not correspond to a group: "+key);
        }
    }
    
    /**
     * @see IEnrollment#getProvisionalReferences(Collection paths)
     */
    @Override
    public List<IReferenceable> getProvisionalReferences(Collection<DictionaryPath> paths) throws DictionaryException{
        Session hs = null;
        Transaction tx = null;
        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();
            
            List<IElementBase> existing = HibernateUtils.safeList(paths, hs, 
                new HibernateUtils.SafeQuery3<DictionaryPath, Long, IElementBase>() {
                    protected String getQueryString(){
                        return "from DictionaryElementBase b "
                        +   "where b.path.pathHash in (:paths) "
                        +   "and b.timeRelation.activeTo > :asOf "
                        +   "and b.timeRelation.activeFrom <= :asOf "
                        +   "and b.enrollment = :enrollment";
                    }
                
                    protected Long convert(DictionaryPath value) {
                        return new Long(value.hashCode());
                    }
                   
                    protected void setQuery(Query q, Collection<Long> values) throws HibernateException{
                        q.setParameterList("paths", values)
                         .setParameter("asOf", new Date(), DateToLongUserType.TYPE)
                         .setParameter("enrollment", Enrollment.this);
                    }
                }
            );
            
            Set<DictionaryPath> remaining = new HashSet<DictionaryPath>(paths);
            List<IReferenceable> res = new ArrayList<IReferenceable>(paths.size());
            for ( IElementBase element : existing ) {
                if (remaining.remove(element.getPath())) {
                    // Since we query by path's hash code and not by path,
                    // there may be "stray" elements in the result; we ignore them.
                    ((Enrollment)element.getEnrollment()).setDictionary(dictionary);
                    res.add(element);
                }
            }
            for (final DictionaryPath path : remaining) {
                res.add(new IReferenceable() {
                    /**
                     * @see IReferenceable#accept(IElementVisitor)
                     */
                    public void accept(IElementVisitor visitor) {
                        visitor.visitProvisionalReference(this);
                    }

                    /**
                     * @see IReferenceable#getPath()
                     */
                    public DictionaryPath getPath() {
                        return path;
                    }

                    public String toString() {
                        return path.getName();
                    }
                });
            }
            return res;
        } catch (HibernateException cause) {
            throw new DictionaryException(
                "Unable to create provisional references.", cause
            );
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }

    /**
     * @see IEnrollment#getType()
     */
    @Override
    public String getType() {
        return enrollmentType;
    }

    /**
     * @see IEnrollment#setType(String)
     */
    @Override
    public void setType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    /**
     * Obtains the external mappings.
     * @return Returns the externalMappings.
     */
    Map<IElementField, ExternalFieldMapping> getExternalMappings() {
        return externalMappings;
    }

    /**
     * This is a setter for the <code>dictionary</code> field.
     * The <code>Dictionary</code> calls this method when it retrieves
     * the <code>Enrollment</code> from the database.
     * @param dictionary the <code>Dictionary</code>
     * that holds this enrollment.
     */
    void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Sets the external mappings. This method is called by Hibernate,
     * and populates the mappingsByType map.
     * @param externalMappings The externalMappings to set.
     */
    void setExternalMappings(Map<IElementField, ExternalFieldMapping> externalMappings) {
        mappingsByType = new HashMap<IElementType, MappingsForType>();
        this.externalMappings = externalMappings;
        for (IElementField f : externalMappings.keySet()) {
            ElementField field = (ElementField) f;
            ExternalFieldMapping mapping = externalMappings.get(field);
            MappingsForType perTypeMappings = mappingsByType.get(field.getParentType());
            if (perTypeMappings == null) {
                perTypeMappings = new MappingsForType();
                mappingsByType.put(field.getParentType(), perTypeMappings);
            }
            perTypeMappings.addStoredMapping(field, mapping.getExternalName());
        }
    }

    /**
     * Implementation for the getXXXPropertyNames methods.
     * @param type the type for which to get the names.
     * @return an array of names of properties of the specific type,
     * or empty array when no properties of the given type exist.
     */
    private String[] getPropertyNames(EnrollmentPropertyType type) {
        List<String> list = new ArrayList<String>();
        for (IEnrollmentProperty prop : properties.values()) {
            EnrollmentProperty property = (EnrollmentProperty) prop;
            if (property.getType() == type) {
                list.add(property.getName());
            }
        }
        String[] res = list.toArray(new String[list.size()]);
        Arrays.sort(res);
        return res;
    }

    /**
     * Obtains a property of the specific type with the specific name.
     * @param name the name of the desired property.
     * @param type the type for which to get the property.
     * @param create a flag indicting that a new property
     * should be created if it does not exist.
     * @return the specified property if it exists.
     * @throws NullPointerException if the name is null.
     * @throws IllegalArgumentException if a property
     * of a different type with the same name exists.
     */
    private IEnrollmentProperty getSpecificProperty(String name, EnrollmentPropertyType type, boolean create) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        EnrollmentProperty res = (EnrollmentProperty)properties.get(name);
        if (res != null && res.getType() != type) {
            throw new IllegalArgumentException(
                "Property with the specified name exists, "
            +   "but it has a different type ("
            +   res.getType()+" instead of "+type+")"
            );
        }
        if (res == null) {
            if (create) {
                res = new EnrollmentProperty(name, this, type);
                properties.put(name, res);
                return res;
            } else {
                return IEnrollmentProperty.DUMMY;
            }
        } else {
            return res;
        }
    }

    /**
     * Instances of this class represent mappings from field to
     * its name, and from a name back to the corresponding field.
     */
    private class MappingsForType {

        private Map<IElementField,String> fieldToName = new HashMap<IElementField,String>();
        private Map<String,Set<IElementField>> nameToField = new HashMap<String,Set<IElementField>>();

        public void addMapping(ElementField field, String externalName) {
            String oldName = fieldToName.get(field);
            if (oldName != null) {
                Set<IElementField> oldFields = nameToField.get(oldName);
                if(oldFields != null){
                    oldFields.remove(field);
                }
            }
            fieldToName.put(field, externalName);
            
            Set<IElementField> fields = nameToField.get(externalName);
            if (fields == null) {
                fields = new HashSet<IElementField>();
                nameToField.put(externalName, fields);
            }
            fields.add(field);
            ExternalFieldMapping mapping = externalMappings.get(field);
            if (mapping == null) {
                mapping = new ExternalFieldMapping(Enrollment.this, field, externalName);
                externalMappings.put(field, mapping);
            } else {
                mapping.setExternalName(externalName);
            }
        }

        public void removeMapping(ElementField field) {
            String externalName = fieldToName.remove(field);
            nameToField.get(externalName).remove(field);
            externalMappings.remove(field);
        }

        public void addStoredMapping(ElementField field, String externalName) {
            String duplicateName = fieldToName.get(field);
            if ( duplicateName != null ) {
                throw new IllegalArgumentException("Field "+field.getName()+" already has a mapping: "+duplicateName);
            }
            fieldToName.put( field, externalName );
            Set<IElementField> fields = nameToField.get(externalName);
            if (fields == null) {
                fields = new HashSet<IElementField>();
                nameToField.put(externalName, fields);
            }
            fields.add(field);
        }

        public IElementField[] getField(String externalName) {
            Set<IElementField> res = nameToField.get(externalName);
            if (res == null) {
                throw new IllegalArgumentException("Undefined external name: " + externalName);
            }
            return res.toArray(new IElementField[res.size()]);
        }

        public String getFieldName(IElementField field) {
            return fieldToName.get(field);
        }

        public String[] getExternalNames() {
            Set<String> names = nameToField.keySet();
            return names.toArray(new String[names.size()]);
        }

    }

    /**
     * Returns the dictionary with which this enrollment is associated.
     * @return the dictionary with which this enrollment is associated.
     */
    public IDictionary getDictionary() {
        return dictionary;
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object other) {
        if ( other instanceof Enrollment ) {
            Enrollment e = (Enrollment)other;
            if ( e.id != null ) {
                return e.id.equals(id);
            } else {
                return e == this;
            }
        } else {
            return false;
        }
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return ObjectHelper.nullSafeHashCode(id);
    }

    /**
     * @see IEnrollment#getIsRecurring()
     */
    @Override
    public boolean getIsRecurring() {
        return this.isRecurring;
    }

    /**
     * @see IEnrollment#setIsRecurring(String)
     */
    @Override
    public void setIsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    /**
     * @see IEnrollment#getIsActive()
     */
    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     * @see IEnrollment#setIsActive(String)
     */
    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * @see IEnrollment#getIsSycing()
     */
    @Override
    public boolean getIsSyncing() {
        return this.isSyncing;
    }

    /**
     * @see IEnrollment#setIsSycing(String)
     */
    @Override
    public void setIsSyncing(boolean isSyncing) {
        this.isSyncing = isSyncing;
    }

    /**
     * Get the update record
     * @return IUpdateRecord
     */
    public IUpdateRecord getUpdateRecord() throws DictionaryException {
        Session hs = null;
        Transaction tx = null;
        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();
            return (IUpdateRecord) hs.createQuery(
                "from UpdateRecord b "
            +   "where "
            +   "b.timeRelation.activeTo > :asOf "
            +   "and b.timeRelation.activeFrom <= :asOf "
            +   "and b.enrollment = :enrollment"
            )
            .setParameter("asOf", new Date(), DateToLongUserType.TYPE)
            .setParameter("enrollment", this)
            .uniqueResult();
        } catch ( HibernateException cause ) {
            throw new DictionaryException(
                "Unable to fetch enrollment status.", cause
            );
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }

    public List<IEnrollmentDeltaCookie> getDeltaCookies() throws DictionaryException {
        Session hs = null;
        Transaction tx = null;

        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();

            return hs.createQuery( "from EnrollmentDeltaCookie c where c.enrollment = :enrollment" )
                .setParameter("enrollment", this)
                .list();
        } catch (HibernateException cause) {
            throw new DictionaryException("Unable to fetch enrollment delta cookies", cause);
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }

    public IEnrollmentDeltaCookie getDeltaCookieByType(String type) throws DictionaryException {
        Session hs = null;
        Transaction tx = null;

        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();

            return (IEnrollmentDeltaCookie) hs.createQuery( "from EnrollmentDeltaCookie c where c.deltaType = :type and c.enrollment = :enrollment" )
                .setParameter("type", type)
                .setParameter("enrollment", this)
                .uniqueResult();
        } catch (HibernateException cause) {
            throw new DictionaryException("Unable to fetch enrollment delta cookies", cause);
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }

    public void setDeltaCookie(IEnrollmentDeltaCookie cookie) throws DictionaryException {
        Session hs = null;
        Transaction tx = null;

        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();

            hs.saveOrUpdate(cookie);
        } catch (HibernateException cause) {
            throw new DictionaryException("Unable to save enrollment delta cookie", cause);
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }

    public void deleteDeltaCookies() throws DictionaryException {
        Session hs = null;
        Transaction tx = null;

        try {
            hs = dictionary.getCountedSession();
            tx = hs.beginTransaction();

            for (IEnrollmentDeltaCookie cookie : getDeltaCookies()) {
                hs.delete(cookie);
            }
        } catch (HibernateException cause) {
            throw new DictionaryException("Unable to delete enrollment delta cookie", cause);
        } finally {
            dictionary.tryCommitAbortOnError(tx);
        }
    }
    
    public void setNextSyncTime(Calendar nextSyncTime) {
        this.nextSyncTime = nextSyncTime;
    }
    
    public Calendar getNextSyncTime() {
        return nextSyncTime;
    }

    @Override
    public String toString() {
        return "com.bluejungle.dictionary.Enrollment (" + id + ", " + domainName + ")";
    }
    
    /*
     * FIXME, could be more consistent if apply the same logic on the input too 
     */
    public static String filterDomainName(String string) {
        StringBuilder sb = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (!Character.isWhitespace(c) && ! Character.isISOControl(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public void updateIsSyncing(boolean isSyncing) throws DictionaryException {
        try {
            Connection connection = dictionary.getCountedSession().connection();
            
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "update dict_enrollments set issyncing = ? where id = ?")) {
                preparedStatement.setBoolean(1, isSyncing);
                preparedStatement.setLong(2, id);
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw ex;
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException | HibernateException ex) {
            throw new DictionaryException(String.format("Error while updating isSyncing status for the enrollment %s", domainName), ex);
        } finally {
            dictionary.closeCurrentSession();
        }
    }
}
