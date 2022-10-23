/*
 * Created on Jun 10, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.server.shared.configuration.impl;

import java.util.BitSet;

import com.bluejungle.destiny.server.shared.configuration.type.DayOfWeek;
import com.bluejungle.destiny.server.shared.configuration.type.DaysOfWeek;

/**
 * @author hchan
 */


public class DaysOfWeekDO {
	private BitSet bits;
	
	public DaysOfWeekDO() {
		bits = new BitSet(7);
	}
	public void setDaysOfWeek(DaysOfWeek daysOfWeek){
        for (DayOfWeek dayOfWeek : daysOfWeek.getDaysOfWeek()) {
            bits.set(dayOfWeek.getDayOfWeek() - 1);
        }
	}
	public BitSet getDaysOfWeek(){
		return bits;
	}
	public void setDaysOfWeek(BitSet bits) {
		this.bits = bits;
	}
	@Override
	public String toString() {
		return bits.toString();
	}
	
}
