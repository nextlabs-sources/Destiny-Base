/*
 * Created on Jul 20, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.framework.messaging.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.nextlabs.framework.messaging.MessagingException;
import com.nextlabs.framework.utils.ByteArrayDataSource;

/**
 * @author hchan
 */

public class MimeMessage extends BaseMessage{
    private final Multipart mp;
    
    public MimeMessage(String subject, Calendar time) {
        super(subject, time);
        mp = new MimeMultipart();
    }
    
    public MimeMessage(String subject) {
        this(subject, null);
    }
    
    public void addPlainText(String plainText) throws MessagingException{
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        try {
            mimeBodyPart.setText(plainText);
            mp.addBodyPart(mimeBodyPart);
        } catch (javax.mail.MessagingException e) {
            throw new MessagingException(MessagingException.Type.COMPOSE_MESSAGE, e);
        }
    }
    
    public void addHtml(String html) throws MessagingException {
        addBinaryData("text/html", html.getBytes());
    }
    
    //TODO test more on this
    public void addBinaryData(String type, byte[] binaryData) throws MessagingException{
        addDataSource(new ByteArrayDataSource(type, binaryData));
    }
    
    public void addFile(File file) throws MessagingException{
        MimeBodyPart bodyPart = addDataSource(new FileDataSource(file));
        try {
            bodyPart.setFileName(file.getName());
        } catch (javax.mail.MessagingException e) {
            try {
                mp.removeBodyPart(bodyPart);
            } catch (javax.mail.MessagingException e1) {
                // only if no such element exists
                // safe to ignore
            }
            throw new MessagingException(MessagingException.Type.COMPOSE_MESSAGE, e);
        }
    }
    
    protected MimeBodyPart addDataSource(DataSource dataSource) throws MessagingException{
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        try {
            mimeBodyPart.setDataHandler(new DataHandler(dataSource));
            mp.addBodyPart(mimeBodyPart);
        } catch (javax.mail.MessagingException e) {
            throw new MessagingException(MessagingException.Type.COMPOSE_MESSAGE, e);
        }
        return mimeBodyPart;
    }
    
    public String getMessageText() {
        throw new UnsupportedOperationException();
    }

    public Multipart getBody(){
        return mp;
    }
}
