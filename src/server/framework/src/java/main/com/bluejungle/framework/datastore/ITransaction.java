package com.bluejungle.framework.datastore;

// Copyright Blue Jungle, Inc.

/*
 * @author Sasha Vladimirov
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/framework/src/java/main/com/bluejungle/framework/datastore/ITransaction.java#1 $
 */

public interface ITransaction {
	void commit();
	void rollback();

	IDataStoreSession getSession();
    

}
