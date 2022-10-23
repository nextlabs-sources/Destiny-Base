/*
 * Created on Oct 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

import com.bluejungle.destiny.agent.activityjournal.IActivityJournal;
import com.bluejungle.destiny.agent.activityjournal.LogIdGenerator;
import com.bluejungle.destiny.agent.controlmanager.CustomObligationsContainer;
import com.bluejungle.destiny.agent.controlmanager.IControlManager;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IDisposable;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.PropertyKey;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IMultivalue;
import com.bluejungle.framework.expressions.ValueType;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.framework.utils.SerializationUtils;
import com.bluejungle.framework.utils.StringUtils;
import com.bluejungle.pf.domain.destiny.obligation.AgentCustomObligation;
import com.bluejungle.pf.domain.destiny.obligation.AgentDisplayObligation;
import com.bluejungle.pf.domain.destiny.obligation.AgentLogObligation;
import com.bluejungle.pf.domain.destiny.obligation.AgentNotifyObligation;
import com.bluejungle.pf.domain.destiny.obligation.ILoggingLevel;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.bluejungle.pf.domain.epicenter.evaluation.IEvaluationRequest;
import com.bluejungle.pf.domain.epicenter.resource.IResource;
import com.bluejungle.pf.engine.destiny.ClientInformation;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.nextlabs.domain.log.PolicyActivityInfoV5;
import com.nextlabs.domain.log.PolicyActivityLogEntryV5;


/**
 * This class encapsulates all the logic needed by the obligation
 * handling code for the PDP. This will send log and notify
 * obligations and pre-process any custom obligations needed.
 */
public class ObligationHandler implements IConfigurable, IDisposable, IInitializable, ILogEnabled, IObligationHandler {
    // Also used in EvaluationContext. TODO - Move some of this code to common location
    private static final String argumentRegex = "(\\$((application|environment|from|resource|to|user)(\\.[A-Za-z0-9:_-]+){1,2})(@[A-Za-z_]+)*|\\$CE[A-Za-z]+)";
    private static final Pattern argumentPattern = Pattern.compile(argumentRegex);
    
    private static final long UNKNOWN_POLICY_ID = -1L;
        
    private IConfiguration configuration;
    private Log log = null;
    private IActivityJournal activityJournal;
    private IControlManager controlManager;
    private Map<String, IDObligationExecutor> obligationMap = null;
    private boolean shuttingDown = false;
    
    /**
     * These are used to queue the custom obligation executions and running these
     * on a separate thread.
     */
    private BlockingQueue<Runnable> asyncTaskQ;
    private AsyncTaskExecutor asyncTaskExecutor;
    private Thread asyncTaskExecutorThread;
    
    /**
     * This is responsible for running all the async tasks by pulling them out of the 
     * queue. It blocks till the next task is available on the queue.
     */
    private class AsyncTaskExecutor implements Runnable {
        private final BlockingQueue<Runnable> queue;

        public AsyncTaskExecutor(BlockingQueue<Runnable> queue) {
            super();
            this.queue = queue;
        }

        public void run() {
            try {
                if (getLog().isTraceEnabled()) {
                    getLog().trace("Starting the Async Task Executor thread");
                }
                while (true) { 
                    queue.take().run();
                }
            } catch (InterruptedException ex) { 
                if (!shuttingDown && getLog().isErrorEnabled()) {
                    getLog().error("The async task executor thread was interrupted and will die", ex);
                }
            }
        }
    } // class AsyncTaskExecutor


    interface IDObligationExecutor {
        boolean shouldPerform(PerformObligationsEnumType type);
        void preamble(ObligationContext obligationContext, IDObligation obl);
        void execute(ObligationContext obligationContext, IDObligation obl);
    }

