/*
 * Created on May 09, 2016
 *
 * All sources, binaries and HTML pages (C) copyright 2016 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/client/openaz/src/java/main/com/nextlabs/openaz/pepapi/Recipient.java#1 $:
 */

package com.nextlabs.openaz.pepapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;

import com.nextlabs.openaz.utils.Constants;

/**
 * Container class defining a Recipient (typically an email recipient) and associated
 * attributes.
 *
 * The associated mapper is {@link com.nextlabs.openaz.pepapi.RecipientMapper}
 */
public final class Recipient extends CategoryContainer {
    public static final Identifier CATEGORY_ID = XACML3.ID_SUBJECT_CATEGORY_RECIPIENT_SUBJECT;
        
    private String recipientId;
    private String[] recipientEmails;

    private Recipient() {
        super(CATEGORY_ID);
    }

    /**
     * Get the id of this recipient
     *
     * @return
     */
    public String getRecipientId() {
        return recipientId;
    }
    
    /**
     * Get recipient emails
     * @return
     */
    public String[] getRecipientEmails() {
    	return recipientEmails;
    }

    /**
     * Creates a new Recipient instance with the given id 
     *
     * @param recipientId the identifier of the recipient
     * @return
     */
    public static Recipient newInstance(String recipientId) {
        Recipient recipient = new Recipient();
        recipient.recipientId = recipientId;
        return recipient;
    }
    
    /**
     * Create a new Recipient instance with emails
     * 
     * @param emails
     * @return
     */
    public static Recipient newInstance(String... emails) {
    	Recipient recipient = new Recipient();
    	recipient.recipientEmails = emails;
    	return recipient;
    }
}
