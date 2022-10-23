/*
 * Created on Mar 01, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread;

import com.bluejungle.dictionary.DictionaryException;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADElementCreator.AzureADElementType;

import com.google.gson.JsonObject;

public interface IAzureADRetriever extends Iterable<JsonObject> {
    void setRetrievalMode(AzureADElementType type);

    boolean hasRetrievalException();

    Exception getRetrievalException();

    void close() throws DictionaryException;
}
