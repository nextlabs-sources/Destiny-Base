package com.bluejungle.pf.destiny.services;

import java.rmi.RemoteException;

/**
 * Allow the other class call client.preapreForTests().
 * such for importexport unit test
 *
 * @author hchan
 * @date May 2, 2007
 */
public class PolicyEditorClientMock{
	public static void prepareForTest(PolicyEditorClient client) throws PolicyEditorException{
		try {
			client.prepareForTests();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
