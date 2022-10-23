/*
 * Created on Jul 21, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.applicationusers.repository.hibernateimpl;

import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager;
import com.bluejungle.destiny.container.shared.applicationusers.repository.IAccessGroup;
import com.bluejungle.destiny.container.shared.applicationusers.repository.IApplicationUser;
import com.nextlabs.framework.utils.PasswordHashUtils;

import java.nio.charset.StandardCharsets;

import java.sql.Timestamp;

/**
 * @author sgoldstein
 */

public class SuperUserDO implements IApplicationUser {

    private static final String UNIQUE_NAME_SEPERATOR = "@";

    private Long id = new Long(IApplicationUserManager.RESERVED_SUPER_USER_ID);
    private byte[] password;
    private String login;
    private String firstName;
    private AccessDomainDO accessDomain;
    private String lastName;
    private IAccessGroup primaryAccessGroup;
    private Timestamp createdDate;
    private long lastUpdatedBy;
    private Timestamp lastUpdated;
    private long createdBy;
    private String displayName;
    private Boolean hideSplash = false;
    private Long lastLoggedTime;
    private String email;
    private String initialLoginDone = "N";
    private Integer failedLoginAttempts = 0;
    private Boolean locked = false;
    private Boolean manualProvision = true;

    /**
     * Create an instance of SuperUserDO
     * 
     * @param login
     * @param firstName
     * @param lastName
     * @param password
     * @param domain
     */
    SuperUserDO(String login, String firstName, String lastName, String password, AccessDomainDO domain) {
        if (login == null) {
            throw new NullPointerException("login cannot be null.");
        }

        if (firstName == null) {
            throw new NullPointerException("firstName cannot be null.");
        }

        if (lastName == null) {
            throw new NullPointerException("lastName cannot be null.");
        }

        if (password == null) {
            throw new NullPointerException("password cannot be null.");
        }

        if (domain == null) {
            throw new NullPointerException("domain cannot be null.");
        }

        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessDomain = domain;
        setPassword(password);
    }

