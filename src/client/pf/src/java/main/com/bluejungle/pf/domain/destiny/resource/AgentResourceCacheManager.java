/*
 * Created on Nov 05, 2010
 *
 * All sources, binaries and HTML pages (C) copyright 2010 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.pf.domain.destiny.resource;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.MemoryUnit;

import com.bluejungle.domain.agenttype.AgentTypeEnumType;

class AgentResourceCacheManager implements IAgentResourceCacheManager {
    private CacheManager cacheManager;
    private Ehcache cache;
    private Log log;

    private static final String CACHE_NAME = AgentResourceManager.class.getName();
    
    private static final String DISK_CACHE_FILE_PREFIX = "evalcache";

    public AgentResourceCacheManager(AgentTypeEnumType agentType, int cacheSizeMB) {
        log = LogFactory.getLog(AgentResourceCacheManager.class.getName());

        try {
            Configuration cacheManagerConfig = new Configuration();

            CacheConfiguration cacheConfig = new CacheConfiguration()
                                             .name(CACHE_NAME)
                                             .maxBytesLocalHeap(cacheSizeMB, MemoryUnit.MEGABYTES);

            cacheManagerConfig.addCache(cacheConfig);
            
            cacheManager = CacheManager.create(cacheManagerConfig);
        } catch (CacheException e) {
            throw new RuntimeException(e);
        }
        
        cache = cacheManager.getEhcache(CACHE_NAME);
    }

    // Exposed for unit tests
    CacheManager getCacheManager(){
        return cacheManager;
    }

    public void clearCache() {
        cache.removeAll();
    }

    public synchronized void dispose() {
        cacheManager.shutdown();
        cache = null;
    }
    

    public void put(Element e) {
        cache.put(e);
    }

    public Element get(Serializable id) {
        Element element = null;
        try {
            element = cache.get(id);
        } catch (CacheException e) {
            if (log.isWarnEnabled()) {
                log.warn("Error accessing resource cache", e);
            }
            // no biggy, just no caching
        }
        
        return element;
    }

    public void remove(Serializable id) {
        cache.remove(id);
    }
}
