/*
 * Created on May 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc., Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved worldwide.
 */
package com.bluejungle.pf.tools;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bluejungle.framework.utils.UnmodifiableDate;
import com.bluejungle.pf.destiny.lifecycle.DeploymentType;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentEntity;
import com.bluejungle.pf.destiny.lifecycle.EntityManagementException;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.bluejungle.pf.destiny.lifecycle.LifecycleManager;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.subject.Location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Very simple tool to import locations.  A lot of hardcoded values.
 *
 * Each line in the input file should (after subtracting leading and trailing spaces) either
 *
 * a) Start with a #, in which case it is treated as a comment and skipped
 * b) Be blank. Also skipped
 * c) Be a location name, followed by some whitespace, followed by a CIDR ip range. The documentation
 *    states that quotes are required, but they are not
 *
 *    # valid
 *    location1   10.10.10.10/8
 *    # also valid
 *    location2   "10.10.10.10/8"
 *    # not valid
 *    location3   (10.10.10.10/8)
 *    # totally not valid
 *    location4   argle bargle!
 * 
 * 
 * @author sasha
 */

public class LocationImporter extends DeploymentToolsBase {
    public static final Pattern PATTERN = Pattern.compile("(\")?(([0-9]{1,3}\\.){3}[0-9]{1,3}(\\/([0-9]|[1-2][0-9]|3[0-2]))?)(\")?");
    
    private static final Log log = LogFactory.getLog(LocationImporter.class);
    
    void importLocations(BufferedReader in, boolean verbose) throws PQLException, Exception {
        // Read in the file, put the data into the defs Map:
        Map<String,String> defs = new HashMap<String,String>();
        
        String line;
        int i = 0;
        while ((line = in.readLine()) != null) {
            i++;
            line = line.trim();
            if (line.startsWith("#")) {
                continue;
            }
            if (line.length() == 0) {
                log.info("Blank line " + i + " Skipping");
                continue;
            }
            String[] values = line.split("\\s");
            if (values.length != 2
             || values[0] == null
             || values[0].length() == 0
             || values[1] == null
             || values[1].length() == 0 ) {
                log.warn("Invalid line " + i + " : " + line + "\nSkipping");
                continue;
            }
            
            String locationID = values[0];
            String locationRange = extractLocationRange(values[1]);

            if (locationRange == null) {
                log.warn("Skipping badly formatted location " + values[1]);
                continue;
            }
            
            if (defs.containsKey(locationID) ) {
                log.warn("Location '" + locationID + "' is redefined.\nSkipping");
                continue;
            }

            String pql =
                "id null status approved creator \"0\" "
            +   "ACCESS_POLICY "
            +   "ACCESS_CONTROL "
            +   "PBAC FOR * ON ADMIN BY PRINCIPAL.USER.NAME = RESOURCE.DSO.OWNER DO ALLOW "
            +   "ALLOWED_ENTITIES "
            +   "HIDDEN location " + locationID + " = \"" + locationRange + "\"";
            
            DomainObjectBuilder dob = new DomainObjectBuilder(pql);
            Location location;
            try {
                location = dob.processLocation();
            } catch (Exception e) {
                log.warn("Invalid line " + i + " : " + line + "\n" + e + "\n Skipping");
                continue;
            }
            if (location == null) {
                log.warn("Invalid line " + i + " : " + line + "\n Skipping");
                continue;
            }
            log.info("Importing location " + locationID + " = " + locationRange);
            defs.put( locationID, pql );
        }
        in.close();
        deployLocations( defs );
        
        log.info("SUCCESS: Imported " + defs.size() + " locations.");
    }

    /**
     * Extract location range
     */
    private String extractLocationRange(String range) {
        Matcher m = PATTERN.matcher(range);

        if (m.matches()) {
            // This should return the range, minus quotes (if they exist)
            return m.group(2);
        }

        return null;
    }
    
    /**
     * @param locations a <code>Map</code> of location names to PQL defs.
     * @throws EntityManagementException when the operation cannot complete.
     */
    private void deployLocations(Map<String,String> locations) throws EntityManagementException {
        LifecycleManager lm = (LifecycleManager) cm.getComponent(LifecycleManager.COMP_INFO);
        Collection<DevelopmentEntity> devs = lm.getEntitiesForNames( EntityType.LOCATION, locations.keySet(), LifecycleManager.MAKE_EMPTY );
        for ( DevelopmentEntity dev : devs ) {
            try {
                dev.setPql( locations.get( dev.getName() ) );
            } catch ( PQLException pqlEx ) {
                // This should not hapen
                assert false : "The PQL has been parsed before.";
            }
        }
        lm.deployEntities( devs, UnmodifiableDate.forTime(Calendar.getInstance().getTimeInMillis() + 60001), DeploymentType.PRODUCTION, true, null);
    }

}
