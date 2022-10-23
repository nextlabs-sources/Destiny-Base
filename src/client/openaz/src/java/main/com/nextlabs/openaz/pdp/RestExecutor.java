/*
 * Created on Aug 11, 2016
 *
 * All sources, binaries and HTML pages (C) copyright 2016 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author sduan
 */
package com.nextlabs.openaz.pdp;

import org.apache.http.entity.ContentType;

interface RestExecutor {
	/**
	 * @param request The request string
	 * @param contentType of the request
	 * @return Response string from server
	 * @throws RestExecutorException
	 */
	public String call(String request, ContentType contentType) throws RestExecutorException;

    /**
     * @param request the permissions request string
     * @param contentType of the request (only JSON supported)
     * @return PermissionsResonse string from server
     * @throws RestExecutorException
     */
    public String callPermissions(String request, ContentType contentType) throws RestExecutorException;
}
