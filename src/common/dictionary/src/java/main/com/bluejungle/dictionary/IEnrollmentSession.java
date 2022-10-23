/*
 * All sources, binaries and HTML pages (C) Copyright 2006 by Blue Jungle Inc,
 * Redwood City, CA. Ownership remains with Blue Jungle Inc.
 * All rights reserved worldwide.
 *
 * @author sergey
 */

package com.bluejungle.dictionary;

import java.util.Collection;

/**
 * This interface establishes the contract for updating elements
 * and groups associated with a particular enrollment.
 */
public interface IEnrollmentSession {
    /**
     * Property common to all enrollments which specifies if we should
     * delete inactive group members or merely set their active_to
     * value to the past (the default)
     *
     * Normally these attribute names go under
     * BasicLDAPEnrollmentProperties, but that class isn't accessible
     * from here.
     */
    public static final String DELETE_INACTIVE_GROUP_MEMBERS = "delete.inactive.group.members";
    
    /**
     * Begins a transaction if it hasn't already been started.
     * If a transaction is already in progress, nothing happens.
     */
    void beginTransaction() throws DictionaryException;

    /**
     * Commits the current transaction. If there is no active
     * transaction at this time, an exception is thrown.
     */
    void commit() throws DictionaryException;

    /**
     * Rolls back the current transaction. If there is no active
     * transaction at this time, an exception is thrown.
     */
    void rollback() throws DictionaryException;

    /**
     * Checks if there is an active transaction at this time.
     *
     * @return true if there is an active transaction, false otherwise.
     */
    boolean hasActiveTransaction();

    /**
     * Saves or updates each element of the specified
     * <code>Collection</code> of elements and/or groups. 
     * @param elements The elements that need to be updated.
     */
    void saveElements(Collection<? extends IElementBase> elements) throws DictionaryException;

   /**
    * Deletes each element of the specified <code>Collection</code>
    * of elements and/or groups. 
    * @param elements The elements that need to be deleted.
    */
    void deleteElements(Collection<? extends IElementBase> elements) throws DictionaryException;

    /**
     * This method closes the session and deallocates
     * the resources associated with it.
     * @param success indicates whether the enrollment succeeded
     * or not (<code>true</code> means that the enrollment session
     * completed successfully; <code>false</code> means that it failed).
     */
    void close(boolean success, String errorMessage) throws DictionaryException;

}