    /**
     * 
     * Create an instance of SuperUserDO.  For Hibernate User Only
     */
    SuperUserDO() {

    }
    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.repository.IApplicationUser#getDestinyId()
     */
    public Long getDestinyId() {
        return this.id;
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.repository.IApplicationUser#isManuallyCreated()
     */
    public boolean isManuallyCreated() {
        return true;
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.core.IUser#getFirstName()
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.core.IUser#getLastName()
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.core.IUser#getLogin()
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.core.IUser#getUniqueName()
     */
    public String getUniqueName() {
        StringBuffer uniqueNameBuffer = new StringBuffer(this.getLogin());
        uniqueNameBuffer.append(UNIQUE_NAME_SEPERATOR);
        uniqueNameBuffer.append(this.getDomainName());

        return uniqueNameBuffer.toString();
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.core.IDomainEntity#getDomainName()
     */
    public String getDomainName() {
        return this.accessDomain.getName();
    }

    /**
     * Retrieve the id.
     * 
     * @return the id.
     */
    Long getId() {
        return this.id;
    }

    /**
     * Set the id
     * 
     * @param id
     *            The id to set.
     */
    void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @param firstName
     */
    void setFirstName(String firstName) {
        if (firstName == null) {
            throw new NullPointerException("firstName cannot be null.");
        }

        this.firstName = firstName;
    }

    /**
     * set lastName attribute of user DO
     * 
     * @param the
     *            lastName of user
     */
    void setLastName(String lastName) {
        if (lastName == null) {
            // Work around for Oracle
            lastName = "";
        }
        this.lastName = lastName;
    }
    
    /**
     * access password attribute of user DO. For Hibernate use only
     * 
     * @return the password of user
     */
    byte[] getPassword() {
        return this.password;
    }

    /**
     * set password attribute of user DO. For Hibernate User Only
     * 
     * @param the
     *            password of user
     */
    void setPassword(byte[] password) {
        this.password = password;
    }

    /**
     * Set a new password
     * 
     * @param password
     *            the password to set
     */
    void setPassword(String password) {
        if (password == null) {
            throw new NullPointerException("password cannot be null.");
        }

        if (password.startsWith(PasswordHashUtils.PBKDF2_HASH_PREFIX)) {
            this.password = password.getBytes();
        } else {
            this.password = PasswordHashUtils.getInstance().hashPassword(password).getBytes();
        }
    }

    /**
     * Determine if the provided password is valid
     * 
     * @param password
     *            the passowrd to check
     * @return true if the passoword is valid; false otherwise
     */
    boolean isPasswordValid(String password) {
        if (password == null) {
            throw new NullPointerException("password cannot be null.");
        }
        if (this.password == null) {
            // account is disabled.
            return false;
        }

        return PasswordHashUtils.getInstance().validatePassword(password, new String(this.password, StandardCharsets.UTF_8));
    }
    
    /**
     * set loginName attribute of user DO. Required by Hibernate
     * 
     * @param the
     *            loginName of user
     */
    void setLogin(String loginName) {
        if (loginName == null) {
            throw new NullPointerException("loginName cannot be null.");
        }
        this.login = loginName;
    }
    
    /**
     * Retrieve the accessDomain. Required for Hibernate
     * 
     * @return the accessDomain.
     */
    AccessDomainDO getAccessDomain() {
        return this.accessDomain;
    }

    /**
     * Set the accessDomain. Required for Hibernate
     * 
     * @param accessDomain
     *            The accessDomain to set.
     */
    void setAccessDomain(AccessDomainDO accessDomain) {
        if (accessDomain == null) {
            throw new NullPointerException("accessDomain cannot be null.");
        }
        this.accessDomain = accessDomain;
    }

    /**
     * Set the primary access group. Required for Hibernate. May be null
     * 
     * @param primaryAccessGroup
     *            the access group to set
     */
    void setHibernatePrimaryAccessGroup(IAccessGroup primaryAccessGroup) {
        this.primaryAccessGroup = primaryAccessGroup;
    }

    /**
     * Set the primary access group. Required for Hibernate. Having a different
     * setter allows for the "hasPrimaryAccessGroup" behavior and avoids
     * returning null to clients, making the code more robust
     * 
     * @return The primary access group for this user; null if it doesn't have
     *         one
     * 
     */
    IAccessGroup getHibernatePrimaryAccessGroup() {
        return this.primaryAccessGroup;
    }

    /**
     * Clear the primary access group setting. Results in this user not having a
     * primary access group
     * 
     */
    void clearPrimaryAccessGroup() {
        this.primaryAccessGroup = null;
    }

    /**
     * Set the primaryAccessGroup
     * 
     * @param primaryAccessGroup
     *            The primaryAccessGroup to set.
     */
    void setPrimaryAccessGroup(IAccessGroup primaryAccessGroup) {
        if (primaryAccessGroup == null) {
            throw new NullPointerException("primaryAccessGroup cannot be null.");
        }
        this.primaryAccessGroup = primaryAccessGroup;
    }
    
    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.repository.IApplicationUser#getPrimaryAccessGroup()
     */
    IAccessGroup getPrimaryAccessGroup() {
        if (this.primaryAccessGroup == null) {
            throw new IllegalStateException("This user does not have a primary access group");
        }
        return this.primaryAccessGroup;
    }

    /**
     * @see com.bluejungle.destiny.container.shared.applicationusers.repository.IApplicationUser#hasPrimaryAccessGroup()
     */
    boolean hasPrimaryAccessGroup() {
        return (this.primaryAccessGroup != null);
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getHideSplash() {
        return hideSplash;
    }

    public void setHideSplash(Boolean hideSplash) {
        this.hideSplash = hideSplash;
    }

    public Long getLastLoggedTime() {
        return lastLoggedTime;
    }

    public void setLastLoggedTime(Long lastLoggedTime) {
        this.lastLoggedTime = lastLoggedTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInitialLoginDone() {
        return initialLoginDone;
    }

    public void setInitialLoginDone(String initialLoginDone) {
        this.initialLoginDone = initialLoginDone;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getManualProvision() {
        return manualProvision;
    }

    public void setManualProvision(Boolean manualProvision) {
        this.manualProvision = manualProvision;
    }

}
