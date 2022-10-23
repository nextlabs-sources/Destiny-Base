/*
 * Created on Sep 24, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.util.List;

public interface IObligationResultData {
    String getObligationName();

    String getPolicyName();

    List<String> getArguments();
}
