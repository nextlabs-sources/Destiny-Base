package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jul 27, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public interface IPDPReferenceRequest {
    /**
     * Get the action name from this reference request. Unlike the other entries,
     * this will be an action rather than a reference to an action
     *
     * @return the action name
     */
    public String getAction();

    /**
     * Get the reference name of the user category
     *
     * @return the user reference name. null if it does not exist
     */
    public String getUserReference();

    /**
     * Get the reference name of the application category
     *
     * @return the application reference name. null if it does not exist
     */
    public String getApplicationReference();

    /**
     * Get the reference name of the host category
     *
     * @return the host reference name. null if it does not exist
     */
    public String getHostReference();

    /**
     * Get the reference name of the from resource. Most applications
     * don't have the concept of "from" and "to" resources, so we have
     * this alternate name
     *
     * @return the from resource reference name. null if it does not exist
     */
    public String getResourceReference();

    /**
     * Get the reference name of the from resource
     *
     * @return the from resource reference name. null if it does not exist
     */
    public String getFromResourceReference();

    /**
     * Get the reference name of the to resource (typically does not exist)
     *
     * @return the to resource reference name. null if it does not exist
     */
    public String getToResourceReference();

    /**
     * Get the reference name of any additional data
     *
     * @return the additional data reference name. null if it does not exist
     */
    public String[] getAdditionalDataReferences();
}
