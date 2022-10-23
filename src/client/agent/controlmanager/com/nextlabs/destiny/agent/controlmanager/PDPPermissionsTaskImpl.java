/*
 * Created on Jul 30, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.util.Map;

import com.bluejungle.framework.threading.ITask;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.nextlabs.destiny.agent.pdpapi.IPDPGenericCallback;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsResponse;

public class PDPPermissionsTaskImpl implements ITask {
    private Map<String, DynamicAttributes> arguments;
    private IPDPGenericCallback<IPDPPermissionsResponse> cb;

    public PDPPermissionsTaskImpl(Map<String, DynamicAttributes> arguments, IPDPGenericCallback<IPDPPermissionsResponse> cb) {
        this.arguments = arguments;
        this.cb = cb;
    }

    public Map<String, DynamicAttributes> getArguments() {
        return arguments;
    }

    public IPDPGenericCallback<IPDPPermissionsResponse> getCallback() {
        return cb;
    }
}
