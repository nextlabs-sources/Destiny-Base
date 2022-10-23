package com.nextlabs.destiny.tools.keymanagement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.destiny.services.keymanagement.KeyManagementServiceStub;
import com.nextlabs.destiny.services.keymanagement.types.KeyDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyIdDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingNamesDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingWithKeysDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingsWithKeysDTO;

public class KeyManagementServiceWrapper {
    private static final Log LOG = LogFactory.getLog(KeyManagementServiceWrapper.class);
    
    private final KeyManagementServiceStub port;
    public KeyManagementServiceWrapper(KeyManagementServiceStub keyManagementIF) {
        this.port = keyManagementIF;
    }
    
    public KeyManagementServiceStub getKeyManagementStub(){
        return port;
    }
    
    private abstract class CommonRemoteExceptionHandler<T> {
        
        abstract T run() throws RemoteException;
        
        KeyManagementMgrException handleSpecific(RemoteException e) {
            LOG.error("", e);
            return KeyManagementMgrException.create(e);
        }
        
        T execute() throws KeyManagementMgrException{
            try {
                return run();
            } catch (RemoteException e) {
                throw handleSpecific(e);
            }
        }
    }
    
    public KeyRingDTO createKeyRing(final String keyRingName) 
            throws KeyManagementMgrException {
        return new CommonRemoteExceptionHandler<KeyRingDTO>() {
            @Override
            KeyRingDTO run() throws RemoteException {
                try {
					return port.createKeyRing(keyRingName);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());
				}
            }
        }.execute();
    }

    public Set<String> getKeyRingNames() 
            throws KeyManagementMgrException {
        KeyRingNamesDTO keyRingNamesDTO = new CommonRemoteExceptionHandler<KeyRingNamesDTO>() {
            @Override
            KeyRingNamesDTO run() throws RemoteException {
                try {
					return port.getKeyRingNames();
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());

				}
            }
        }.execute();
        
        Set<String> names = new HashSet<String>();
        if (keyRingNamesDTO != null && keyRingNamesDTO.getKeyRingNames() != null) {
            Collections.addAll(names, keyRingNamesDTO.getKeyRingNames());
        }
        return names;
    }

    public KeyRingDTO getKeyRing(final String keyRingName) 
            throws KeyManagementMgrException {
        return new CommonRemoteExceptionHandler<KeyRingDTO>() {
            @Override
            KeyRingDTO run() throws RemoteException {
                try {
					return port.getKeyRing(keyRingName);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());

				}
            }
        }.execute();
    }

    public KeyRingWithKeysDTO getKeyRingWithKeys(final String keyRingName) 
            throws KeyManagementMgrException {
        return new CommonRemoteExceptionHandler<KeyRingWithKeysDTO>() {
            @Override
            KeyRingWithKeysDTO run() throws RemoteException {
                try {
					return port.getKeyRingWithKeys(keyRingName);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());

				}
            }
        }.execute();
    }

    public void deleteKeyRing(final String keyRingName)
            throws KeyManagementMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					port.deleteKeyRing(keyRingName);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());
				}
                return null;
            }
        }.execute();
    }

    public KeyIdDTO generateKey(final String keyRingName, final int keyLength)
            throws KeyManagementMgrException {
        return new CommonRemoteExceptionHandler<KeyIdDTO>() {
            @Override
            KeyIdDTO run() throws RemoteException {
                try {
					return port.generateKey(keyRingName, keyLength);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());

				}
            }
        }.execute();
    }

    public void setKey(final String keyRingName, final KeyDTO key)
            throws KeyManagementMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					port.setKey(keyRingName, key);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());

				}
                return null;
            }
        }.execute();
    }

    public KeyDTO getKey(final String keyRingName, final KeyIdDTO keyId)
            throws KeyManagementMgrException {
        return new CommonRemoteExceptionHandler<KeyDTO>() {
            @Override
            KeyDTO run() throws RemoteException {
                try {
					return port.getKey(keyRingName, keyId);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());

				}
            }
        }.execute();
    }

    public void deleteKey(final String keyRingName, final KeyIdDTO keyId)
            throws KeyManagementMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					port.deleteKey(keyRingName, keyId);
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());
				}
                return null;
            }
        }.execute();
    }

    public long getAllLatestModifiedDate() throws KeyManagementMgrException {
        return new CommonRemoteExceptionHandler<Long>() {
            @Override
            Long run() throws RemoteException {
                try {
					return port.getAllLatestModifiedDate();
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());
				}
            }
        }.execute();
    }

    public List<KeyRingWithKeysDTO> getAllKeyRingsWithKeys()
            throws KeyManagementMgrException {
        KeyRingsWithKeysDTO keyRingsWithKeysDTO = new CommonRemoteExceptionHandler<KeyRingsWithKeysDTO>() {
            @Override
            KeyRingsWithKeysDTO run() throws RemoteException {
                try {
					return port.getAllKeyRingsWithKeys();
				} catch (com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.KeyManagementFault e) {
					throw new RemoteException(e.getMessage());
				} catch (com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault e) {
					throw new RemoteException(e.getMessage());
				}
            }
        }.execute();
        
        List<KeyRingWithKeysDTO> keyRings = new ArrayList<KeyRingWithKeysDTO>();
        if (keyRingsWithKeysDTO != null && keyRingsWithKeysDTO.getKeyRings() != null) {
            Collections.addAll(keyRings, keyRingsWithKeysDTO.getKeyRings());
        }
        return keyRings;
    }
    
}
