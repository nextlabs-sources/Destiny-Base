/*
 * Created on Apr 8, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2008 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc., All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.framework.heartbeat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.utils.StringUtils;

public class ServerHeartbeatManagerImpl implements IServerHeartbeatManager {
    private static final Log LOG = LogFactory.getLog(ServerHeartbeatManagerImpl.class.getName());
    
    private Map<String, IHeartbeatProvider> providers = new HashMap<String, IHeartbeatProvider>();

    synchronized public void register(String name, IHeartbeatProvider provider) {
        providers.put(name, provider);
        return;
    }

    synchronized public void unregister(String name, IHeartbeatProvider provider) {
        providers.remove(name);
    }

    synchronized public Serializable processHeartbeatPluginData(String name, String data) {
        IHeartbeatProvider provider = providers.get(name);
        
        if (provider == null) {
            LOG.warn("Recieved heartbeat request for " + name + " which is not registered on this system");

            if (LOG.isDebugEnabled()) {
                LOG.debug("Registered plugins: " + StringUtils.join(providers.keySet(), ", "));
            }
                    
            return null;
        }
        
        return provider.serviceHeartbeatRequest(name, data);
    }
}
