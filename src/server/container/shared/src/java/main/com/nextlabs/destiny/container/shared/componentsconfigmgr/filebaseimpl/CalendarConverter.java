/*
 * Created on Jun 8, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.container.shared.componentsconfigmgr.filebaseimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/nextlabs/destiny/container/shared/componentsconfigmgr/filebaseimpl/CalendarConverter.java#1 $
 */

public class CalendarConverter implements Converter {
    
	private static final Map<String, SimpleDateFormat> POSSIBLE_FORMATS;
	static{
		POSSIBLE_FORMATS = new HashMap<String, SimpleDateFormat>();
		POSSIBLE_FORMATS.put("(([0-1][0-9])|(2[0-3])):[0-5][0-9]", new SimpleDateFormat("HH:mm"));
		POSSIBLE_FORMATS.put("(([0-1][0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9]", new SimpleDateFormat("HH:mm:ss"));
	}
	
	private final Map<String, SimpleDateFormat> formats;
	
	public CalendarConverter() {
		this(POSSIBLE_FORMATS);
    }
	
	public CalendarConverter(Map<String, SimpleDateFormat> formats) {
        this.formats = formats;
    }

	public Object convert(Class type, Object value) {
		if (value == null) {
            return null;
		}

		if (value instanceof Calendar) {
			return value;
		} else if (value instanceof Date) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) value);
			return c;
		} else {
			Date d = null;
			for(Map.Entry<String, SimpleDateFormat> format : formats.entrySet()){
			    if (Pattern.matches(format.getKey(), value.toString())) {
    				try {
    					d = format.getValue().parse(value.toString());
    					break;
    				} catch (ParseException e) {
    					//ignore
    				}
			    }
			}
			
			if (d == null) {
                return null;
			}
			
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		}
	}

}
