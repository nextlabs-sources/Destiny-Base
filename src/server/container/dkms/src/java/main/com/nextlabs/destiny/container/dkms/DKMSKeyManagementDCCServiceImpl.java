package com.nextlabs.destiny.container.dkms;

import com.nextlabs.destiny.services.keymanagement.KeyManagementDCCIF;
import com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault;
import com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault;
import com.nextlabs.destiny.services.keymanagement.types.KeyDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyIdDTO;
import com.nextlabs.destiny.services.keymanagement.KeyManagementFault;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingNamesDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingWithKeysDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingsWithKeysDTO;

public class DKMSKeyManagementDCCServiceImpl implements KeyManagementDCCIF {

    DKMSKeyManagementServiceImpl impl = new DKMSKeyManagementServiceImpl();
    
    @Override
    public KeyRingDTO createKeyRing(String keyRingName) throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        return impl.createKeyRing(keyRingName);
    }

    @Override
    public KeyRingNamesDTO getKeyRingNames() throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        return impl.getKeyRingNames();
    }

    @Override
    public KeyRingDTO getKeyRing(String keyRingName) throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        return impl.getKeyRing(keyRingName);
    }

    @Override
    public KeyRingWithKeysDTO getKeyRingWithKeys(String keyRingName)
            throws UnauthorizedCallerFault,
            ServiceNotReadyFault, KeyManagementFault {
        return impl.getKeyRingWithKeys(keyRingName);
    }

    @Override
    public void deleteKeyRing(String keyRingName) throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        impl.deleteKeyRing(keyRingName);
    }

    @Override
	public KeyIdDTO generateKey(String keyRingName, int keyLength)
			throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
		return impl.generateKey(keyRingName, keyLength);
	}

    @Override
    public void setKey(String keyRingName, KeyDTO key) throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        impl.setKey(keyRingName, key);
    }

    @Override
	public KeyDTO getKey(String keyRingName, KeyIdDTO keyId)
			throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
		return impl.getKey(keyRingName, keyId);
	}

    @Override
    public void deleteKey(String keyRingName, KeyIdDTO keyId) throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        impl.deleteKey(keyRingName, keyId);
    }

    @Override
    public long getAllLatestModifiedDate() throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        return impl.getAllLatestModifiedDate();
    }

    @Override
    public KeyRingsWithKeysDTO getAllKeyRingsWithKeys() throws UnauthorizedCallerFault, ServiceNotReadyFault, KeyManagementFault {
        return impl.getAllKeyRingsWithKeys();
    }

}
