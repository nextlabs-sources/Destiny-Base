package com.nextlabs.evaluationconnector.parsers;

/*
 * Created on Aug 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import com.nextlabs.evaluationconnector.beans.PDPRequest;

public interface IPDPRequestValidator {
    boolean isValid(PDPRequest request);
}
