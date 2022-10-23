/*
 * Created on Jan 19, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.bluejungle.destiny.agent.pdpapi.IPDPApplication;
import com.bluejungle.destiny.agent.pdpapi.IPDPHost;
import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.IPDPResource;
import com.bluejungle.destiny.agent.pdpapi.IPDPSDKCallback;
import com.bluejungle.destiny.agent.pdpapi.IPDPUser;
import com.bluejungle.destiny.agent.pdpapi.PDPSDK;

/**
 * <p>
 * PDPRequest
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class PDPRequest {
	private String action = null;
	private IPDPUser user = null;
	private IPDPHost host = null;
	private IPDPApplication application = null;
	private IPDPResource[] resourceArr = null;
	private String[] recipients = null;
	private IPDPNamedAttributes[] additionalData = null;
	private PolicyOnDemand policyOnDemand = null;

    private boolean returnPolicyIdList = false;
	private IPDPSDKCallback callBack = IPDPSDKCallback.NONE;
	private int noiseLevel = PDPSDK.NOISE_LEVEL_USER_ACTION;

    // The default timeout (which was not settable) used to be 10
    // seconds. That's been too short in the past and we've used 30s
    // elsewhere, so we'll take that as the new default
	private int timeoutInMs = 30 * 1000;

	/**
	 * <p>
	 * Getter method for action
	 * </p>
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * <p>
	 * Setter method for action
	 * </p>
	 *
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * <p>
	 * Getter method for user
	 * </p>
	 * 
	 * @return the user
	 */
	public IPDPUser getUser() {
		return user;
	}

	/**
	 * <p>
	 * Setter method for user
	 * </p>
	 *
	 * @param user
	 *            the user to set
	 */
	public void setUser(IPDPUser user) {
		this.user = user;
	}

	/**
	 * <p>
	 * Getter method for host
	 * </p>
	 * 
	 * @return the host
	 */
	public IPDPHost getHost() {
		return host;
	}

	/**
	 * <p>
	 * Setter method for host
	 * </p>
	 *
	 * @param host
	 *            the host to set
	 */
	public void setHost(IPDPHost host) {
		this.host = host;
	}

	/**
	 * <p>
	 * Getter method for application
	 * </p>
	 * 
	 * @return the application
	 */
	public IPDPApplication getApplication() {
		return application;
	}

	/**
	 * <p>
	 * Setter method for application
	 * </p>
	 *
	 * @param application
	 *            the application to set
	 */
	public void setApplication(IPDPApplication application) {
		this.application = application;
	}

	/**
	 * <p>
	 * Getter method for resourceArr
	 * </p>
	 * 
	 * @return the resourceArr
	 */
	public IPDPResource[] getResourceArr() {
		return resourceArr;
	}

	/**
	 * <p>
	 * Setter method for resourceArr
	 * </p>
	 *
	 * @param resourceArr
	 *            the resourceArr to set
	 */
	public void setResourceArr(IPDPResource[] resourceArr) {
		this.resourceArr = resourceArr;
	}

	/**
	 * <p>
	 * Getter method for recipients
	 * </p>
	 * 
	 * @return the recipients
	 */
	public String[] getRecipients() {
		return recipients;
	}

	/**
	 * <p>
	 * Setter method for recipients
	 * </p>
	 *
	 * @param recipients
	 *            the recipients to set
	 */
	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	/**
	 * <p>
	 * Getter method for additionalData
	 * </p>
	 * 
	 * @return the additionalData
	 */
	public IPDPNamedAttributes[] getAdditionalData() {
		return additionalData;
	}

	/**
	 * <p>
	 * Setter method for additionalData
	 * </p>
	 *
	 * @param additionalData
	 *            the additionalData to set
	 */
	public void setAdditionalData(IPDPNamedAttributes[] additionalData) {
		this.additionalData = additionalData;
	}

    /**
	 * <p>
     * Getter for policy ids in the response
	 * </p>
     */
    public boolean isReturnPolicyIdList() {
        return returnPolicyIdList;
    }

    /**
	 * <p>
     * Setter for policyIdsInResponse
	 * </p>
     *
     * @param policyIdsInResponse
     */
    public void setReturnPolicyIdList(boolean returnPolicyIdList) {
        this.returnPolicyIdList = returnPolicyIdList;
    }
    
	/**
	 * <p>
	 * Getter method for callBack
	 * </p>
	 * 
	 * @return the callBack
	 */
	public IPDPSDKCallback getCallBack() {
		return callBack;
	}

	/**
	 * <p>
	 * Setter method for callBack
	 * </p>
	 *
	 * @param callBack
	 *            the callBack to set
	 */
	public void setCallBack(IPDPSDKCallback callBack) {
		this.callBack = callBack;
	}

	/**
	 * <p>
	 * Getter method for noiseLevel
	 * </p>
	 * 
	 * @return the noiseLevel
	 */
	public int getNoiseLevel() {
		return noiseLevel;
	}

	/**
	 * <p>
	 * Setter method for noiseLevel
	 * </p>
	 *
	 * @param noiseLevel
	 *            the noiseLevel to set
	 */
	public void setNoiseLevel(int noiseLevel) {
		this.noiseLevel = noiseLevel;
	}

	/**
	 * <p>
	 * Getter method for timeoutInMs
	 * </p>
	 * 
	 * @return the timeoutInMs
	 */
	public int getTimeoutInMs() {
		return timeoutInMs;
	}

	/**
	 * <p>
	 * Setter method for timeoutInMs
	 * </p>
	 *
	 * @param timeoutInMs the timeoutInMs to set
	 */
	public void setTimeoutInMs(int timeoutInMs) {
		this.timeoutInMs = timeoutInMs;
	}

	/**
	 * <p>
	 * Getter method for policyOnDemand
	 * </p>
	 * 
	 * @return the policyOnDemand
	 */
	public PolicyOnDemand getPolicyOnDemand() {
		return policyOnDemand;
	}

	/**
	 * <p>
	 * Setter method for policyOnDemand
	 * </p>
	 *
	 * @param policyOnDemand
	 *            the policyOnDemand to set
	 */
	public void setPolicyOnDemand(PolicyOnDemand policyOnDemand) {
		this.policyOnDemand = policyOnDemand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
