/*
 * Created on Aug 11, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */


package com.nextlabs.evaluationconnector.beans;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    public ObjectFactory() {
    }

    public PermissionsResponsesType createPermissionsResponsesType() {
        return new PermissionsResponsesType();
    }

    @XmlElementDecl(namespace = "urn:nextlabs:names:permsvc:1.0:response", name = "PermissionsResponsesType")
    public JAXBElement<PermissionsResponsesType> createPermissionsResponsesType(PermissionsResponsesType value) {
        return new JAXBElement<PermissionsResponsesType>(PermissionsResponsesType.QNAME, PermissionsResponsesType.class, null, value);
    }
    
    public PermissionsResponseType createPermissionsResponseType() {
        return new PermissionsResponseType();
    }

    @XmlElementDecl(namespace = "urn:nextlabs:names:permsvc:1.0:response", name = "PermissionsResponseType")
    public JAXBElement<PermissionsResponseType> createPermissionsResponseType(PermissionsResponseType value) {
        return new JAXBElement<PermissionsResponseType>(PermissionsResponseType.QNAME, PermissionsResponseType.class, null, value);
    }
    
    public PermissionsActionObligations createPermissionsActionObligations() {
        return new PermissionsActionObligations();
    }
    
    @XmlElementDecl(namespace = "urn:nextlabs:names:permsvc:1.0:response", name = "PermissionsActionObligations")
    public JAXBElement<PermissionsActionObligations> createPermissionsActionObligations(PermissionsActionObligations value) {
        return new JAXBElement<PermissionsActionObligations>(PermissionsActionObligations.QNAME, PermissionsActionObligations.class, null, value);
    }

    public PermissionsActionObligationsList createPermissionsActionObligationsList() {
        return new PermissionsActionObligationsList();
    }

    @XmlElementDecl(namespace = "urn:nextlabs:names:permsvc:1.0:response", name = "PermissionsActionObligationsList")
    public JAXBElement<PermissionsActionObligationsList> createPermissionsActionObligationsList(PermissionsActionObligationsList value) {
        return new JAXBElement<PermissionsActionObligationsList>(PermissionsActionObligationsList.QNAME, PermissionsActionObligationsList.class, null, value);
    }
}
