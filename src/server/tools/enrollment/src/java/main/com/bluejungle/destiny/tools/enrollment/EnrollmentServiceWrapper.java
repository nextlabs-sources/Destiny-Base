/*
 * Created on Jan 29, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.enrollment;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.services.enrollment.DictionaryFault;
import com.bluejungle.destiny.services.enrollment.DuplicatedFault;
import com.bluejungle.destiny.services.enrollment.EnrollmentFailedFault;
import com.bluejungle.destiny.services.enrollment.EnrollmentInternalFault;
import com.bluejungle.destiny.services.enrollment.EnrollmentServiceStub;
import com.bluejungle.destiny.services.enrollment.InvalidConfigurationFault;
import com.bluejungle.destiny.services.enrollment.NotFoundFault;
import com.bluejungle.destiny.services.enrollment.ReservedAttributeNameFault;
import com.bluejungle.destiny.services.enrollment.ServiceNotReadyFault;
import com.bluejungle.destiny.services.enrollment.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.enrollment.types.Column;
import com.bluejungle.destiny.services.enrollment.types.ColumnList;
import com.bluejungle.destiny.services.enrollment.types.EntityType;
import com.bluejungle.destiny.services.enrollment.types.Realm;
import com.bluejungle.destiny.services.enrollment.types.RealmList;

/**
 * similar interface to EnrollmentStub but less exception handling
 * 
 * @author hchan
 */

class EnrollmentServiceWrapper {
    private static final Log LOG = LogFactory.getLog(EnrollmentServiceWrapper.class);
    
    private final EnrollmentServiceStub stub;
    public EnrollmentServiceWrapper(EnrollmentServiceStub enrollmentStub) {
        stub = enrollmentStub;
    }
    
    public EnrollmentServiceStub getEnrollmentStub(){
        return stub;
    }
    
    private abstract class CommonRemoteExceptionHandler<T> {
        
        abstract T run() throws RemoteException;
        
        EnrollmentMgrException handleSpecific(Exception e) {
            LOG.error("", e);
            return EnrollmentMgrException.create(e);
        }
        
        T execute() throws EnrollmentMgrException{
            try {
                return run();
            } catch (RemoteException e) {
                throw handleSpecific(e);
            }
        }
    }
    
    
    RealmList getRealms(final String name) throws EnrollmentMgrException {
        return new CommonRemoteExceptionHandler<RealmList>() {
            @Override
            RealmList run() throws RemoteException {
                try {
					return stub.getRealms(name);
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (NotFoundFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (DictionaryFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				}
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                if(e instanceof NotFoundFault){
                    return new EnrollmentMgrException((NotFoundFault)e);
                }
                
                return super.handleSpecific(e);
            }
            
        }.execute();
    }

    void createRealm(final Realm realm) throws EnrollmentMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					stub.createRealm(realm);
                } catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (DictionaryFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (InvalidConfigurationFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				} catch (DuplicatedFault e) {
					LOG.error("", e);
		            throw new RemoteException(e.getMessage());
				}
                return null;
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                
                if (e instanceof InvalidConfigurationFault) {
                    return new EnrollmentMgrException((InvalidConfigurationFault) e);
                } else if (e instanceof DuplicatedFault) {
                    return new EnrollmentMgrException((DuplicatedFault) e);
                }
                
                return super.handleSpecific(e);
            }
            
        }.execute();
    }

    void updateRealm(final Realm realm) throws EnrollmentMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					stub.updateRealm(realm);
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (InvalidConfigurationFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (NotFoundFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DictionaryFault e) {
					LOG.error("", e);
					e.printStackTrace();
				}
                return null;
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                
                if (e instanceof InvalidConfigurationFault) {
                    return new EnrollmentMgrException((InvalidConfigurationFault) e);
                } else if (e instanceof NotFoundFault) {
                    return new EnrollmentMgrException((NotFoundFault) e);
                }
                
                return super.handleSpecific(e);
            }
            
        }.execute();
    }

    void deleteRealm(final Realm realm) throws EnrollmentMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					stub.deleteRealm(realm);
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (InvalidConfigurationFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (NotFoundFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DictionaryFault e) {
					LOG.error("", e);
					e.printStackTrace();
				}
                return null;
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                
                if (e instanceof InvalidConfigurationFault) {
                    return new EnrollmentMgrException((InvalidConfigurationFault) e);
                } else if (e instanceof NotFoundFault) {
                    return new EnrollmentMgrException((NotFoundFault) e);
                }
                
                return super.handleSpecific(e);
            }
            
        }.execute();
    }

    void enrollRealm(final Realm realm) throws EnrollmentMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					stub.enrollRealm(realm);
				} catch (EnrollmentFailedFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (InvalidConfigurationFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (NotFoundFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DictionaryFault e) {
					LOG.error("", e);
					e.printStackTrace();
				}
                return null;
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                if (e instanceof EnrollmentFailedFault) {
                    return new EnrollmentMgrException((EnrollmentFailedFault) e);
                } else if (e instanceof InvalidConfigurationFault) {
                    return new EnrollmentMgrException((InvalidConfigurationFault) e);
                } else if (e instanceof NotFoundFault) {
                    return new EnrollmentMgrException((NotFoundFault) e);
                }

                return super.handleSpecific(e);
            }

        }.execute();
    }

    ColumnList getColumns() throws EnrollmentMgrException {
        return new CommonRemoteExceptionHandler<ColumnList>() {
            @Override
            ColumnList run() throws RemoteException {
                try {
					return stub.getColumns();
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DictionaryFault e) {
					LOG.error("", e);
					e.printStackTrace();
				}
				return null;
            }
            
        }.execute();

    }

    void addColumn(final Column column) throws EnrollmentMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					stub.addColumn(column);
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (InvalidConfigurationFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DictionaryFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DuplicatedFault e) {
					LOG.error("", e);
					e.printStackTrace();
				}
                return null;
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                if (e instanceof InvalidConfigurationFault) {
                    return new EnrollmentMgrException((InvalidConfigurationFault) e);
                } else if (e instanceof DuplicatedFault) {
                    return new EnrollmentMgrException((DuplicatedFault) e);
                }

                return super.handleSpecific(e);
            }

        }.execute();
    }

    void delColumn(final String logicalName, final EntityType elementType) throws EnrollmentMgrException {
        new CommonRemoteExceptionHandler<Object>() {
            @Override
            Object run() throws RemoteException {
                try {
					stub.delColumn(logicalName, elementType);
				} catch (UnauthorizedCallerFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ReservedAttributeNameFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (EnrollmentInternalFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (InvalidConfigurationFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (NotFoundFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (ServiceNotReadyFault e) {
					LOG.error("", e);
					e.printStackTrace();
				} catch (DictionaryFault e) {
					LOG.error("", e);
					e.printStackTrace();
				}
                return null;
            }

            @Override
            EnrollmentMgrException handleSpecific(Exception e) {
                if (e instanceof InvalidConfigurationFault) {
                    return new EnrollmentMgrException((InvalidConfigurationFault) e);
                } else if (e instanceof ReservedAttributeNameFault) {
                    return new EnrollmentMgrException((ReservedAttributeNameFault) e);
                } else if (e instanceof NotFoundFault) {
                    return new EnrollmentMgrException((NotFoundFault) e);
                }

                return super.handleSpecific(e);
            }

        }.execute();
    }

}
