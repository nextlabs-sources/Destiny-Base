/*
 * Created on Oct 01, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.destiny.agent.profile;

import java.util.Base64;
import javax.activation.DataHandler;

import org.apache.axis2.databinding.utils.ConverterUtil;
import org.exolab.castor.mapping.GeneralizedFieldHandler;

import com.nextlabs.framework.utils.ByteArrayDataSource;

public class PasswordHashCastorHandler extends GeneralizedFieldHandler {
    @Override
    public Object convertUponGet(Object value){
        return ConverterUtil.getStringFromDatahandler((DataHandler)value);
    }

    @Override
    public Object convertUponSet(Object value) {
        if (value == null) {
            return null;
        }

        byte[] hashBytes = Base64.getDecoder().decode(((String)value).getBytes());
        
        ByteArrayDataSource ds = new ByteArrayDataSource("application/octet-stream", hashBytes);
        return new DataHandler(ds);
    }

    @Override
    public Class getFieldType() {
        return DataHandler.class;
    }
}

