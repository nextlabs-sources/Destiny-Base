/*
 * Created on Aug 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.beans.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PermissionsResponseWrapper implements Serializable {
    private static final long serialVersionUID = 4775404020093376340L;
    
    private Status Status;
    private List<PermissionsResult> Response;

    public PermissionsResponseWrapper() {
        super();
        Response = new ArrayList<PermissionsResult>();
    }

    /**
     * <p>
     * Getter method for response
     * </p>
     *
     * @return the response
     */
    public List<PermissionsResult> getResponse() {
        return Response;
    }

    /**
     * <p>
     * Setter method for response
     * </p>
     *
     * @param response the response to set
     */
    public void setResponse(List<PermissionsResult> response) {
        if (response == null) {
            response = new ArrayList<PermissionsResult>();
        }

        Response = response;
    }
    
	/**
	 * <p>
	 * Getter method for status
	 * </p>
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return Status;
	}

	/**
	 * <p>
	 * Setter method for status
	 * </p>
	 *
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		Status = status;
	}


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
