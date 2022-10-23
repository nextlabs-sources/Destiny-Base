/*
 * Created 2004
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author unknown
 * @author amorgan
 */
package com.bluejungle.destiny.agent.controlmanager;

import com.bluejungle.framework.comp.ComponentImplBase;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;

public class PDPJni extends ComponentImplBase implements IPDPJni, IHasComponentInfo<PDPJni> {

    public static final String NAME = PDPJni.class.getName();

    private static final ComponentInfo<PDPJni> COMP_INFO = new ComponentInfo<PDPJni>(
    		NAME, 
    		PDPJni.class, 
    		IPDPJni.class, 
    		LifestyleType.SINGLETON_TYPE);


    /**
     * @return ComponentInfo to help creating an instance with Component Manager
     * 
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<PDPJni> getComponentInfo() {
        return COMP_INFO;
    }

    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
    }

    /**
     * @see com.bluejungle.framework.comp.IStartable#start()
     */
    public void start() {

    }

    /**
     * @see com.bluejungle.framework.comp.IDisposable#dispose()
     */
    public void dispose() {
    }

    /**
     * @see com.bluejungle.framework.comp.IStartable#stop()
     */
    public void stop() {

    }
    public native boolean SendPolicyResponse(long handle, String uniqueNumber, long allow, String[] attrs);

    public native boolean SendPolicyMultiResponse(long handle, String uniqueNumber, Object[] responses);

    public native boolean SendServiceResponse(long handle, String reqId, Object[] response);

    public native boolean SendPermissionsResponse(long handle, String uniqueNumber, Object[] responses);
}
