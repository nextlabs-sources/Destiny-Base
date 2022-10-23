/*
 * Created on Nov 7, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.shared.tools;

import java.io.IOException;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/common/framework/src/java/test/com/nextlabs/shared/tools/TestMaskedConsolePrompt.java#1 $
 */

public class TestMaskedConsolePrompt {
	public static void main(String[] args) throws IOException {
		MaskedConsolePrompt mcp = new MaskedConsolePrompt("How are you?");
		char[] input = mcp.readConsoleSecure();
		System.out.println("input = " + input);
		//eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=76936
		//putting this console in infinite loop
	}
}
