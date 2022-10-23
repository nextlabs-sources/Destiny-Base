/*
 * Created on Jun 25, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.destiny.server.shared.configuration.type;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class DaysOfWeek {
    private final Set<DayOfWeek> daysOfWeek;

    public DaysOfWeek() {
        this.daysOfWeek = new HashSet<DayOfWeek>();
    }

    public void addDayOfWeek(DayOfWeek dow) {
        daysOfWeek.add(dow);
    }
    
    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    @Override
    public int hashCode() {
        final int prime = 11;
        int result = 1;
        for (DayOfWeek day : daysOfWeek) {
            result = prime * result + day.getDayOfWeek();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else {
            return ((DaysOfWeek)obj).daysOfWeek == daysOfWeek;
        }
    }
}
