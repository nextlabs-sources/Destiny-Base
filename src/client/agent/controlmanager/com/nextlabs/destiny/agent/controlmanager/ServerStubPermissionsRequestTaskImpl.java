/*
 * Created on Jun 04, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.util.List;

import com.bluejungle.destiny.agent.controlmanager.IPDPJni;
import com.bluejungle.framework.threading.ITask;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsResponse;

public class ServerStubPermissionsRequestTaskImpl implements ITask {
    private Object[] inputParams;
    private IPDPJni pdpJni;
    private String requestID;
    private long handle;
    private IServerStubPermissionsResponseHandler responseHandler;

    public ServerStubPermissionsRequestTaskImpl(IPDPJni pdpJni, String requestID, Object[] inputParams, long handle, IServerStubPermissionsResponseHandler responseHandler) {
        this.pdpJni = pdpJni;
        this.requestID = requestID;
        this.inputParams = inputParams;
        this.handle = handle;
        this.responseHandler = responseHandler;
    }

    public Object[] getInputParams() {
        return inputParams;
    }

    public IPDPJni getOSWrapper() {
        return pdpJni;
    }

    public String getRequestID() {
        return requestID;
    }

    public long getHandle() {
        return handle;
    }

    public IServerStubPermissionsResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public static interface IServerStubPermissionsResponseHandler {
        void respond(ServerStubPermissionsRequestTaskImpl request, String[] resultActionEnumeration, String[][] policiesAndObligations);
    }
}
