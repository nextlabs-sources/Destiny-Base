package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jul 31, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

/**
 * A callback for any PDP call
 */
public interface IPDPGenericCallback<T>
{
    /**
     * Invoked by the evaluation code when the query operation has completed
     * @param result the appropriate enforcement result
     */
    void callback(T result);
}