    private IDObligationExecutor NotifyExecutor = new IDObligationExecutor() {
        public boolean shouldPerform(PerformObligationsEnumType type) {
            return type == PerformObligationsEnumType.ALL;
        }
        
        public void preamble(ObligationContext obligationContext, IDObligation obl) {
            if (obligationContext.getEvaluationResult().getPAInfo().getLevel() == ILoggingLevel.LOG_LEVEL_USER) {
                obligationContext.addDescription((AgentNotifyObligation)obl, "Email");
            }
        }

        public void execute(ObligationContext obligationContext, IDObligation obl) {
            AgentNotifyObligation realObl = (AgentNotifyObligation)obl;

            if (obligationContext.getEvaluationResult().getPAInfo().getLevel() == ILoggingLevel.LOG_LEVEL_USER) {
                String body = realObl.createBody(obligationContext.getEvaluationResult().getPAInfo());
                String subject = "Notification";

                realObl.getExecutor().notify(null, realObl.getEmailAddresses(), subject, body);
            }
        }
    };

    private IDObligationExecutor DisplayExecutor = new IDObligationExecutor() {
        public boolean shouldPerform(PerformObligationsEnumType type) {
            return type == PerformObligationsEnumType.ALL || type == PerformObligationsEnumType.PEP_ONLY;
        }
        
        private boolean appNameMatches(String appName, String nameToMatch) {
            return appName.equals(nameToMatch) || appName.endsWith("/" + nameToMatch) || appName.endsWith("\\" + nameToMatch);
        }
        
        private boolean doDisplay(EvaluationResult res) {
            // Only do display notification for USER events
            if (res.getPAInfo().getLevel() != ILoggingLevel.LOG_LEVEL_USER) {
                return false;
            }

            String appName = res.getPAInfo().getApplicationName().toLowerCase();
            String resName = res.getPAInfo().getFromResourceInfo().getName();

            // If the action is OPEN by either explorer or acroread32info then we don't want
            // to do display (except when the file is http:)
            return (!(ActionEnumType.ACTION_OPEN.getName().equals(res.getPAInfo().getAction()) && appName != null &&
                      !resName.startsWith("http:") &&
                      (appNameMatches(appName, "explorer.exe") || appNameMatches(appName, "acrord32info.exe"))));
        }

        
        public void preamble(ObligationContext obligationContext, IDObligation obl) {
            if (doDisplay(obligationContext.getEvaluationResult())) {
                obligationContext.addDescription((AgentDisplayObligation)obl, "Display: " + ((AgentDisplayObligation)obl).getMessage());
            }
        }

        public void execute(ObligationContext obligationContext, IDObligation obl) {
            final AgentDisplayObligation realObl = (AgentDisplayObligation)obl;
                
            EvaluationResult res = obligationContext.getEvaluationResult();
            if (doDisplay(res)) {
                asyncTaskQ.add(new Runnable() {
                    public void run() {
                        controlManager.notifyUser(res.getEvaluationRequest().getLoggedInUser().getUid(),
                                                  Calendar.getInstance().getTime().toString(),
                                                  res.getPAInfo().getAction(),
                                                  res.getEffectName(),
                                                  res.getPAInfo().getFromResourceInfo().getName(),
                                                  realObl.getMessage());
                    }
                });
            }
        }
    };

    /*
     * Stores arguments and hints.
     *
     * Arguments can be followed by @<hint>, which tell us how the
     * values should be formatted. This is critical if they values
     * are part of a larger string (say, SQL)
     *
     * The hints supported are
     *    @comma          multivalues should be separated by a comma
     *    @semicolon      multivalues should be separated by a semicolon
     *    @space          multivalues should be separated by a space
     *    @singlequote    all values (single and multi) should be surrounded by ' '
     *    @doublequote    all values (single and multi) should be surrounded by " "
     *
     * Unpredictable behavior will result if you specify more than one of comma, semicolon, or space.
     * Likewise with singlequote and doublequote
     */
    private class ObligationArgumentWithHints {
        private String argument;
        private Set<String> hints = new HashSet<>();;
        
