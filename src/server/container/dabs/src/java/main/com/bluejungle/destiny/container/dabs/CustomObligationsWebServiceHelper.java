/*
 * Created on March 8, 2007
 * All sources, binaries and HTML pages (C) copyright 2007 by NextLabs Inc.,
 * San MateoCA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 */
 
package com.bluejungle.destiny.container.dabs;

import com.bluejungle.destiny.server.shared.configuration.ICustomObligationConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.ICustomObligationsConfigurationDO;
import com.bluejungle.destiny.types.custom_obligations.CustomObligation;
import com.bluejungle.destiny.types.custom_obligations.CustomObligationsData;


/*
 * This class converts custom obligation information between web-service objects and
 * DO/Data objects
 *
 * @author amorgan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/dabs/src/java/main/com/bluejungle/destiny/container/dabs/CustomObligationsWebServiceHelper.java#1 $
 */

public class CustomObligationsWebServiceHelper {
    public static CustomObligationsData convertFromCustomObligations(ICustomObligationsConfigurationDO conf) {
        ICustomObligationConfigurationDO[] obl = conf.getCustomObligations();

        CustomObligation[] wsObligations = new CustomObligation[obl.length];

        for (int i = 0; i < obl.length; i++) {
            CustomObligation customObligation = new CustomObligation();
            customObligation.setDisplayName(obl[i].getDisplayName());
            customObligation.setRunAt(obl[i].getRunAt());
            customObligation.setRunBy(obl[i].getRunBy());
            customObligation.setInvocationString(obl[i].getInvocationString());

            wsObligations[i] = customObligation;
        }

        CustomObligationsData customObligationsData = new CustomObligationsData();
        customObligationsData.setCustomObligation(wsObligations);

        return customObligationsData;
    }
}
