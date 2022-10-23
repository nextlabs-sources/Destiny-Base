/*
 * Created on May 31, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentSyncException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.EnrollerBase;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.util.SyncResultEnum;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IDictionaryIterator;
import com.bluejungle.dictionary.IElement;
import com.bluejungle.dictionary.IElementBase;
import com.bluejungle.dictionary.IEnrollment;
import com.bluejungle.dictionary.IEnrollmentSession;
import com.bluejungle.dictionary.IMGroup;
import com.bluejungle.framework.utils.IPair;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADElementCreator.AzureADElementType;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl.AzureADElementCreator;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl.AzureADEnrollmentInitializer;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl.AzureADEnrollmentWrapperImpl;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl.AzureADRetriever;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AzureADEnroller extends EnrollerBase {
    private static final int DEFAULT_BATCH_SIZE = 512;
	private static final Log LOG = LogFactory.getLog(AzureADEnroller.class);
    
    private AzureADEnrollmentWrapperImpl wrapper;
    private AzureADElementCreator creator;
    private JsonParser parser = new JsonParser();
    private final int batchSize;

    
    public AzureADEnroller() {
        this(DEFAULT_BATCH_SIZE);
    }

    public AzureADEnroller(int batchSize) {
        this.batchSize = batchSize;
    }
    
	@Override
	protected Log getLog() {
		return LOG;
	}

    @Override
    protected void preSync(IEnrollment enrollment, IDictionary dictionary, IEnrollmentSession session) throws EnrollmentValidationException, DictionaryException {
        wrapper = new AzureADEnrollmentWrapperImpl(enrollment, dictionary);
        status = STATUS_PRE_SYNC;
        creator = new AzureADElementCreator(wrapper, dictionary);
    }

    @Override
    public void process(IEnrollment enrollment, Map<String, String[]> properties,
                 IDictionary dictionary) throws EnrollmentValidationException, DictionaryException {
        super.process(enrollment, properties, dictionary);
        new AzureADEnrollmentInitializer(enrollment, properties, dictionary).setup();
    }
    
    @Override
    protected void internalSync(IEnrollment enrollment,
                                IDictionary dictionary,
                                IEnrollmentSession session,
                                SyncResult syncResult) throws EnrollmentSyncException {
        syncResult.newCount = 0;
        syncResult.deleteCount = 0;

        syncResult.changeCount = 0;
        syncResult.nochangeCount = 0;
        syncResult.ignoreCount = 0;
        syncResult.failedCount = 0;
        syncResult.totalCount  = 0;

        status = STATUS_FETCHING_DATA;

        try {
            int count = 0;
            int incrementalCount;
            
            IAzureADRetriever retriever = getAzureADRetriever(wrapper, dictionary, batchSize);

            if (wrapper.enrollUsers()) {
                getLog().info("Enroll ADD users...");
                do {
                    incrementalCount = retrieveElements(retriever, AzureADElementType.USER_TYPE, session, syncResult);
                    getLog().debug("Read " + incrementalCount + " user elements");
                    count += incrementalCount;
                } while(incrementalCount != 0);
            }

            if (wrapper.enrollGroups()) {
                getLog().info("Enroll ADD groups...");
                do {
                    incrementalCount = retrieveElements(retriever, AzureADElementType.GROUP_TYPE, session, syncResult);
                    getLog().debug("Read " + incrementalCount + " group elements");
                    count += incrementalCount;
                } while (incrementalCount != 0);
            } else {
                getLog().info("Deleting any existing AAD groups...");
                // If we aren't enrolling groups we should delete all the groups we currently have
                int deletedGroups = deleteActiveGroups(enrollment, dictionary, session);
                getLog().debug("Deleted " + deletedGroups + " groups");
            }
            
            getLog().debug("Read " + count + " user and group elements");

        } catch (DictionaryException de) {
            throw new EnrollmentSyncException(de);
        } catch (EnrollmentValidationException eve) {
            throw new EnrollmentSyncException(eve);
        }
        
        status = STATUS_DONE;
        syncResult.success = true;
    }

    /**
     * Deletes all active groups (used when enroll.groups=false)
     */
    private int deleteActiveGroups(IEnrollment enrollment, IDictionary dictionary, IEnrollmentSession session) throws DictionaryException {
        IDictionaryIterator<? extends IMGroup> itor = dictionary.getEnumeratedGroups(dictionary.condition(enrollment), null, new Date(), null);
        
        ArrayList<IMGroup> groupsToDelete = new ArrayList<>();
        
        while(itor.hasNext()) {
            groupsToDelete.add(itor.next());
        }
        
        session.beginTransaction();
        session.deleteElements(groupsToDelete);
        session.commit();

        return groupsToDelete.size();
    }
    
    private int retrieveElements(IAzureADRetriever retriever, AzureADElementType type, IEnrollmentSession session, SyncResult syncResult) throws EnrollmentSyncException, DictionaryException {
        int counter = 0;

        retriever.setRetrievalMode(type);
        
        try {
            ArrayList<IElementBase> elementsToSave = new ArrayList<>();
            ArrayList<IElementBase> elementsToDelete = new ArrayList<>();
            
            for (JsonObject entry : retriever) {
                // Making the retriever be an Iterable is nice, but we have to work a little
                // to extract exceptions
                if (retriever.hasRetrievalException()) {
                    throw new EnrollmentSyncException(retriever.getRetrievalException()); 
                }

                IPair<SyncResultEnum, ? extends IElementBase> result = creator.createElement(type, entry);

                if (result != null) {
                    counter++;
                    updateSyncResult(result.first(), result.second(),
                                     elementsToSave, elementsToDelete, syncResult);
                }

                if (elementsToSave.size() == batchSize) {
                    session.beginTransaction();
                    session.saveElements(elementsToSave);
                    session.commit();

                    elementsToSave.clear();
                }

                if (elementsToDelete.size() == batchSize) {
                    session.beginTransaction();
                    session.deleteElements(elementsToDelete);
                    session.commit();

                    elementsToDelete.clear();
                }
            }

            if (!elementsToSave.isEmpty() || !elementsToDelete.isEmpty()) {
                session.beginTransaction();
                session.saveElements(elementsToSave);
                session.deleteElements(elementsToDelete);
                session.commit();
            }
        } catch (DictionaryException de) {
            throw new EnrollmentSyncException(de);
        }
        
        retriever.close();
        return counter;
    }

    @Override
    public String getEnrollmentType() {
        return "AZURE_AD";
    }

    // This can be overridden by unit tests
    public IAzureADRetriever getAzureADRetriever(IAzureADEnrollmentWrapper enrollmentWrapper, IDictionary dictionary, int batchSize) throws EnrollmentValidationException {
        return new AzureADRetriever(enrollmentWrapper, dictionary, batchSize);
    }
}
