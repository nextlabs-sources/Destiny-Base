/*
 * Created on Mar 16, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.enrollment;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * @author atian
 */

public class EnrollmentMgrException extends Exception {
    public EnrollmentMgrException(String msg) {
        super(msg);
    }

    public EnrollmentMgrException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.interfaces.secure_session.v1.AccessDeniedFault fault) {
        this("The user does not have permission to access this service.", fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.interfaces.secure_session.v1.InvalidPasswordFault fault) {
        this("The username or password is not correct.", fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.UnauthorizedCallerFault fault) {
        this("Failed to authorize.", fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.ServiceNotReadyFault fault) {
        this("Service is not ready.", fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.DictionaryFault fault) {
        this(fault.getFaultMessage() + "  Dictionary database problem. Please contact your system administration for more information.", fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.EnrollmentInternalFault fault) {
        this(fault.getFaultMessage().getEnrollmentInternalFault().getMsg(), fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.NotFoundFault fault) {
        this(fault.getFaultMessage().getNotFoundFault().getMsg(), fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.DuplicatedFault fault) {
        this(fault.getFaultMessage().getDuplicatedFault().getMsg(), fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.ReservedAttributeNameFault fault) {
        this(fault.getFaultMessage().getReservedAttributeNameFault().getMsg(), fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.InvalidConfigurationFault fault) {
        this(fault.getFaultMessage().getInvalidConfigurationFault().getMsg() + "  Check configuration.", fault);
    }
    
    public EnrollmentMgrException(com.bluejungle.destiny.services.enrollment.EnrollmentFailedFault fault) {
        this(fault.getFaultMessage().getEnrollmentFailedFault().getMsg(), fault);
    }
    
    public static EnrollmentMgrException create(Exception fault) {
        Throwable cause = fault.getCause();
        if (cause instanceof UnknownHostException ) {
            return new EnrollmentMgrException("Unknown Host: " + cause.getMessage() + ".  Check parameters.", fault);
        } else if (cause instanceof ConnectException) {
            return new EnrollmentMgrException ("Cannot connect to Policy Server.  Make sure it is running, and check parameters.", fault);
        } else if (fault.getMessage().contains("InvalidPasswordFault, Access Denied")) {
            return new EnrollmentMgrException("The username or password is not correct.");
        } else {
            return new EnrollmentMgrException("Remote Exception: " + fault.toString(), fault);
        }
    }
}



