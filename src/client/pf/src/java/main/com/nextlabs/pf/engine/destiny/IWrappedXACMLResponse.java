/*
 * Created on May 04, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.pf.engine.destiny;

public interface IWrappedXACMLResponse {
    String XML_DUMMY_RESPONSE = "<Response xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
                                "  <Result>\n" +
                                "    <Decision>Indeterminate</Decision>\n" +
                                "    <Status>\n" +
                                "      <StatusCode Value=\"urn:oasis:names:tc:xacml:1.0:status:ok\"/>\n" +
                                "    </Status>\n" +
                                "  </Result>\n" +
                                "</Response>";
    
    String JSON_DUMMY_RESPONSE = "{\n" +
                                 "  \"Response\" : {\n" +
                                 "    \"Decision\" : \"Permit\",\n" +
                                 "    \"Status\" : {\n" +
                                 "      \"StatusCode\" : {\n" +
                                 "        \"Value\" : \"urn:oasis:names:tc:xacml:1.0:status:ok\"\n" +
                                 "      }\n" +
                                 "    }\n" +
                                 "  }\n" +
                                 " }";
    
    IWrappedXACMLResponse DUMMY_WRAPPED_RESPONSE = new IWrappedXACMLResponse() {
        public String encode(String type) {
            if ("application/xml".equals(type)) {
                return XML_DUMMY_RESPONSE;
            } else {
                return JSON_DUMMY_RESPONSE;
            }
        }
    };
    
    String encode(String type);
}
