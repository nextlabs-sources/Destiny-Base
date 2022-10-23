/*
 * Created on Sep 12, 2013
 *
 * All sources, binaries and HTML pages (C) copyright 2013 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/dabs/src/java/main/com/nextlabs/destiny/bindings/log/v3/DABSLogServiceWSConverter.java#1 $:
 */
package com.nextlabs.destiny.bindings.log.v3;

import com.bluejungle.domain.log.*;
import com.nextlabs.domain.log.PolicyActivityLogEntryV3;
import com.nextlabs.domain.log.TrackingLogEntryV3;

import java.io.*;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is a utility class to covert activity log objects from their internal types
 * to serialized string. This class will be versioned according to the different 
 * versions of the dabs log service (DABSLogServiceImpl)
 * 
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/dabs/src/java/main/com/nextlabs/destiny/bindings/log/v3/DABSLogServiceWSConverter.java#1 $
 */

public class DABSLogServiceWSConverter {
    /**
     * This is the method that reads TrackingLogEntry from a ObjectInput
     * 
     * @param in
     * @return a single TrackingLogEntry from input
     * @throws IOException
     */
    public static TrackingLogEntry readExternalTrackingLog(ObjectInput in) throws IOException {
        TrackingLogEntry logEntry = new TrackingLogEntry();

        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
        }

