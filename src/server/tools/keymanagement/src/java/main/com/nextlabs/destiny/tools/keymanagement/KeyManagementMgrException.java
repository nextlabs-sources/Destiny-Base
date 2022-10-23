package com.nextlabs.destiny.tools.keymanagement;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.bluejungle.destiny.interfaces.secure_session.v1.AccessDeniedFault;
import com.nextlabs.destiny.services.keymanagement.ServiceNotReadyFault;
import com.nextlabs.destiny.services.keymanagement.UnauthorizedCallerFault;


class KeyManagementMgrException extends Exception {

    private static final long serialVersionUID = 1L;

    public KeyManagementMgrException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyManagementMgrException(String message) {
        super(message);
    }

    public KeyManagementMgrException(Throwable cause) {
        super(cause);
    }
    
    public KeyManagementMgrException(AccessDeniedFault fault) {
        this("The username or password is not correct.", fault);
    }
    
    public KeyManagementMgrException(UnauthorizedCallerFault fault) {
        this("Failed to authorize.", fault);
    }
    
    public KeyManagementMgrException(ServiceNotReadyFault fault) {
        this("Service is not ready.", fault);
    }
    
    public static KeyManagementMgrException create(java.rmi.RemoteException fault) {
        Throwable cause = fault.getCause();
        if (cause instanceof UnknownHostException ) {
            return new KeyManagementMgrException("Unknown Host: " + cause.getMessage() + ".  Check parameters.", fault);
        } else if (cause instanceof ConnectException) {
            return new KeyManagementMgrException ("Cannot connect to Policy Server.  Make sure it is running, and check parameters.", fault);
        } else if (cause instanceof SocketException) {
            return new KeyManagementMgrException ("Cannot connect to Policy Server successfully. Make sure the dkms is running, and check parameters.", fault);
        } else {        
            return new KeyManagementMgrException("Remote Exception: " + fault.getMessage(), fault);
        }
    }
}
