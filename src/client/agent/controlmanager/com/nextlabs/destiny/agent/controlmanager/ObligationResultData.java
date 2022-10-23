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

import java.util.ArrayList;
import java.util.List;

import com.bluejungle.framework.utils.StringUtils;

public class ObligationResultData implements IObligationResultData {
    private String obligationName;
    private String policyName;
    private List<String> arguments;

    public ObligationResultData(String obligationName) {
        this.obligationName = obligationName;
    }

    public ObligationResultData(String obligationName, String policyName, List<String> arguments) {
        this.obligationName = obligationName;
        this.policyName = policyName;
        this.arguments = arguments;
    }
  
    @Override
    public String getObligationName() {
        return obligationName;
    }
    
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    @Override
    public String getPolicyName() {
        return policyName;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public List<String> getArguments() {
        if (arguments == null) {
            arguments = new ArrayList<>();
        }
        
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(obligationName);
        sb.append('\n');
        sb.append("\tPolicy: ");
        sb.append(policyName);
        sb.append('\n');
        sb.append(StringUtils.join(arguments, ", "));

        return sb.toString();
    }
}