        public ObligationArgumentWithHints(String argument) {
            // The attribute name will never contain @, so we can safely split on this
            String[] argumentPieces = argument.split("@");
            
            this.argument = argumentPieces[0];
            
            for (int i = 1; i < argumentPieces.length; i++) {
                hints.add(argumentPieces[i]);
            }
        }
        
        public String getArgument() {
            return argument;
        }
        
        private boolean hasHint(String hint) {
            return hints.contains(hint);
        }
        
        private String formatSingleString(String s) {
            String escaped = StringUtils.escape(s);
            
            if (hints.contains("singlequote")) {
                return "'" + escaped + "'";
            } else if (hints.contains("doublequote")) {
                return "\"" + escaped + "\"";
            } else {
                return escaped;
            }
        }
        
        public String format(IEvalValue val) {
            if (val.getType() == ValueType.STRING || val.getType() == ValueType.LONG || val.getType() == ValueType.DATE) {
                return formatSingleString(val.getValue().toString());
            } else if (val.getType() == ValueType.MULTIVAL) {
                IMultivalue mval = (IMultivalue)val.getValue();
                
                String[] vals = mval.toArray(new String[mval.size()]);
                
                for (int i = 0; i < vals.length; i++) {
                    vals[i] = formatSingleString(vals[i]);
                }
                
                if (hints.contains("comma")) {
                    return StringUtils.join(vals, ",");
                } else if (hints.contains("semicolon")) {
                    return StringUtils.join(vals, ";");
                } else if (hints.contains("space")) {
                    return StringUtils.join(vals, " ");
                } else {
                    // These should have been quoted, so we'll sneak it in here
                    return "[\"" + StringUtils.join(vals, "\",\"") + "\"]";
                }
            }
            
            return "";
        }
    }
    