        return logEntry;
    }

    /**
     * This is the method that serializes a single TrackingLogEntry to ObjectOutput
     * 
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalTrackingLog(ObjectOutput out, TrackingLogEntry entry) throws IOException {
        entry.writeExternal(out);
    }

    /**
     * This is the method that reads TrackingLogEntryV2 from a ObjectInput
     * 
     * @param in
     * @return a single TrackingLogEntry from input
     * @throws IOException
     */
    public static TrackingLogEntryV2 readExternalTrackingLogV2(ObjectInput in) throws IOException {
        TrackingLogEntryV2 logEntry = new TrackingLogEntryV2();
        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
            
        }
        return logEntry;
    }

    /**
     * This is the method that serializes a single TrackingLogEntryV2 to ObjectOutput
     * 
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalTrackingLogV2(ObjectOutput out, TrackingLogEntryV2 entry) throws IOException {
        entry.writeExternal(out);
    }
    /**
     * This is the method that reads TrackingLogEntryV3 from a ObjectInput
     * 
     * @param in
     * @return a single TrackingLogEntry from input
     * @throws IOException
     */
    public static TrackingLogEntryV3 readExternalTrackingLogV3(ObjectInput in) throws IOException {
        TrackingLogEntryV3 logEntry = new TrackingLogEntryV3();
        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
            
        }
        return logEntry;
    }

    /**
     * This is the method that serializes a single TrackingLogEntryV2 to ObjectOutput
     * 
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalTrackingLogV3(ObjectOutput out, TrackingLogEntryV3 entry) throws IOException {
        entry.writeExternal(out);
    }

    
    /**
     * This is the method that reads PolicyActivityLogEntry from a ObjectInput
     * 
     * @param in
     * @return a single PolicyActivityLogEntry from input
     * @throws IOException
     */
    public static PolicyActivityLogEntry readExternalPolicyLog(ObjectInput in) throws IOException {
        PolicyActivityLogEntry logEntry = new PolicyActivityLogEntry();

        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
        }

        return logEntry;
    }

    /**
     * This is the method that serializes a single PolicyActivityLogEntry to ObjectOutput
     * 
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalPolicyLog(ObjectOutput out, PolicyActivityLogEntry entry) throws IOException {
        entry.writeExternal(out);
    }

    /**
     * This is the method that reads PolicyActivityLogV2 from a ObjectInput
     * 
     * @param in
     * @return a single TrackingLogEntry from input
     * @throws IOException
     */
    public static PolicyActivityLogEntryV2 readExternalPolicyLogV2(ObjectInput in) throws IOException {
        PolicyActivityLogEntryV2 logEntry = new PolicyActivityLogEntryV2();
        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
        }
        return logEntry;
    }

    /**
     * This is the method that serializes a single PolicyActivityLogEntryV2 to ObjectOutput
     * 
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalPolicyLogV2(ObjectOutput out, PolicyActivityLogEntryV2 entry) throws IOException {
        entry.writeExternal(out);
    }

    /**
     * This is the method that reads PolicyActivityLogV3 from a ObjectInput
     * 
     * @param in
     * @return a single TrackingLogEntry from input
     * @throws IOException
     */
    public static PolicyActivityLogEntryV3 readExternalPolicyLogV3(ObjectInput in) throws IOException {
        PolicyActivityLogEntryV3 logEntry = new PolicyActivityLogEntryV3();
        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
        }
        return logEntry;
    }

    /**
     * This is the method that serializes a single PolicyActivityLogEntryV3 to ObjectOutput
     * 
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalPolicyLogV3(ObjectOutput out, PolicyActivityLogEntryV3 entry) throws IOException {
        entry.writeExternal(out);
    }

    /**
     * This is the method that reads PolicyAssistantLogEntry from a ObjectInput
     * 
     * @param in
     * @return a single PolicyAssistantLogEntry from input
     * @throws IOException
     */
    public static PolicyAssistantLogEntry readExternalPolicyAssistantLog(ObjectInput in) throws IOException {
        PolicyAssistantLogEntry logEntry = new PolicyAssistantLogEntry();
        try {
            logEntry.readExternal(in);
        } catch (ClassNotFoundException e) {
        }

        return logEntry;
    }

    /**
     * This is the method that serializes a single PolicyAssistantLogEntry to ObjectOutput
     *
     * @param out
     * @param entry
     * @throws IOException
     */
    public static void writeExternalPolicyAssistantLog(ObjectOutput out, PolicyAssistantLogEntry entry) throws IOException {
        entry.writeExternal(out);
    }
    
    /**
     * Serializes, compresses, and encodes (Base64) collection of log entries.
     * The enclosing collection is not serialized, instead the number of entries
     * is the first entry (int) in the serialized stream.
     * 
     * @param logEntries entries to serialize, must be Externalizable
     * @return String encoding of serialized and compressed log entries
     * @throws IOException
     */
    public static final String encodeLogEntries(Collection logEntries) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        GZIPOutputStream zipStream = new GZIPOutputStream(outStream);
        ObjectOutputStream oos = new ObjectOutputStream(zipStream);
        
        oos.writeInt(logEntries.size());
        for (Iterator iter = logEntries.iterator(); iter.hasNext();) {
            BaseLogEntry entry = (BaseLogEntry) iter.next();
            if (entry instanceof TrackingLogEntry){
                writeExternalTrackingLog(oos, (TrackingLogEntry)entry);
            } else if (entry instanceof TrackingLogEntryV2) {
                writeExternalTrackingLogV2(oos, (TrackingLogEntryV2)entry);
            } else if (entry instanceof TrackingLogEntryV3) {
                writeExternalTrackingLogV3(oos, (TrackingLogEntryV3)entry);
            } else if (entry instanceof PolicyActivityLogEntry) {
                writeExternalPolicyLog(oos, (PolicyActivityLogEntry)entry);
            } else if (entry instanceof PolicyActivityLogEntryV2) {
                writeExternalPolicyLogV2(oos, (PolicyActivityLogEntryV2)entry);
            } else if (entry instanceof PolicyActivityLogEntryV3) {
                writeExternalPolicyLogV3(oos, (PolicyActivityLogEntryV3)entry);
            } else {
                writeExternalPolicyAssistantLog(oos, (PolicyAssistantLogEntry)entry);
            }
        }
        oos.close();

        byte[] bytes = outStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Decodes passed in encoded (Base64) and compressed data and provides
     * an ObjectInputStream
     * 
     * @param data data to decode
     * @return ObjectInputStream decoded from the data
     * @throws IOException
     */
    public static final ObjectInputStream decodeData(String data) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(data);
        
        return (new ObjectInputStream (new GZIPInputStream (new ByteArrayInputStream(bytes))));
    }

}
