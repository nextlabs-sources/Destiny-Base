/*
 * Created on Aug 03, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.evaluationconnector.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Response for permissions request
 */
public class PermissionsResponse implements Serializable {
    private static final long serialVersionUID = -4808320696131961427L;
    private String status;

    private PermissionsResponsesType responsesType;
    
    private Long requestStartTime = 0L;
    private Long permissionsEvalTime = 0L;
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the requestStartTime
     */
    public Long getRequestStartTime() {
        return requestStartTime;
    }

    /**
     * @param requestStartTime the requestStartTime to set
     */
    public void setRequestStartTime(Long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    /**
     * @return the permissionsEvalTime
     */
    public Long getPermissionsEvalTime() {
        return permissionsEvalTime;
    }

    /**
     * @param permissionsEvalTime the permissionsEvalTime to set
     */
    public void setPermissionsEvalTime(Long permissionsEvalTime) {
        this.permissionsEvalTime = permissionsEvalTime;
    }

    public void setResponsesType(PermissionsResponsesType responseType) {
        this.responsesType = responseType;
    }
    
    public PermissionsResponsesType getResponsesType() {
        return responsesType;
    }
}
