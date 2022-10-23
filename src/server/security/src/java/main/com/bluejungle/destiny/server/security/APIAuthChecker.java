/*
 * Created on Jan 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.server.security;

import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.security.IKeyManager;

/**
 * This handler is a generic handler verifying that a caller with a given
 * certificate can access a given API on a web service. Typically, callers can
 * access all APIs of a web service as long as their certificate is valid, but
 * in some cases, we want to make a distinction and give access only to some
 * APIs based on the caller. The handler uses configuration parameters to figure
 * out which certificate allows access to which API on the web service. By
 * default, if nothing is specified in the configuration, this handler denies
 * access to all callers regardless of their certificate. There are two ways to
 * specify the access list for a given certificate:
 * 
 * 1) Alias=API1 API2 API3 gives access only to API1 API2 and API3
 * 
 * 2) Alias= *- API1 gives access to all APIs but API1
 * 
 * The handler configuration specifies what API are allowed/denied for a given
 * certificate.
 * 
 * @author ihanen
 */

public class APIAuthChecker extends BaseHandlerImpl {
    private static final String ALL_METHODS = "*";
    private static final String MINUS = "-";
    protected Map<String, Map<PublicKey, Set<String>>> serviceToIncludedMethods = new HashMap<>();
    protected Map<String, Map<PublicKey, Set<String>>> serviceToExcludedMethods = new HashMap<>();
    private List<String> applicableServices = new ArrayList<>();
    
    /**
     * Cleans up the handler before its destruction.
     * 
     * @see org.apache.axis.Handler#cleanup()
     */
    public void cleanup() {
        super.cleanup();
        serviceToIncludedMethods.clear();
        serviceToExcludedMethods.clear();
    }

    /**
     * Initialization method. This method walks through the option map and
     * populates the list of allowed APIs for different certificate aliases.
     * 
     * @see org.apache.axis.Handler#init()
     */
    public void init(HandlerDescription handlerdesc) {
        super.init(handlerdesc);
        handlerdesc.getParent().getParameters().forEach(parameter -> {
            if ("APIAuthChecker-Services".equals(parameter.getName())) {
                applicableServices = Arrays.asList(parameter.getValue().toString().split(","));
            }
        });
        if (handlerdesc.getParent().getParameters().stream().noneMatch(parameter -> parameter.getName().equals("APIAuthChecker-"))) {
            // Everything will be denied!
            return;
        }

        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        IKeyManager keyManager = (IKeyManager) componentManager.getComponent(IKeyManager.COMPONENT_NAME);

        handlerdesc.getParent().getParameters().forEach(parameter -> {
            String parameterName = parameter.getName();
            if (parameterName.startsWith("APIAuthChecker-")) {
                String[] identifiers = parameterName.split("-");
                String aliasName = identifiers[1];
                String serviceName = identifiers[2];
                PublicKey pubKey = keyManager.getPublicKey(aliasName);
                String accessList = parameter.getValue().toString();
                parseAccessList(serviceName, pubKey, accessList);
            }
        });
    }

    /**
     * This is the main handler invocation method.
     * 
     * @param context
     *            message context
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     */
    public InvocationResponse invoke(MessageContext context) {
        String serviceName = context.getAxisService().getName();
        if (!applicableServices.contains(serviceName)) {
            return InvocationResponse.CONTINUE;
        }
        HttpServletRequest request = (HttpServletRequest) context.getProperty(HTTP_REQUEST_ATTR);

        if (request == null) {
            // Illegal message - no HTTP request provided
            return InvocationResponse.ABORT;
        }

        X509Certificate[] certs = (X509Certificate[]) request.getAttribute(HTTP_REQUEST_CERT_ATTR);
        // If no certificate is given, security is not enabled and, the handler
        // should be simply bypassed.
        if (certs != null) {
            // Search the operation that is targeted
            String opName = context.getOperationContext().getServiceName();

            // See if this operation is mapped to an alias that we know of
            int count = certs.length;
            for (int i = 0; i < count; i++) {
                X509Certificate cert = certs[i];
                PublicKey pubKey = cert.getPublicKey();
                if (isAccessAllowed(serviceName, pubKey, opName)) {
                    return InvocationResponse.CONTINUE;
                }
            }
            // No certificate in the chain can be used to access this method
            return InvocationResponse.ABORT;
        }

        return InvocationResponse.CONTINUE;
    }

    /**
     * This method returns whether a certificate with a given public key can
     * access a given API or not. The method inspects the list of included or
     * excluded methods to figure out what answer to give.
     * 
     * @param key
     *            public key of the certificate
     * @param methodName
     *            API to be accessed
     * @return true if the certificate can access this API, false otherwise.
     */
    private boolean isAccessAllowed(String serviceName, PublicKey key, String methodName) {
        Map<PublicKey, Set<String>> excludedMethods = serviceToExcludedMethods.computeIfAbsent(serviceName, service ->  new HashMap<>());
        Map<PublicKey, Set<String>> includedMethods = serviceToIncludedMethods.computeIfAbsent(serviceName, service ->  new HashMap<>());
        if (includedMethods.containsKey(key)) {
            // to allow access, the method has to be present in the set
            return (includedMethods.get(key).contains(methodName));
        } else if (excludedMethods.containsKey(key)) {
            // to allow access, the method should not be present in the set
            return (!excludedMethods.get(key).contains(methodName));
        } else {
            // the certificate is not found anywhere, by default, deny access
            return (false);
        }
    }

    /**
     * This method parses the access list given for a particular alias. Based on
     * the access list specification, it places the key in the relevant groups
     * (included or excluded)
     * 
     * @param key
     *            public key for the certificate alias
     * @param accessList
     *            String to parse, containing the access list specification
     */
    private void parseAccessList(String serviceName, PublicKey key, String accessList) {

        Map<PublicKey, Set<String>> excludedMethods = serviceToExcludedMethods.computeIfAbsent(serviceName, service ->  new HashMap<>());
        Map<PublicKey, Set<String>> includedMethods = serviceToIncludedMethods.computeIfAbsent(serviceName, service ->  new HashMap<>());
        if (key == null || accessList == null) {
            return;
        }

        // Check whether the access list is based on inclusion or exclusion
        String exclusionPattern = ALL_METHODS + MINUS;
        int index = accessList.indexOf(exclusionPattern);
        if (index >= 0) {
            // This is an exclusion. Process excluded methods
            String excludedMethodsList = accessList.substring(index + exclusionPattern.length());
            StringTokenizer parser = new StringTokenizer(excludedMethodsList, SPACE);
            Set<String> exclusions = new HashSet<String>();
            int methodCount = parser.countTokens();
            for (int i = 0; i < methodCount; i++) {
                exclusions.add(parser.nextToken());
            }
            excludedMethods.put(key, exclusions);
        } else {
            // This is an inclusion. Process included methods
            StringTokenizer parser = new StringTokenizer(accessList, SPACE);
            Set<String> inclusions = new HashSet<String>();
            int methodCount = parser.countTokens();
            for (int i = 0; i < methodCount; i++) {
                inclusions.add(parser.nextToken());
            }
            includedMethods.put(key, inclusions);
        }
    }
}
