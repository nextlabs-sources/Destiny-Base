package com.nextlabs.destiny.agent.controlmanager;

/*
 * Created on Aug 01, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateVisitor;
import com.bluejungle.pf.destiny.parser.DefaultPQLVisitor;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.DomainObjectDescriptor;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.action.IAction;

public class PolicyActionParser {
    private static final Log log = LogFactory.getLog(PolicyActionParser.class.getName());
    private Cache cache = null;

    public PolicyActionParser() {
        try {
            final CacheManager manager = CacheManager.create();
            cache = manager.getCache(PolicyActionParser.class.toString());

            if (cache == null) {
                cache = new Cache(PolicyActionParser.class.toString(), 50, false, false, 60, 60);

                manager.addCache(cache);
            }
        } catch (CacheException e) {
            cache = null;
            log.warn("Error creating cache - running cacheless", e);
        }
    }
    
    public List<String> getActions(String pql) {
        if (pql == null || pql.equals("")) {
            return new ArrayList<String>();
        }

        if (cache != null) {
            try {
                Element e = cache.get(pql);

                if (e != null) {
                    log.debug("Found actions in cache");
                    return (List<String>)e.getValue();
                } else {
                    log.debug("Did not find actions in cache");
                    List<String> actions = parseActions(pql);
                    cache.put(new Element(pql, actions));
                    return actions;
                }
            } catch (CacheException e) {
                log.error("Cache exception " + e);
            }
        }

        return parseActions(pql); 
    }
    
    private List<String> parseActions(String pql) {
        final List<String> actions = new ArrayList<>();
        try {
            DomainObjectBuilder.processInternalPQL(pql,
                                                   new DefaultPQLVisitor() {
                                                       @Override
                                                       public void visitPolicy(DomainObjectDescriptor descr, IDPolicy policy) {
                                                           IPredicate actionPred = policy.getEvaluationTarget().getActionPred();

                                                           actionPred.accept(new DefaultPredicateVisitor() {
                                                               @Override
                                                               public void visit(IPredicate pred) {
                                                                   if (pred instanceof IAction) {
                                                                       actions.add(((IAction)pred).getName());
                                                                   }
                                                               }
                                                           }, IPredicateVisitor.PREORDER);
                                                       }
                                                   });
        } catch (PQLException e) {
            log.error("Error parsing PQL: " + pql, e);
        }
        
        return actions;
    }
}
