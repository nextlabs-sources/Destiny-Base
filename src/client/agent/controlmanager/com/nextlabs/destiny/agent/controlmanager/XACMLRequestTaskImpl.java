/*
 * Created on Nov 21, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import com.bluejungle.framework.threading.ITask;
import com.nextlabs.destiny.agent.pdpapi.IPDPGenericCallback;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

public class XACMLRequestTaskImpl implements ITask {
    private String xacmlRequest;
    private IPDPGenericCallback<IWrappedXACMLResponse> cb;
    private String dataType;
    
    public XACMLRequestTaskImpl(String xacmlRequest, String dataType, IPDPGenericCallback<IWrappedXACMLResponse> cb) {
        this.xacmlRequest = xacmlRequest;
        this.dataType = dataType;
        this.cb = cb;
    }

    public String getXACMLRequest() {
        return xacmlRequest;
    }

    public String getDataType() {
        return dataType;
    }
    
    public IPDPGenericCallback<IWrappedXACMLResponse> getCallback() {
        return cb;
    }
}
