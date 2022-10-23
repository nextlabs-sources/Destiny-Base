/*
 * Created on Jun 25, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.componentsconfigmgr.filebaseimpl;

import org.apache.commons.beanutils.Converter;

import com.bluejungle.destiny.server.shared.configuration.impl.DaysOfMonthDO;

public class DaysOfMonthConverter implements Converter{
    @Override
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            String[] daysOfMonth = ((String)value).split(",");

            DaysOfMonthDO daysOfMonthDO = new DaysOfMonthDO();
            
            for (String dayOfMonth : daysOfMonth) {
                try {
                    daysOfMonthDO.addDayOfMonth(Byte.parseByte(dayOfMonth));
                } catch (NumberFormatException nfe) {
                    // Continue
                }
            }

            return daysOfMonthDO;
        }
        return null;
    }
}
