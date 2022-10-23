package com.nextlabs.pf.destiny.lib;
/*
 * Created on Dec 10, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluejungle.framework.utils.IPair;
import com.bluejungle.framework.utils.Pair;

public class XACMLPoliciesResponseDTO implements Cloneable, Externalizable, Serializable {
    private static final long serialVersionUID = -2059020782898684015L;
    
    /**
     * The build time of the DTO. Used by CC to check to see if updates are
     * needed
     */
    private long buildTime;

    /**
     * The policies and their ids
     */
    private List<IPair<Long, String>> policiesAndPolicySets = new ArrayList<IPair<Long, String>>();

    /**
     * Serialization constructor
     */
    public XACMLPoliciesResponseDTO() {
    }

    /**
     * Builds empty DTO
     */
    public XACMLPoliciesResponseDTO(Date buildTime) {
        this.buildTime = buildTime.getTime();
    }

    /**
     * Access build time of this DTO
     */
    public Date getBuildTime() {
        return new Date(buildTime);
    }

    /**
     * Get all the policies
     */
    public List<IPair<Long, String>> getPoliciesAndPolicySets() {
        return Collections.unmodifiableList(policiesAndPolicySets);
    }

    /**
     * Add a new policy/policy set to the DTO
     */
    public void addPolicyOrPolicySet(Long policyId, String policyOrPolicySet) {
        policiesAndPolicySets.add(new Pair<Long, String>(policyId, policyOrPolicySet));
    }

    /**
     * Access the number of policies/policy sets in this DTO
     */
    public int numPolicies() {
        return policiesAndPolicySets.size();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Reads the data from the specified ObjectInput
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        buildTime = in.readLong();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            Long id = in.readLong();
            String xacml = (String)in.readObject();
            addPolicyOrPolicySet(id, xacml);
        }
    }

    /**
     * Writes the object data to the specified ObjectOutput
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(buildTime);
        out.writeInt(numPolicies());
        for (IPair<Long, String> pair : policiesAndPolicySets) {
            out.writeLong(pair.first());
            // writeUTF only handles strings up to 64K in length for
            // stupid reasons, so we'll use writeObject to be safe
            out.writeObject(pair.second());
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("XACML POLICIES\n==============\n");
        res.append(getBuildTime());
        res.append('\n');

        for (IPair<Long, String> pair : policiesAndPolicySets) {
            res.append(pair.first());
            res.append(": ");
            res.append(pair.second());
            res.append('\n');
        }

        return res.toString();
    }
}