    private IDObligationExecutor CustomExecutor = new IDObligationExecutor() {
        public boolean shouldPerform(PerformObligationsEnumType type) {
            return type == PerformObligationsEnumType.ALL || type == PerformObligationsEnumType.PEP_ONLY;
        }
        
        private String wrap(String s) {
            return "\"" + s + "\"";
        }
            
        private String trimResourceName(String name) {
            if (name.startsWith("file:///")) {
                return wrap(name.substring(8));
            } else if (name.startsWith("file:")) {
                return wrap(name.substring(5));
            }
            return wrap(name);
        }

        private String convertArg(ObligationContext obligationContext, AgentCustomObligation obl, String argument) {
            StringBuilder sb = new StringBuilder();

            Matcher m = argumentPattern.matcher(argument);

            while (m.find()) {
                String replacementString = convertSingleArg(obligationContext, obl, m.group(0));

                // $ has a special meaning for appendReplacement's
                // replacement string, so we have to escape it
                replacementString = replacementString.replace("$", "\\$");
                
                m.appendReplacement(sb, replacementString);
            }
            m.appendTail(sb);

            return sb.toString();
        }

        private String convertSingleArg(ObligationContext obligationContext, AgentCustomObligation obl, String argument) {
            if (argument == null || argument.equals("") || argument.charAt(0) != '$') {
                return argument;
            }

            ObligationArgumentWithHints obligationArgument = new ObligationArgumentWithHints(argument);
            
            EvaluationResult res = obligationContext.getEvaluationResult();
            
            // Use a hash table for matching?  Probably takes longer to set up than we
            // are spending here
            if (obligationArgument.getArgument().equals("$CETimestamp")) {
                return Long.toString(res.getEvaluationRequest().getTimestamp());
            } else if (obligationArgument.getArgument().equals("$CEUser")) {
                return wrap(res.getEvaluationRequest().getUserName());
            } else if (obligationArgument.getArgument().equals("$CEComputer")) {
                return wrap(res.getPAInfo().getHostName());
            } else if (obligationArgument.getArgument().equals("$CEApplication")) {
                return wrap((String)(res.getEvaluationRequest().getApplication().getAttribute("name").getValue()));
            } else if (obligationArgument.getArgument().equals("$CEAction")) {
                return wrap(res.getEvaluationRequest().getAction().getName());
            } else if (obligationArgument.getArgument().equals("$CEPolicy")) {
                return wrap(obl.getPolicy().getName());
            } else if (obligationArgument.getArgument().equals("$CEIPAddress")) {
                return res.getEvaluationRequest().getHostIPAddress();
            } else if (obligationArgument.getArgument().equals("$CESource")) {
                return trimResourceName((String)res.getEvaluationRequest().getFromResource().getIdentifier());
            } else if (obligationArgument.getArgument().equals("$CERecipient") ||
                       obligationArgument.getArgument().equals("$CERecipients")) {
                String[] recipients = res.getPAInfo().getAttributesMap().get("RF").getStrings("Sent to");
                if (recipients.length == 0) {
                    return "NONE";
                } else {
                    return StringUtils.join(recipients, ";");
                }
            } else if (obligationArgument.getArgument().equals("$CEDestination")) {
                if (res.getEvaluationRequest().getToResource() == null) {
                    return "NONE";
                } else {
                    return trimResourceName((String)res.getEvaluationRequest().getToResource().getIdentifier());
                }
            } else if (obligationArgument.getArgument().equals("$CEClients") ||
                       obligationArgument.getArgument().equals("$CEClientIds")) {
                EvaluationRequest req = res.getEvaluationRequest();
                ClientInformation[] clients = req.getClientInformationManager().getClientsForUser(req.getUser().getUid());

                if (clients.length == 0) {
                    return "";
                } else {
                    Arrays.sort(clients, new Comparator<ClientInformation>() {
                        private final Collator collator = Collator.getInstance();
                        public int compare (ClientInformation clientA, ClientInformation clientB) {
                            return collator.compare(clientA.getShortName(), clientB.getShortName());
                        }
                    });
                
                    String[] clientDetails = new String[clients.length];
                        
                    boolean getShortName = obligationArgument.getArgument().equals("$CEClients");

                    for (int i = 0; i < clients.length; i++) {
                        if (getShortName) {
                            clientDetails[i] = clients[i].getShortName();
                        } else {
                            clientDetails[i] = clients[i].getIdentifier();
                        }
                    }

                    return StringUtils.join(clientDetails, ";");
                }
            } else if (obligationArgument.getArgument().equals("$CEMatchedRecipients")) {
                List<IDSubject> matchedRecipients = res.getEvaluationRequest().getSentToMatchesForPolicy(obl.getPolicy().getName());

                if (matchedRecipients == null) {
                    return "";
                } else {
                    String[] matches = new String[matchedRecipients.size()];
                    int i = 0;
                    for (IDSubject recip : matchedRecipients) {
                        matches[i++] = recip.getUniqueName();
                    }

                    return StringUtils.join(matches, ";");
                }
            } else if (obligationArgument.getArgument().startsWith("$CEClientName.")) {  // $CEClientName.blahblah
                EvaluationRequest req = res.getEvaluationRequest();
                String key = obligationArgument.getArgument().substring("$CEClientName.".length());
                Object idAttribute = req.getFromResource().getAttribute(key).getValue();
                if (idAttribute != null) {
                    Set<String> clientIdSet = new HashSet<String>();
                    if (idAttribute instanceof String) {
                        clientIdSet.add((String)idAttribute);
                    } else if (idAttribute instanceof IMultivalue) {
                        for (IEvalValue element : (IMultivalue)idAttribute) {
                            Object elementValue = element.getValue();
                            if (elementValue instanceof String) {
                                clientIdSet.add((String)elementValue);
                            }
                        }
                    }
                    List<String> clients = new ArrayList<String>();
                    int unknownCount = 0;
                    for (String clientId : clientIdSet) {
                        ClientInformation clientInfo = req.getClientInformationManager().getClient(clientId);
                        if (clientInfo != null) {
                            clients.add(clientInfo.getLongName());
                        } else {
                            unknownCount++;
                        }
                    }
                    Collections.sort(clients, Collator.getInstance());
                    if (unknownCount != 0) {
                        clients.add("Unknown client ("+unknownCount+")");
                    }
                    if (!clients.isEmpty()) {
                        return StringUtils.join(clients, ";");
                    } else {
                        return "No client";
                    }
                } else {
                    return "No client";
                }
            } else if (obligationArgument.getArgument().equals("$CESerializedLog")) {
                PolicyActivityLogEntryV5 entry = new PolicyActivityLogEntryV5(res.getPAInfo(), obl.getPolicy().getId().longValue(), getLogUid(obligationContext, obl.getPolicy().getName()));

                String ret = SerializationUtils.wrapExternalizable(entry);
                    
                if (ret == null) {
                    ret = "";
                }

                return ret;
            } else if (obligationArgument.getArgument().equals("$CELogUid")) {
                return Long.toString(getLogUid(obligationContext, obl.getPolicy().getName()));
            } else {
                // Assume the argument is someting like $ctx.attr and
                // get that value from the dynamic attributes. For
                // symmetry with PQL attributes, $from, $to, $resource
                // can specify a (completely useless) resource type
                String[] attrIdentifier = obligationArgument.getArgument().split("\\.");

                if (attrIdentifier.length < 2 || attrIdentifier.length > 3) {
                    return argument;
                }

                String dimension = attrIdentifier[0];

                // But $user.twodots.attribute is not allowed...
                if (attrIdentifier.length == 3 && !(dimension.equals("$from") || dimension.equals("$to") || dimension.equals("$resource"))) {
                    return argument;
                }
                
                String attr = attrIdentifier[attrIdentifier.length-1];

                IEvaluationRequest req = res.getEvaluationRequest();

                IEvalValue v = EvalValue.NULL;

                if (dimension.equals("$environment")) {
                    v = req.getEnvironment().get(attr);
                } else if (dimension.equals("$from") || dimension.equals("$resource")) {
                    v = req.getFromResource().getAttribute(attr);
                } else if (dimension.equals("$to")) {
                    IResource to = req.getToResource();
                    if (to != null) {
                        v = to.getAttribute(attr);
                    }
                } else if (dimension.equals("$user")) {
                    v = req.getUser().getAttribute(attr);
                } else if (dimension.equals("$application")) {
                    v = req.getApplication().getAttribute(attr);
                } else {
                    return argument;
                }

                if (v == null || v.getType() == ValueType.NULL) {
                    return "";
                }
                
                return obligationArgument.format(v);
            }
        }

        // Expand strings "$CE..." in obligation arguments
        // invocationString was added to arguments to support old obligation argument format 
        public List<String> convertArgs(ObligationContext obligationContext, AgentCustomObligation obl, List <? extends Object> args, String invocationString) {
            EvaluationResult res = obligationContext.getEvaluationResult();
            List<String> newArgs = new ArrayList<String>();

            // If the obligation is one of five obligations that expect old format, 
            // (value, value, ...) instead of (label, value, label, value ...)
            // Skip labels by ignoring every other argument
            // The definition of the obligation names was taken from Enforcers_Tuscany/platforms/win32/modules/msoPEP/stdafx.h
            boolean skipLabel = false;
            if (invocationString.equals("PREPEND_BODY") ||
                invocationString.equals("APEND_BODY") ||
                invocationString.equals("PREPEND_SUBJECT") ||
                invocationString.equals("TAG_MESSAGE") ||
                invocationString.equals("STORE_ATTACHMENTS"))
                skipLabel = true;
            int i = 0;
            for (Object o : args) {
                i++;
                if (skipLabel == true && (i % 2 != 0)) {
                    // skip this argument
                    continue;
                }
                newArgs.add(convertArg(obligationContext, obl, (String)o));
            }

            return newArgs;
        }

        private String joinToString(List<? extends Object> args) {
            StringBuilder buffer = new StringBuilder("");
            for (Object obj : args) {
                buffer.append(" ");
                buffer.append(obj);
            }

            return buffer.toString();
        }

        public void preamble(ObligationContext obligationContext, IDObligation obl) {
            AgentCustomObligation realObl = (AgentCustomObligation)obl;
            String name = realObl.getCustomObligationName();
            CustomObligationsContainer obligationsContainer = controlManager.getCustomObligationsContainer();

            obligationContext.addDescription(realObl, obligationsContainer.getInvocationString(name) + " " + joinToString(realObl.getCustomObligationArgs()));
        }

        public void execute(ObligationContext obligationContext, IDObligation obl) {
            AgentCustomObligation realObl = (AgentCustomObligation)obl;

            final String name = realObl.getCustomObligationName();

            final CustomObligationsContainer obligationsContainer = controlManager.getCustomObligationsContainer();

            if (obligationsContainer.obligationExists(name)) {
                String runLocation = obligationsContainer.getRunLocation(name);
                final String invocationString = obligationsContainer.getInvocationString(name);
                // invocationString was added to convertArgs() arguments to support old obligation argument format 
                final List<String> args = convertArgs(obligationContext, realObl, realObl.getCustomObligationArgs(), invocationString);

                if (runLocation.equals(CustomObligationsContainer.PDP_LOCATION)) {
                    asyncTaskQ.add(new Runnable() {
                        @Override
                        public void run() {
                            // Executable obligation
                            String cmd = invocationString + " " + joinToString(args);;
                                
                            try {
                                String runBy = obligationsContainer.getRunBy(name);
                                    
                                getLog().trace("Custom Obligation: " + cmd);
                                    
                                if (runBy == null || runBy.equals("System")) {
                                    Runtime.getRuntime().exec(cmd);
                                } else {
                                    controlManager.executeCustomObligations(obligationContext.getEvaluationResult().getEvaluationRequest().getLoggedInUser().getUid(), cmd);
                                }
                            } catch (IOException e) {
                                //....
                            }
                                
                        }
                    });          
                } else {
                    // If it's not PDP, assume PEP (crossing fingers)
                    // Delegated obligation
                    obligationContext.getPolicyEvaluationResult().addObligation(new ObligationResultData(invocationString, realObl.getPolicy().getName(), args));
                }
            } else {
                // Likewise, if we've never heard of the obligation, assume it's a PEP obligation and send it off
                getLog().info("Unable to find obligation information for " + name + ". Assuming PEP obligation");

                // We have to use the given name as the invocation string, because we have nothing else
                String invocationString = name;
                final List<String> args = convertArgs(obligationContext, realObl, realObl.getCustomObligationArgs(), invocationString);
                obligationContext.getPolicyEvaluationResult().addObligation(new ObligationResultData(invocationString, realObl.getPolicy().getName(), args));
            }
            return;
        }
    };

