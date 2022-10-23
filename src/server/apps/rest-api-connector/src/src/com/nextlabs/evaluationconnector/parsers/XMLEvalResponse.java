/*
 * Created on Jan 20, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.parsers;

import static com.nextlabs.evaluationconnector.utils.Constants.CONTENT_TYPE_XML;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.beans.PermissionsResponsesType;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.evaluationconnector.utils.Constants;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * <p>
 * XMLEvalResponse
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class XMLEvalResponse implements EvalResponse {

	private static final Log log = LogFactory.getLog(XMLEvalResponse.class);

    private JAXBContext jc;
	private ObjectFactory objectFactory;

	public void init() throws EvaluationConnectorException {
		try {
			synchronized (this) {
                jc = JAXBContext.newInstance(ResponseType.class.getPackage().getName() + ":" + PermissionsResponsesType.class.getPackage().getName());
				objectFactory = new ObjectFactory();
			}

			if (log.isInfoEnabled())
				log.info("XMLEvalResponse initialized successfully");
		} catch (Exception e) {
			throw new EvaluationConnectorException(
					"Error in XMLEvalResponse.init()", e);
		}
	}

	@Override
	public void handleResponse(HttpServletResponse httpResponse, XACMLResponse response)
			throws EvaluationConnectorException {

		final StringWriter writer = new StringWriter();

		try {
                
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			long startCounter = System.nanoTime();
            JAXBElement<ResponseType> responseTypeElement = objectFactory.createResponse(response.getResponseType());
			marshaller.marshal(responseTypeElement, writer);
			String responseString = writer.toString();

			long policyEvalTime = response.getPdpEvalTime();
			long restApiResponseTime = (System.nanoTime() - response.getRequestStartTime());

			httpResponse.setContentType(CONTENT_TYPE_XML);
			httpResponse.setContentLength(responseString.length());
			httpResponse.setHeader(Constants.POLICY_EVAL_TIME, String.valueOf(policyEvalTime));
			httpResponse.setHeader(Constants.REST_API_RESPONSE_TIME, String.valueOf(restApiResponseTime));

			PrintWriter pWriter = httpResponse.getWriter();
			pWriter.write(responseString);
			pWriter.flush();

			if (log.isDebugEnabled())
				log.debug(
						String.format("%s, Thread Id:[%s],  XMLEvalResponse -> Total Response creation Time : %d nano",
								Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
								(System.nanoTime() - startCounter)));

			if (log.isDebugEnabled())
				log.debug("XMLEvalResponse is written to response successfully, [Response : " + responseString + "]");

		} catch (Exception e) {
			throw new EvaluationConnectorException("Error occurred while handleResponse in XML Eval Response, ", e);
		}
	}
    
	@Override
	public void handleResponse(HttpServletResponse httpResponse, IWrappedXACMLResponse enforcement)
			throws EvaluationConnectorException {
		try {
            String responseString = enforcement.encode(CONTENT_TYPE_XML);
            
			httpResponse.setContentType(CONTENT_TYPE_XML);
			httpResponse.setContentLength(responseString.length());

			PrintWriter pWriter = httpResponse.getWriter();
			pWriter.write(responseString);
			pWriter.flush();

			if (log.isDebugEnabled())
				log.debug("XMLEvalResponse is written to response successfully, [Response : " + responseString + "]");

		} catch (Exception e) {
			throw new EvaluationConnectorException("Error occurred while handleResponse in XML Eval Response, ", e);
		}
	}

    @Override
    public void handleResponse(HttpServletResponse httpResponse, PermissionsResponse response) throws EvaluationConnectorException {
        final StringWriter writer = new StringWriter();

        try {
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            long startCounter = System.nanoTime();
            JAXBElement<PermissionsResponsesType> responseTypeElement =
                new JAXBElement<>(PermissionsResponsesType.QNAME,
                                  PermissionsResponsesType.class,
                                  null,
                                  response.getResponsesType());
            marshaller.marshal(responseTypeElement, writer);
            String responseString = writer.toString();
            long permissionsEvalTime = response.getPermissionsEvalTime();
            long restApiResponseTime = (System.nanoTime() - response.getRequestStartTime());

            httpResponse.setContentType(CONTENT_TYPE_XML);
            httpResponse.setContentLength(responseString.length());
            httpResponse.setHeader(Constants.PERMISSIONS_EVAL_TIME, String.valueOf(permissionsEvalTime));
            httpResponse.setHeader(Constants.REST_API_RESPONSE_TIME, String.valueOf(restApiResponseTime));

            PrintWriter pWriter = httpResponse.getWriter();
            pWriter.write(responseString);
            pWriter.flush();
            
			if (log.isDebugEnabled()) {
				log.debug(
						String.format("%s, Thread Id:[%s],  XMLEvalResponse -> Total permissions response creation Time : %d nano",
								Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
								(System.nanoTime() - startCounter)));

				log.debug("XMLEvalResponse is written successfully, [Response : " + responseString + "]");
            }
        } catch (Exception e) {
            throw new EvaluationConnectorException("Error occurred while handleResponse in handleResponse for Permissions");
        }
    }
}
