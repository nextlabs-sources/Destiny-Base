/*
 * Created on Jun 19, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentSyncException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.util.ElementCreator;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.util.SyncResultEnum;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.DictionaryKey;
import com.bluejungle.dictionary.DictionaryPath;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IElementBase;
import com.bluejungle.dictionary.IMElement;
import com.bluejungle.dictionary.IMElementBase;
import com.bluejungle.dictionary.IMGroup;
import com.bluejungle.dictionary.IReferenceable;
import com.bluejungle.framework.utils.IPair;
import com.bluejungle.framework.utils.Pair;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADElementCreator;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADEnrollmentWrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AzureADElementCreator extends ElementCreator implements IAzureADElementCreator {
	private static final Log LOG = LogFactory.getLog(AzureADElementCreator.class);
    
    // Should this be configurable? Mapping in .def file?
    private static final String AAD_DN_FIELD = "onPremisesDistinguishedName";
    private static final String AAD_DISPLAYNAME_FIELD = "displayName";
    
    private static final String GROUP_ID_KEY = "id";
    private static final String GROUP_NAME_KEY = "displayName";

    private static final String GROUP_LDAP_ENTRY_USER_MEMBERS_KEY = "users";
    private static final String GROUP_LDAP_ENTRY_GROUP_MEMBERS_KEY = "groups";
    private static final String GROUP_LDAP_ENTRY_DELETED_USER_MEMBERS_KEY = "deletedusers";
    private static final String GROUP_LDAP_ENTRY_DELETED_GROUP_MEMBERS_KEY = "deletedgroups";
    
    // Change to use objectGUIAttributeName. 
    private static final String USER_ID_KEY = "id";
    private static final String USER_PRINCIPAL_NAME_KEY = "userPrincipalName";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final String AAD_REMOVED_KEY = "@removed";
    private static final String GROUP_MEMBER_ID_KEY = "id";
    private static final String GROUP_MEMBERS_KEY = "members@delta";
    private static final String GROUP_MEMBER_TYPE_KEY = "@odata.type";
    private static final String GROUP_MEMBER_IS_USER_VALUE = "#microsoft.graph.user";
    private static final String GROUP_MEMBER_IS_GROUP_VALUE = "#microsoft.graph.group";

    private AzureADElementType elementTypes;
    
    public AzureADElementCreator(IAzureADEnrollmentWrapper enrollmentWrapper, IDictionary dictionary) throws EnrollmentValidationException {
        super(enrollmentWrapper, dictionary);
    }


    public IPair<SyncResultEnum, ? extends IElementBase> createElement(AzureADElementType type, JsonObject object) throws DictionaryException, EnrollmentSyncException {
        if (type == AzureADElementType.USER_TYPE) {
            return createUserElement(object);
        } else {
            return createGroupElement(object);
        }
    }
    
    /**
     * set the group members
     *
     * @param group the group to which members should be added
     * @param entry contains the members for the new group. This will consist of only changes
     *              for existing groups
     * @param isExistingGroup indicates if this is an already existing group or not
     *
     * @note this code is largely borrowed from ElementCreator. The
     * main difference is that we get deltas, just new members and
     * deleted members. ElementCreator.setGroupMembers expects a full
     * list.
     */
    @Override
    protected boolean setGroupMembers(IMGroup group, LDAPEntry entry, boolean isExistingGroup) throws EnrollmentSyncException {
        boolean isChanged = false;

        //oracle has limit on 1000 object in the query. 800 is not optimized but a large safe number.
        final int MAX_PATH_NUMBER = 800;

        // This string describes the error that might occur in the
        // action we are performing next, so if an exception is thrown
        // we can explain why
        String errorMessage = "determineAddedMembers";

        Queue<DictionaryPath> addedMembers = new LinkedList<DictionaryPath>();

        LDAPAttribute addedUsersAttr = entry.getAttribute(GROUP_LDAP_ENTRY_USER_MEMBERS_KEY);
        if (addedUsersAttr != null) {
            Enumeration<String> addedUsers = addedUsersAttr.getStringValues();
            while(addedUsers.hasMoreElements()) {
                addedMembers.add(createUserDictionaryPath(addedUsers.nextElement()));
            }
        }
        
        LDAPAttribute addedGroupsAttr = entry.getAttribute(GROUP_LDAP_ENTRY_GROUP_MEMBERS_KEY);
        if (addedGroupsAttr != null) {
            Enumeration<String> addedGroups = addedGroupsAttr.getStringValues();
            while(addedGroups.hasMoreElements()) {
                addedMembers.add(createGroupDictionaryPath(addedGroups.nextElement()));
            }
        }

        errorMessage = "addNewMembersToGroup";
        try {
            while (!addedMembers.isEmpty()) {
                Collection<DictionaryPath> pathsChunk = new LinkedList<DictionaryPath>();
                for (int i = 0; i < MAX_PATH_NUMBER & !addedMembers.isEmpty(); i++) {
                    pathsChunk.add(addedMembers.poll());
                }
                
                for (IReferenceable member : enrollment.getProvisionalReferences(pathsChunk)) {
                    group.addChild(member);
                    isChanged = true;
                }
            }
        } catch (DictionaryException e) {
            throw new EnrollmentSyncException(errorMessage, e, entry.getDN());
        }

        
        errorMessage = "determineDeletedMembers";
        Queue<DictionaryPath> deletedMembers = new LinkedList<DictionaryPath>();
        
        LDAPAttribute deletedUsersAttr = entry.getAttribute(GROUP_LDAP_ENTRY_DELETED_USER_MEMBERS_KEY);
        if (deletedUsersAttr != null) {
            Enumeration<String> deletedUsers = deletedUsersAttr.getStringValues();
            while(deletedUsers.hasMoreElements()) {
                deletedMembers.add(createUserDictionaryPath(deletedUsers.nextElement()));
            }
        }
        
        
        LDAPAttribute deletedGroupsAttr = entry.getAttribute(GROUP_LDAP_ENTRY_DELETED_GROUP_MEMBERS_KEY);
        if (deletedGroupsAttr != null) {
            Enumeration<String> deletedGroups = deletedGroupsAttr.getStringValues();
            while(deletedGroups.hasMoreElements()) {
                deletedMembers.add(createGroupDictionaryPath(deletedGroups.nextElement()));
            }
        }

        errorMessage = "removedDeletedMembersFromGroup";
        try {
            while (!deletedMembers.isEmpty()) {
                Collection<DictionaryPath> pathsChunk = new LinkedList<DictionaryPath>();
                for (int i = 0; i < MAX_PATH_NUMBER & !deletedMembers.isEmpty(); i++) {
                    pathsChunk.add(deletedMembers.poll());
                }
                
                for (IReferenceable member : enrollment.getProvisionalReferences(pathsChunk)) {
                    group.removeChild(member);
                    isChanged = true;
                }
            }
        } catch (DictionaryException e) {
            throw new EnrollmentSyncException(errorMessage, e, entry.getDN());
        }
        
        return isChanged;
    }
    
    private IPair<SyncResultEnum, ? extends IElementBase> createUserElement(JsonObject user) throws DictionaryException, EnrollmentSyncException {
        if (isDeleted(user)) {
            return createDeletedUserElement(user);
        }
        
        String userId = user.get(USER_ID_KEY).getAsString();
        String userPrincipalName = user.get(USER_PRINCIPAL_NAME_KEY).getAsString();

        DictionaryKey key = new DictionaryKey(userId.getBytes(DEFAULT_CHARSET));
        
        DictionaryPath path = createUserDictionaryPath(userId);
        
        String dn = getDistinguishedName(user);
           
        LDAPEntry ldapEntry = convertToLDAPEntry(user);

        LOG.debug("Creating element for " + userPrincipalName + " (" + userId + ")"); 
        return createElement(ldapEntry, USER_TYPE, key, dn, path);
    }

    private IPair<SyncResultEnum, ? extends IElementBase> createDeletedUserElement(JsonObject user) throws DictionaryException, EnrollmentSyncException {
        String userId = user.get(USER_ID_KEY).getAsString();

        DictionaryKey key = new DictionaryKey(userId.getBytes(DEFAULT_CHARSET));
        
        LDAPEntry ldapEntry = convertToLDAPEntry(user);
        
        LOG.debug("Creating delete element for " + userId); 
        IElementBase element = createDeleteElement(ldapEntry, key);
        
        // If we try to delete something that isn't there, is that
        // an error? It probably indicates something is wrong, but
        // since we don't want the entry in the first place let's
        // pretend it's okay
        return element != null ?
            new Pair<SyncResultEnum, IElementBase>(SyncResultEnum.DELETED_ENTRY, element) :
            new Pair<SyncResultEnum, IElementBase>(SyncResultEnum.IGNORED_ENTRY, element);
    }

    private IPair<SyncResultEnum, ? extends IElementBase> createDeletedGroupElement(JsonObject group) throws DictionaryException, EnrollmentSyncException {
        String groupId = group.get(GROUP_ID_KEY).getAsString();

        DictionaryKey key = new DictionaryKey(groupId.getBytes(DEFAULT_CHARSET));

        // The LDAP entry is largely irrelevant. The important bit is the key
        LDAPEntry ldapEntry = convertToLDAPEntry(group);
        
        LOG.debug("Creating delete element for " + groupId); 
        IElementBase element = createDeleteElement(ldapEntry, key);
        
        // If we try to delete something that isn't there, is that
        // an error? It probably indicates something is wrong, but
        // since we don't want the entry in the first place let's
        // pretend it's okay
        return element != null ?
            new Pair<SyncResultEnum, IElementBase>(SyncResultEnum.DELETED_ENTRY, element) :
            new Pair<SyncResultEnum, IElementBase>(SyncResultEnum.IGNORED_ENTRY, element);
    }
    
    private IPair<SyncResultEnum, ? extends IElementBase> createGroupElement(JsonObject group) throws DictionaryException, EnrollmentSyncException {
        if (isDeleted(group)) {
            return createDeletedGroupElement(group);
        }
        
        String groupId = group.get(GROUP_ID_KEY).getAsString();
        String groupName = group.get(GROUP_NAME_KEY).getAsString();

        DictionaryKey key = new DictionaryKey(groupId.getBytes(DEFAULT_CHARSET));

        DictionaryPath path = createGroupDictionaryPath(groupId);

        String dn = getGroupDistinguishedName(group);

        // Create a new LDAPEntry that describes the form of the group
        // including members. But we don't know all the members, just the changes. That's okay,
        // we have our own "setGroupMembers" method.
        ArrayList<String> newUserMembers = new ArrayList<>();
        ArrayList<String> newGroupMembers = new ArrayList<>();
        ArrayList<String> deletedUserMembers = new ArrayList<>();
        ArrayList<String> deletedGroupMembers = new ArrayList<>();

        extractGroupMembers(groupName, group.get(GROUP_MEMBERS_KEY), newUserMembers, newGroupMembers, deletedUserMembers, deletedGroupMembers);

        LDAPAttributeSet attrs = new LDAPAttributeSet();

        if (!newUserMembers.isEmpty()) {
            LDAPAttribute attr = new LDAPAttribute(GROUP_LDAP_ENTRY_USER_MEMBERS_KEY,
                                                   newUserMembers.toArray(new String[newUserMembers.size()]));
            attrs.add(attr);
        }
        
        if (!newGroupMembers.isEmpty()) {
            LDAPAttribute attr = new LDAPAttribute(GROUP_LDAP_ENTRY_GROUP_MEMBERS_KEY,
                                                   newGroupMembers.toArray(new String[newGroupMembers.size()]));
            attrs.add(attr);
        }

        if (!deletedUserMembers.isEmpty()) {
            LDAPAttribute attr = new LDAPAttribute(GROUP_LDAP_ENTRY_DELETED_USER_MEMBERS_KEY,
                                                   deletedUserMembers.toArray(new String[deletedUserMembers.size()]));
            
            attrs.add(attr);
        }
        
        if (!deletedGroupMembers.isEmpty()) {
            LDAPAttribute attr = new LDAPAttribute(GROUP_LDAP_ENTRY_DELETED_GROUP_MEMBERS_KEY,
                                                   deletedGroupMembers.toArray(new String[deletedGroupMembers.size()]));
            
            attrs.add(attr);
        }
        
        LDAPEntry ldapEntry = new LDAPEntry(dn, attrs);

        LOG.debug("Creating group element for " + groupName + " (" + groupId + ")");
        
        return createGroupElement(ldapEntry, key, dn, path);
    }


    private String getDistinguishedName(JsonObject userObject) {
        JsonElement dnElement = userObject.get(AAD_DN_FIELD);

        if (dnElement != null) {
            return dnElement.getAsString();
        }
        
        return "CN=" + getDisplayName(userObject) + ",OU=Users,DC=" + enrollmentWrapper.getEnrollment().getDomainName();
    }
    
    private String getGroupDistinguishedName(JsonObject groupObject) {
        return "CN=" + getDisplayName(groupObject) + ",OU=Groups,DC=" + enrollmentWrapper.getEnrollment().getDomainName();
    }
    
    @Override
    protected Object getSidValue(LDAPAttribute entry) {
        return entry.getStringValue();
    }

    private String getDisplayName(JsonObject object) {
        JsonElement displayName = object.get(AAD_DISPLAYNAME_FIELD);

        if (displayName == null) {
            return "<< MISSING DISPLAY NAME NAME >>";
        }
        
        return displayName.getAsString();
    }

    // This is a special attribute with fields are "extensionAttribute1", "extensionAttribute2", etc.
    // No idea why AAD does this instead of just making them their own attributes
    private static String ON_PREMISES_EXTENSION_ATTRIBUTES = "onPremisesExtensionAttributes";
    
    private LDAPEntry convertToLDAPEntry(JsonObject userObject) {
        LDAPAttributeSet attributes = new LDAPAttributeSet();

        for (Map.Entry<String, JsonElement> entry : userObject.entrySet()) {

            if (entry.getKey().equals(ON_PREMISES_EXTENSION_ATTRIBUTES)) {
                if (entry.getValue() != null) {
                    JsonObject extensionAttributes = entry.getValue().getAsJsonObject();

                    for (Map.Entry<String, JsonElement> extensionAttribute : extensionAttributes.entrySet()) {
                        LDAPAttribute attr = buildLDAPAttribute("onPremisesExtensionAttributes." + extensionAttribute.getKey(), extensionAttribute.getValue());
                        if (attr == null) {
                            continue;
                        }
                        attributes.add(attr);
                    }
                }
                continue;
            }
            
            LDAPAttribute attr = buildLDAPAttribute(entry.getKey(), entry.getValue());

            if (attr == null) {
                LOG.info("User " + getDisplayName(userObject) + " key " + entry.getKey() + " has null value. Skipping");
                continue;
            }
            attributes.add(attr);
        }
        
        return new LDAPEntry(getDistinguishedName(userObject), attributes);
    }

    private LDAPAttribute buildLDAPAttribute(String key, JsonElement element) {
        if (element == null) {
            return null;
        }
        
        if (element.isJsonArray()) {
            return new LDAPAttribute(key, buildArrayFromJsonArray(element.getAsJsonArray()));
        } else {
            // This might be a null, boolean, etc. toString() handles
            // them all, but puts "" around actual strings, which we
            // don't want
            return new LDAPAttribute(key, element.toString().replaceAll("^\"|\"$", ""));
        }
        
    }

    private String[] buildArrayFromJsonArray(JsonArray values) {
        String[] result = new String[values.size()];

        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).toString();
        }

        return result;
    }

    /**
     * Create a "delete" element. This is just the existing element from the dictionary
     *
     * See also ActiveDirectoryElementFactory
     */
    private IMElementBase createDeleteElement(LDAPEntry entry, DictionaryKey key) throws EnrollmentSyncException {
        if (key != null) {
            try {
                IMElementBase element = enrollmentWrapper.getEnrollment().getByKey(key, new Date());

                return element;
            } catch (DictionaryException e) {
                throw new EnrollmentSyncException(e, entry.getDN());
            }
        }

        return null;
    }

    /**
     * Extract group member information from an AAD group member
     * element. This will consist of new members and deleted members.
     * It will look something like this:
     *
     * "members@delta": [
     *      {
     *          "@odata.type":"#microsoft.graph.user",
     *          "id":"43d4e6df-7548-4f20-8616-0c10bf481241",
     *          "@removed": {
     *              "reason":"deleted"
     *          }
     *      },
     *      {
     *          "@odata.type":"#microsoft.graph.user",
     *          "id":"4721188c-68e2-4cd7-889a-c048c6a21499"
     *      }
     *  ]
     *
     *
     * @param groupName the name of the group
     * @param groupMembers the JSON element describing the group's members
     * @param newUserMembers the new user members will be recorded here
     * @param newGroupMembers the new group members will be recorded here
     * @param deletedUserMembers the deleted user members will be recorded here
     * @param deletedGroupMembers the deleted group members will be recorded here
     */
    private void extractGroupMembers(String groupName, JsonElement groupMembers, List<String> newUserMembers, List<String> newGroupMembers, List<String> deletedUserMembers, List<String> deletedGroupMembers) {
        if (groupMembers == null || !groupMembers.isJsonArray()) {
            return;
        }

        JsonArray members = groupMembers.getAsJsonArray();

        for (int i = 0; i < members.size(); i++) {
            JsonObject member = members.get(i).getAsJsonObject();

            String memberId = member.get(GROUP_MEMBER_ID_KEY).getAsString();
            String dataType = member.get(GROUP_MEMBER_TYPE_KEY).getAsString();
            
            boolean isDeleted = member.get(AAD_REMOVED_KEY) != null;
            
            if (dataType.equals(GROUP_MEMBER_IS_USER_VALUE)) {
                if (isDeleted) {
                    deletedUserMembers.add(memberId);
                } else {
                    newUserMembers.add(memberId);
                }
            } else if (dataType.equals(GROUP_MEMBER_IS_GROUP_VALUE)) {
                if (isDeleted) {
                    deletedGroupMembers.add(memberId);
                } else { 
                    newGroupMembers.add(memberId);
                }
            } else {
                LOG.info("Group " + groupName + " contains unknown member type " + dataType + " with id " + memberId);
            }
        }
    }

    private boolean isDeleted(JsonObject object) {
        // There's a reason associated with this key, but we don't care
        return object.get(AAD_REMOVED_KEY) != null;
    }

    private DictionaryPath createGroupDictionaryPath(String id) {
        return new DictionaryPath( new String[] { enrollment.getDomainName(), "groups", id } );
    }

    private DictionaryPath createUserDictionaryPath(String id) {
        return new DictionaryPath( new String[] { enrollment.getDomainName(), id } );
    }
}