    private IDObligationExecutor LogExecutor = new IDObligationExecutor() {
        public boolean shouldPerform(PerformObligationsEnumType type) {
            return type == PerformObligationsEnumType.ALL;
        }
        
        public void preamble(ObligationContext obligationContext, IDObligation obl) {
            obligationContext.addDescription((AgentLogObligation)obl, "LOG");
        }
        
        public void execute(ObligationContext obligationContext, IDObligation obl) {

            if (!activityJournal.isEnabled()) {
                getLog().warn("Unable to create PolicyActivityLog as ActivityJournal is not initialized (has the agent registered?)");
                return;
            }
            
            asyncTaskQ.add(new Runnable() {
                @Override
                public void run() {
                    AgentLogObligation actualObl = (AgentLogObligation) obl;
                    
                    DynamicAttributes oblDesc = obligationContext.getDescription(actualObl);
                    
                    PolicyActivityInfoV5 paInfo = obligationContext.getEvaluationResult().getPAInfo();
                    
                    if (oblDesc != null) {
                        paInfo = new PolicyActivityInfoV5(paInfo);
                        paInfo.addAttributes(PolicyActivityInfoV5.FROM_RESOURCE_ATTRIBUTES_TAG, oblDesc);
                    }

                    Long policyId = actualObl.getPolicy().getId();

                    if (policyId == null) {
                        policyId = UNKNOWN_POLICY_ID;
                    }
                    
                    PolicyActivityLogEntryV5 entry = new PolicyActivityLogEntryV5(paInfo,
                                                                                  policyId,
                                                                                  actualObl.getPolicy().getTags(),
                                                                                  getLogUid(obligationContext, actualObl.getPolicy().getName()));
                    
                    actualObl.getExecutor().logActivity(entry);
                }
            });
        }
    };

