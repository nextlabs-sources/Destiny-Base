/*
 * Created on Mar 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.securesession.service.axis;


import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.impl.soap.SOAPException;

import com.bluejungle.destiny.container.shared.securesession.ISecureSession;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/securesession/service/axis/AuthenticationResponseFlowHandler.java#1 $
 */

public class AuthenticationResponseFlowHandler extends AbstractHandler {

    private static final Log LOG = LogFactory.getLog(AuthenticationResponseFlowHandler.class.getName());
    
    private static final String SECURE_SESSION_TYPES_PREFIX = "ss";
    private List<String> applicableServices = new ArrayList<>();

    public void init(HandlerDescription handlerdesc) {
        super.init(handlerdesc);
        handlerdesc.getParent().getParameters().forEach(parameter -> {
            if ("AuthenticationResponseFlowHandler-Services".equals(parameter.getName())) {
                applicableServices = Arrays.asList(parameter.getValue().toString().split(","));
            }
        });
    }
        
    /**
     * @return 
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     */
    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        if (!applicableServices.contains(msgContext.getAxisService().getName())) {
            return InvocationResponse.CONTINUE;
        }
        ISecureSession currentSecureSession = (ISecureSession)msgContext.getProperty(AuthenticationHandlerConstants.SECURE_SESSION_PROPERTY_NAME);
        if (currentSecureSession != null) {
            try {
                addSecureSessionHeader(msgContext, currentSecureSession);
            } catch (AxisFault axisFault) {
                LOG.error("Failed to add secure session key.  Client session may time out.", axisFault);
            } catch (SOAPException exception) {
                LOG.error("Failed to add secure session key.  Client session may time out.", exception);
            }
        }
        
        return InvocationResponse.CONTINUE;
    }

    /**
     * Add the secure session header to the response messages
     * 
     * @param msgContext
     * @param currentSecureSession
     * @throws SOAPException
     * @throws AxisFault
     */
    private void addSecureSessionHeader(MessageContext msgContext, ISecureSession currentSecureSession) throws AxisFault, SOAPException {
        SOAPEnvelope soapEnvelope = msgContext.getEnvelope();       
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        
        SOAPFactory factory = (SOAPFactory) soapEnvelope.getOMFactory();            
        OMElement secureSessionElement = factory.createOMElement(new QName( AuthenticationHandlerConstants.SECURE_SESSION_TYPE_NAMESPACE, AuthenticationHandlerConstants.SECURE_SESSION_HEADER_ELEMENT_NAME, SECURE_SESSION_TYPES_PREFIX), null);
        OMElement secureSessionKeyElement = factory.createOMElement(new QName("", AuthenticationHandlerConstants.SECURE_SESSION_KEY_ELEMENT_NAME, ""), null);
        secureSessionKeyElement.setText(currentSecureSession.generateKey());
        secureSessionElement.addChild(secureSessionKeyElement);
         
        soapHeader.addChild(secureSessionElement);
         
    }
}
