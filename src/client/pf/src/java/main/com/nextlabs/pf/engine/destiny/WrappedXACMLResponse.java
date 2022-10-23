/*
 * Created on May 04, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.pf.engine.destiny;

import java.util.Collections;
import java.util.List;

import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.Result;

public class WrappedXACMLResponse implements IWrappedXACMLResponse {
    private final ResponseCtx responseCtx;
    
    public WrappedXACMLResponse(ResponseCtx responseCtx) {
        this.responseCtx = responseCtx;
    }

    public static WrappedXACMLResponse buildIndeterminate(String reason) {
        List<String> code = Collections.singletonList(Status.STATUS_OK);

        ResponseCtx responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE,
                                                             new Status(code, reason)));

        return new WrappedXACMLResponse(responseCtx);
    }
    
    @Override
    public String encode(String dataType) {
        // This should work for either XML or JSON, but right now just XML
        return responseCtx.encode();
    }
}