    public void executeObligationPreamble(ObligationContext obligationContext, IDObligation obl) {
        IDObligationExecutor oblExec = obligationMap.get(obl.getType());

        if (oblExec == null) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Can't find obligation type " + obl.getType() + " in map.");
            }
        } else {
            if (oblExec.shouldPerform(obligationContext.getPerformObligations())) {
                oblExec.preamble(obligationContext, obl);
            }
        }
    }
    

    public void executeObligation(ObligationContext obligationContext, IDObligation obl) {
        IDObligationExecutor oblExec = obligationMap.get(obl.getType());

        if (oblExec == null) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Can't find obligation type " + obl.getType() + " in map.");
            }
        } else {
            if (oblExec.shouldPerform(obligationContext.getPerformObligations())) {
                oblExec.execute(obligationContext, obl);
            }
        }
    }

    @Override
    public void executeObligations(ObligationContext obligationContext) {
        for (IDObligation obl : obligationContext.getObligations()) {
            executeObligationPreamble(obligationContext, obl);
        }
        for (IDObligation obl : obligationContext.getObligations()) {
            executeObligation(obligationContext, obl);
        }
    }

    private LogIdGenerator getLogIdGenerator() {
        if (activityJournal.isEnabled()) {
            return activityJournal.getLogIdGenerator();
        }

        return null;
    }
    
    public Long getLogUid(ObligationContext obligationContext, String policyName) {
        Long uid = obligationContext.getLogUid(policyName);

        if (uid == null) {
            if (getLogIdGenerator() != null) {
                uid = getLogIdGenerator().getNextId();
                obligationContext.setLogUid(policyName, uid);
            } else {
                uid = -1L;
            }
        }

        return uid;
    }

    public void addTask(Runnable task) {
        asyncTaskQ.add(task);
    }
    
    /**
     * @see com.bluejungle.framework.comp.IConfigurable#getConfiguration()
     */
    @Override
    public IConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#setConfiguration(com.bluejungle.framework.comp.IConfiguration)
     */
    @Override
    public void setConfiguration(IConfiguration config) {
        this.configuration = config;
    }
    
    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(org.apache.commons.logging.Log)
     */
    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    @Override
    public Log getLog() {
        return log;
    }

    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    @Override
    public void init() {
        activityJournal = getConfiguration().get(ACTIVITY_JOURNAL_NAME);
        controlManager = getConfiguration().get(CONTROL_MANAGER_NAME);
        
        obligationMap = new HashMap<String, IDObligationExecutor>();
        obligationMap.put(AgentCustomObligation.OBLIGATION_NAME, CustomExecutor);
        obligationMap.put(AgentDisplayObligation.OBLIGATION_NAME, DisplayExecutor);
        obligationMap.put(AgentLogObligation.OBLIGATION_NAME, LogExecutor);
        obligationMap.put(AgentNotifyObligation.OBLIGATION_NAME, NotifyExecutor);
        
        // This sets up the mechanism for running the custom obligation tasks on
        //  a separate thread
        asyncTaskQ = new LinkedBlockingQueue<Runnable>();
        asyncTaskExecutor = new AsyncTaskExecutor(asyncTaskQ);
        asyncTaskExecutorThread = new Thread(asyncTaskExecutor, "ObligationHandler.AsyncTaskExecutor");
        asyncTaskExecutorThread.start();

    }

    @Override
    public void dispose() {
        shuttingDown = true;
        asyncTaskExecutorThread.interrupt();
    }
}
