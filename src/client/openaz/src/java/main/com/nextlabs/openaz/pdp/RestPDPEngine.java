/*
 * Created on Aug 11, 2016
 *
 * All sources, binaries and HTML pages (C) copyright 2016 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author sduan
 */
package com.nextlabs.openaz.pdp;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.PermissionsResponse;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPException;
import org.apache.openaz.xacml.std.jaxp.JaxpResponse;
import org.apache.openaz.xacml.std.json.JSONRequest;
import org.apache.openaz.xacml.std.json.JSONPermissionsResponse;
import org.apache.openaz.xacml.std.json.JSONResponse;
import org.apache.openaz.xacml.std.json.JSONStructureException;

import com.nextlabs.openaz.pdp.utils.RequestTypeMarshaller;
import com.nextlabs.openaz.pdp.utils.RequestTypeUtil;
import com.nextlabs.openaz.pdp.utils.ResponseTypeParser;
import com.nextlabs.openaz.utils.Constants;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;

public class RestPDPEngine implements PDPEngine {

	private static final Log logger = LogFactory.getLog(RestPDPEngine.class);

	private static final List<URI> EMPTY_PROFILES = new ArrayList<URI>();
	private volatile static RestPDPEngine engine = null;

	private Properties properties;
	private RestExecutor restExecutor;
	private static final String DEFAULT_PDP_REST_PAYLOAD_CONTENT_TYPE = ContentType.APPLICATION_JSON.getMimeType();

	private RestPDPEngine(Properties properties) throws PDPException {
		if (properties == null) {
			throw new PDPException("properties is null");
		}
		restExecutor = new NextLabsRestExecutor(properties);
		this.properties = properties;
	}

	public static RestPDPEngine getInstance(Properties properties) throws PDPException {
		if (engine == null) {
			synchronized (RestPDPEngine.class) {
				if (engine == null) {
					engine = new RestPDPEngine(properties);
				}
			}
		}

		return engine;
	}

	@Override
	public Response decide(Request request) throws PDPException {
		ContentType contentType = ContentType.parse(properties.getProperty(Constants.PDP_REST_PAYLOAD_CONTENT_TYPE,
				DEFAULT_PDP_REST_PAYLOAD_CONTENT_TYPE));

		if(contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())) {
			RequestType requestType = RequestTypeUtil.convert(request);

			String xmlRequestString = null;
			try {
				xmlRequestString = RequestTypeMarshaller.marshal(requestType);
			} catch (JAXBException e) {
				throw new PDPException("Error marshal request to XML payload: ", e);
			}

			if(logger.isDebugEnabled()) {
				logger.debug("Created xacml request xml string:\n" + xmlRequestString);
			}

			String xmlResponseString = null;
			try {
				xmlResponseString = this.restExecutor.call(xmlRequestString, contentType);
			} catch (RestExecutorException e) {
				throw new PDPException("Error making REST call to PDP: ", e);
			}

			if(logger.isDebugEnabled()) {
				logger.debug("Received xacml response xml string:\n" + xmlResponseString);
			}

			ResponseType responseType = null;
			try {
				responseType = ResponseTypeParser.parse(xmlResponseString);
			} catch (JAXBException e) {
				throw new PDPException("Error parse response body: ", e);
			}

			return JaxpResponse.newInstance(responseType);
		} else if(contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())) {
			String jsonRequestString = null;
			try {
				jsonRequestString = JSONRequest.toString(request, false);
			} catch (Exception e) {
				throw new PDPException("Error marshalling request to JSON paylaod: ", e);
			}

			if(logger.isDebugEnabled()) {
				logger.debug("Created xacml request json string:\n" + jsonRequestString);
			}

			String jsonResponseString = null;
			try {
				jsonResponseString = this.restExecutor.call(jsonRequestString, contentType);
			} catch (RestExecutorException e) {
				throw new PDPException("Error making REST call to PDP: ", e);
			}

			try {
				Response response = JSONResponse.load(jsonResponseString);
				return response;
			} catch (JSONStructureException e) {
				throw new PDPException("Error json parse response body: ", e);
			}

		} else {
			throw new PDPException(String.format("Invalid value of PDP_REST_PAYLOAD_CONTENT_TYPE: %s",
					contentType));
		}

	}

    @Override
    public PermissionsResponse decidePermissions(Request permissionsRequest) throws PDPException {
        ContentType contentType = ContentType.parse(properties.getProperty(Constants.PDP_REST_PAYLOAD_CONTENT_TYPE,
                                                                           DEFAULT_PDP_REST_PAYLOAD_CONTENT_TYPE));

        if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())) {
            throw new PDPException("XML format not supported for permissions request");
        } else if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())) {
            String jsonPermissionsRequestString = null;
            try {
                // Permissions request looks just like a regular request (except it's missing the action), so we can
                // use the same formatter
                jsonPermissionsRequestString = JSONRequest.toString(permissionsRequest);
            } catch (Exception e) {
                throw new PDPException("Error marshalling permissions request to JSON: ", e); 
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Created xacml request json string:\n" + jsonPermissionsRequestString);
            }

            String jsonPermissionsResponseString = null;
            try {
                jsonPermissionsResponseString = this.restExecutor.callPermissions(jsonPermissionsRequestString, contentType);
            } catch (RestExecutorException e) {
                throw new PDPException("Error making REST call to PDP: ", e);
            }

            try {
                // The response looks very different, however
                PermissionsResponse permissionsResponse = JSONPermissionsResponse.load(jsonPermissionsResponseString);
                return permissionsResponse;
            } catch (JSONStructureException e) {
                throw new PDPException("Error in parsing json permissions response: ", e);
            }
        } else {
            throw new PDPException(String.format("Invalid value of PDP_REST_PAYLOAD_CONTENT_TYPE: %s", contentType));
        }
    }
    
	@Override
	public Collection<URI> getProfiles() {
		return EMPTY_PROFILES;
	}

	@Override
	public boolean hasProfile(URI uriProfile) {
		return false;
	}

	public RestExecutor getRestExecutor() {
		return restExecutor;
	}

	public void setRestExecutor(RestExecutor restExecutor) {
		this.restExecutor = restExecutor;
	}

}
