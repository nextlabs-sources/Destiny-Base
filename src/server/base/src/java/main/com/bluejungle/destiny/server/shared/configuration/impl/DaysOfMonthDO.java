/*
 * Created on Jun 10, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.server.shared.configuration.impl;

import java.util.BitSet;

/**
 * @author hchan
 */

public class DaysOfMonthDO {
	
	private BitSet bits;
	
	public DaysOfMonthDO(){
		bits = new BitSet(31);
	}

	public void addDayOfMonth(Byte dayOfMonth){
        if (dayOfMonth >= 1 && dayOfMonth <= 31) {
            bits.set((byte)(dayOfMonth-1));
        }
	}	
	public BitSet getDaysOfMonth(){
		return bits;
	}
	public void setDaysOfMonth(BitSet bits){
		this.bits = bits;
	}

	@Override
	public String toString() {
		return bits.toString();
	}
	
	
}
