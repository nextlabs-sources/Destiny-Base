/*
 * Created on Aug 08, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.pf.domain.destiny.serviceprovider;

import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.pf.domain.epicenter.evaluation.IEvaluationRequest;
import com.bluejungle.pf.domain.destiny.serviceprovider.IServiceProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.ServiceProviderException;
import com.nextlabs.pf.domain.destiny.serviceprovider.types.GeneralProviderTypeSpecifier;

public interface IGeneralAttributeProvider extends IServiceProvider {
    IEvalValue getAttribute(IEvaluationRequest request, GeneralProviderTypeSpecifier type, String attributeName) throws ServiceProviderException;
}
