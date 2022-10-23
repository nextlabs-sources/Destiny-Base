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

import com.bluejungle.destiny.server.shared.configuration.impl.DaysOfWeekDO;
import com.bluejungle.destiny.server.shared.configuration.type.DayOfWeek;
import com.bluejungle.destiny.server.shared.configuration.type.DaysOfWeek;

public class DaysOfWeekConverter implements Converter{
    
    @Override
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            String[] days = ((String)value).split(",");

            DayOfWeekConverter dowConverter = new DayOfWeekConverter();
            
            DaysOfWeek daysOfWeek = new DaysOfWeek();

            for (String day : days) {
                DayOfWeek dow = (DayOfWeek)dowConverter.convert(DayOfWeek.class, day);

                if (dow != null) {
                    daysOfWeek.addDayOfWeek(dow);
                }
            }

            DaysOfWeekDO daysOfWeekDO = new DaysOfWeekDO();
            daysOfWeekDO.setDaysOfWeek(daysOfWeek);
            
            return daysOfWeekDO;
        }
        return null;
    }
}
