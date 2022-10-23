/*
 * Created on Jun 15, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.EnrollmentDeltaCookie;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IEnrollmentDeltaCookie;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.AzureADConnectionException;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADEnrollmentWrapper;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADElementCreator.AzureADElementType;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADRetriever;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AzureADRetriever implements IAzureADRetriever {
	private static final Log LOG = LogFactory.getLog(AzureADRetriever.class);
    
    private static final String GRAPH = "https://graph.microsoft.com";
    private static final String GRAPH_USERS_QUERY =  GRAPH + "/beta/users/delta";
    private static final String GRAPH_GROUPS_QUERY = GRAPH + "/v1.0/groups/delta?$select=displayName,description,transitiveMembers";
    private static final String ACCEPT_RESPONSE_FORMAT = "application/json";
    
    private static final String ENTITY_ID = "id";
    
    private Exception retrievalException = null;
    private IAzureADEnrollmentWrapper enrollmentWrapper;
    private final JsonParser parser = new JsonParser();
    private final int batchSize;
    private IRetrievalIterator iterator;
    private IDictionary dictionary;
    private IEnrollmentDeltaCookie enrollmentDeltaCookie = null;
    private final String configuredUserExtendedAttributesQuery;
    private final AzureADAuthenticator authenticator;
    
    public AzureADRetriever(IAzureADEnrollmentWrapper enrollmentWrapper, IDictionary dictionary, int batchSize) throws EnrollmentValidationException {
        this.enrollmentWrapper = enrollmentWrapper;
        this.batchSize = batchSize;
        this.dictionary = dictionary;

        try {
            this.authenticator = new AzureADAuthenticator(this.enrollmentWrapper);
        } catch (AzureADConnectionException ace) {
            throw new EnrollmentValidationException("Unable to get AzureADAuthenticator: " + ace.getMessage(), ace);
        }
        
        this.configuredUserExtendedAttributesQuery = enrollmentWrapper.getUserExtendedAttributesQuery();
        this.iterator = null;
        
    }

    public void setRetrievalMode(AzureADElementType type) {
        /* Gotta love Microsoft. Information for a single group can be
         * split into multiple separate non-adjacent JsonElements.
         * This plays badly with the enrollment code so the best thing
         * to do is suck down all the group information and combine
         * the information for similar elements
         * 
         * Update: This is now true for users as well. BatchRetrievalIterator
         * must be created for everything until a better solution is created
         */
        this.iterator = new BatchRetrievalIterator(type);
    }
    
    @Override
    public Iterator<JsonObject> iterator() {
        return iterator;
    }
    
    /**
     * Given the information in the enrollmentWrapper, construct an
     * appropriate URL specification for retrieving the relevant
     * attributes for all users
     *
     * To get the extended attributes we are currently using a beta
     * api. This might change at any time (thanks, Microsoft) so we
     * have the option to configure a different query in the
     * properties file.
     *
     * We could modify the query to ensure that the response includes
     * just the attributes we need, but that adds complicated for
     * limited benefit (a slightly smaller response, and deltas make
     * that largely irrelevant anyway)
     */
    private String buildUsersURLSpec() {
        return configuredUserExtendedAttributesQuery == null ? GRAPH_USERS_QUERY : configuredUserExtendedAttributesQuery;
    }

    /**
     * Given the information in the enrollmentWrapper, construct an appropriate URL
     * specification for retrieving the relevant group information
     */
    private String buildGroupsURLSpec() {
        return GRAPH_GROUPS_QUERY;
    }
    
    private HttpURLConnection buildConnectionRequest(String spec) throws AzureADConnectionException {
        try {
            URL url = new URL(spec);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + authenticator.getAccessToken());
            conn.setRequestProperty("Accept", ACCEPT_RESPONSE_FORMAT);
            int httpResponseCode = conn.getResponseCode();
            
            if (httpResponseCode != HttpURLConnection.HTTP_OK) {
                throw new AzureADConnectionException("Unable to connect: " + httpResponseCode);
            }
            
            return conn;
        } catch (MalformedURLException mue) {
            throw new AzureADConnectionException(mue);
        } catch (IOException ioe) {
            throw new AzureADConnectionException(ioe);
        }
    }
    
    private String getResponse(HttpURLConnection conn) throws IOException {
        return getResponse(conn, true);
    }
    
    private String getResponse(HttpURLConnection conn, boolean isSuccess) throws IOException {
        BufferedReader reader = null;
        try {
            if (isSuccess) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            
            return stringBuffer.toString();
        } finally {
            reader.close();
        }
    }

    public boolean hasRetrievalException() {
        return retrievalException != null;
    }
    
    public Exception getRetrievalException() {
        return retrievalException;
    }
    
    private void setRetrievalException(Exception e) {
        retrievalException = e;
    }

    /**
     * Finishes the retrieval session. Saves the delta link to the
     * database to be used for the next enrollment sync
     */
    public void close() throws DictionaryException {
        String deltaLink = iterator.getDeltaLink();

        if (deltaLink != null) {
            if (enrollmentDeltaCookie == null) {
                EnrollmentDeltaCookie cookie = new EnrollmentDeltaCookie(enrollmentWrapper.getEnrollment());
                if (iterator.getRetrievalType() == AzureADElementType.USER_TYPE) { 
                    cookie.setDeltaType(IEnrollmentDeltaCookie.USERS);
                } else if (iterator.getRetrievalType() == AzureADElementType.GROUP_TYPE) {
                    cookie.setDeltaType(IEnrollmentDeltaCookie.GROUPS);
                }

                enrollmentDeltaCookie = cookie;
            }
            
            enrollmentDeltaCookie.setCookie(deltaLink);
            
            enrollmentWrapper.getEnrollment().setDeltaCookie(enrollmentDeltaCookie);
        }
    }

    interface IRetrievalIterator extends Iterator<JsonObject> {
        String getDeltaLink();

        AzureADElementType getRetrievalType();
    }

    abstract class BaseRetrievalIterator implements IRetrievalIterator {
        private static final String RESPONSE_NEXTLINK_KEY = "@odata.nextLink";
        private static final String RESPONSE_DELTALINK_KEY = "@odata.deltaLink";
        private static final String RESPONSE_ARRAY_KEY = "value";

        protected String nextLink = null;
        protected String deltaLink = null;
        protected AzureADElementType retrievalType;

        public BaseRetrievalIterator(AzureADElementType retrievalType) {
            this.retrievalType = retrievalType;
        }

        public AzureADElementType getRetrievalType() {
            return retrievalType;
        }

        public String getDeltaLink() {
            return deltaLink;
        }
        
        abstract public JsonObject next();

        abstract public void addElement(JsonElement element);
        
        /**
         * Parse the Json response, setting nextLink and deltaLink, and adding the
         * entries to 'elements'
         */
        protected void parse(String response) {
            LOG.debug("Processing JSON response:\n" + response);
            
            JsonObject jsonResponse = parser.parse(response).getAsJsonObject();

            nextLink = null;
            JsonElement nextLinkElement = jsonResponse.get(RESPONSE_NEXTLINK_KEY);

            if (nextLinkElement != null) {
                nextLink = nextLinkElement.getAsString();
            }

            JsonElement deltaLinkElement = jsonResponse.get(RESPONSE_DELTALINK_KEY);

            if (deltaLinkElement != null) {
                deltaLink = deltaLinkElement.getAsString();
            }

            for (JsonElement element : jsonResponse.get(RESPONSE_ARRAY_KEY).getAsJsonArray()) {
                addElement(element);
            }
        }

        protected String getElementId(JsonElement element) {
            if (element == null) {
                return null;
            }

            JsonElement id = element.getAsJsonObject().get(ENTITY_ID);

            if (id != null && id.isJsonPrimitive()) {
                return id.getAsString();
            }

            return null;
        }
        
        protected boolean hasSameId(JsonElement first, JsonElement second) {
            String firstId = getElementId(first);
            String secondId = getElementId(second);

            return firstId != null && firstId.equals(secondId);
        }
        
        /**
         * Combine the information for two JsonElements. The result is
         * put into mergeToElem
         *
         * @param mergeToElem an element. Will contain the result of
         * the merge
         * @param mergeFromElem the other element
         */
        protected JsonElement merge(JsonElement mergeToElem, JsonElement mergeFromElem) {
            JsonObject mergeTo = mergeToElem.getAsJsonObject();
            JsonObject mergeFrom = mergeFromElem.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : mergeFrom.entrySet()) {
                if (!mergeTo.has(entry.getKey())) {
                    // Move anything new across
                    mergeTo.add(entry.getKey(), entry.getValue());
                } else if (entry.getValue().isJsonArray()) {
                    // If the value is an array, add its elements to
                    // the corresponding array in mergeTo
                    JsonElement toElement = mergeTo.get(entry.getKey());
                    if (toElement.isJsonArray()) {
                        toElement.getAsJsonArray().addAll(entry.getValue().getAsJsonArray());
                    } else {
                        LOG.warn("Can't merge key " + entry.getKey() + " from array to non-array:\n" +
                                 mergeFrom.toString() +
                                 "\n===\n" +
                                 mergeTo.toString());
                        continue;
                    }
                }
                // If the member is a primitive then don't do
                // anything. The one in the "to" object will work just
                // fine
            }

            return mergeToElem;
        }
        
        /**
         * Queries for a chunk of data
         *
         * If this is the first call then the query will either be the
         * delta link from a previous enrollment sync (if it exists)
         * or the basic user/group query.
         *
         * If this is not the first call then the query will be the
         * nextlink from the previous call
         *
         * If this is the last chunk then deltaLink will be set and
         * nextLink will not
         *
         * @return if there are more chunks to come
         */
        protected boolean getChunk() {
            String query;

            try {
                if (nextLink == null) {
                    query = getPreviousDeltaLink(retrievalType);
                    
                    if (query == null) {
                        if (retrievalType == AzureADElementType.USER_TYPE) {
                            query = buildUsersURLSpec();
                        } else {
                            query = buildGroupsURLSpec();
                        }
                    }
                } else {
                    query = nextLink;
                }
                
                if (query != null) {
                    HttpURLConnection conn = buildConnectionRequest(query);
                    String response = getResponse(conn);
                    parse(response);
                }
                
                return nextLink != null;
            } catch (DictionaryException | AzureADConnectionException | IOException ex) {
                setRetrievalException(ex);
            }

            return false;
        }
    }

    /**
     * The BatchRetrievalIterator reads all of the entities from AAD
     * before returning any. Any JsonElements that refer to the same
     * entity (user or group) will be merged into one.
     *
     * This iterator should be used if you know that information about
     * a single entity can be split up among multiple *non-adjacent*
     * JsonElements. This is, sadly, the case for groups (but does not
     * appear to be the case for users).
     */
    class BatchRetrievalIterator extends BaseRetrievalIterator {
        private boolean initialized = false;
        HashMap<String, Integer> idIndexMap = new HashMap<>();
        ArrayList<JsonElement> elements = new ArrayList<>();
        LinkedList<JsonElement> queue = new LinkedList<>();
        
        public BatchRetrievalIterator(AzureADElementType retrievalType) {
            super(retrievalType);
        }

        public boolean hasNext() {
            if (!initialized) {
                // Get everything and move it to queue
                while (getChunk()) {
                    LOG.debug("Reading...");
                }
                    
                
                idIndexMap = null;

                queue.addAll(elements);

                elements = null;
                
                initialized = true;
            }

            return !queue.isEmpty();
        }
        
        public JsonObject next() {
            return queue.poll().getAsJsonObject();
        }

        public void addElement(JsonElement element) {
            String id = getElementId(element);

            if (idIndexMap.containsKey(id)) {
                int index = idIndexMap.get(id);

                LOG.debug("Merging " + id + " with existing element");
                merge(elements.get(index), element);
            } else {
                LOG.debug("Adding " + id + " as new element");
                idIndexMap.put(id, elements.size());
                elements.add(element);
            }
        }
        
    }

    /**
     * The IncrementalRetrieverIterator queries the AAD service as
     * needed for data. If two adjacent JsonElements reference the
     * same entity (user or group, determined by id) then they will be
     * merged into one.
     *
     * This iterator can be used if you expect a large number of
     * entities and you know that an entity's information will never
     * be split up among multiple non-adjacent JsonElements. This is
     * probably the case for users, but is definitely not the case, at
     * the time of writing, for groups.
     */
    class IncrementalRetrievalIterator extends BaseRetrievalIterator {
        private boolean moreToRead = true;
        private Queue<JsonElement> elements = new LinkedList<>();
        
        
        public IncrementalRetrievalIterator(AzureADElementType retrievalType) {
            super(retrievalType);
        }

        /**
         * Determines if there are more elements to be read
         *
         * @return true if there are more elements to be read
         */
        public boolean hasNext() {
            // If we have elements in the queue then we are fine
            if (!elements.isEmpty()) {
                return true;
            }

            // If we don't, but the last call said that there was more data
            // then we should try to refill the queue
            if (moreToRead) {
                moreToRead = getChunk();
                return !elements.isEmpty();
            }

            // Done
            return false;
        }

        /**
         * Returns the next element. It is an error to call this if hasNext() returned false
         * 
         * @return the next JsonObject
         */
        public JsonObject next() {
            JsonElement elem = elements.poll();

            // The data for an element can be split across two
            // requests. Look for that and combine the entries
            JsonElement nextElem = elements.peek();

            // The next element might be (probably *is*) in the next
            // chunk of data. If that exists
            if (nextElem == null && moreToRead) {
                moreToRead = getChunk();
                nextElem = elements.peek();
            }

            // It's unlikely, but possible, that an element could be
            // split into more than two pieces.  The API, however,
            // guarantees (?) that they are all adjacent
            //
            // If we lose that guarantee then the only solution is to
            // read everything up-front into memory and then process
            // the whole block of data. Not unworkable, really.
            while(hasSameId(elem, nextElem)) {
                elements.poll();
                elem = merge(elem, nextElem);
                nextElem = elements.peek();

                if (nextElem == null && moreToRead) {
                    moreToRead = getChunk();
                    nextElem = elements.peek();
                }
            }
            
            return elem.getAsJsonObject();
        }

        public void addElement(JsonElement element) {
            elements.add(element);
        }
    };

    /**
     * Get the "delta link" recorded from the last sync. If this is the first sync of a
     * new enrollment the link will be null
     */
    private String getPreviousDeltaLink(AzureADElementType type) throws DictionaryException {
        if (type == AzureADElementType.USER_TYPE) {
            enrollmentDeltaCookie = enrollmentWrapper.getEnrollment().getDeltaCookieByType(IEnrollmentDeltaCookie.USERS);
        } else {
            enrollmentDeltaCookie = enrollmentWrapper.getEnrollment().getDeltaCookieByType(IEnrollmentDeltaCookie.GROUPS);
        }

        return enrollmentDeltaCookie != null ? enrollmentDeltaCookie.getCookieAsString() : null;
    }
}
