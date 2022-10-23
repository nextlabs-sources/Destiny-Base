/*
 * Created on Oct 8, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.shared.tools.display;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/common/framework/src/java/main/com/nextlabs/shared/tools/display/IDisplayable.java#1 $
 */

public interface IDisplayable {
	//return what will print out
	String getOutput();
	
	//the fix length of the output
	int getLength();
	
	boolean isUpdateable();
}
