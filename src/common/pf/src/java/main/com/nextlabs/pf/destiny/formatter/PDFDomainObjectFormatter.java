/*
 * Created on Mar 18, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.pf.destiny.formatter;

import com.bluejungle.framework.domain.IHasId;
import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.IAttribute;
import com.bluejungle.framework.expressions.ICompositePredicate;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IExpressionReference;
import com.bluejungle.framework.expressions.IExpressionVisitor;
import com.bluejungle.framework.expressions.IFunctionApplication;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.framework.expressions.IPredicateVisitor;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.Predicates;
import com.bluejungle.framework.expressions.Predicates.DefaultTransformer;
import com.bluejungle.framework.expressions.Predicates.ITransformer;
import com.bluejungle.framework.expressions.Relation;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.framework.utils.IPair;
import com.bluejungle.framework.utils.StringUtils;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.bluejungle.pf.domain.destiny.common.IDSpecRef;
import com.bluejungle.pf.domain.destiny.environment.HeartbeatAttribute;
import com.bluejungle.pf.domain.destiny.environment.RemoteAccessAttribute;
import com.bluejungle.pf.domain.destiny.environment.TimeAttribute;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.misc.IDEffectType;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.subject.IDSubjectAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyExceptions;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;
import com.bluejungle.pf.domain.epicenter.misc.IEffectType;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.bluejungle.pf.domain.epicenter.misc.ITarget;
import com.nextlabs.framework.expressions.PartialDateTime;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

/**
 * Formatter for PDF
 *
 * This takes domain objects (usually policies) and converts them into
 * pdf files represented as bytes
 *
 * The primary concern is coherently representing policies that are
 * written using the console. The PQL language is fairly flexible and
 * many of the constraints that we think of as normal are simply there
 * because of the authoring tools.
 *
 * One example can be seen in the date/time formatting. The PQL
 * language permits comparisons against dates in the month (e.g. on
 * the 15th), but the authoring tool does not support this. The PQL
 * language permits specifying a different time zone for each time
 * based expression, but the authoring tool (sensibly) does not.
 */
public class PDFDomainObjectFormatter extends PdfPageEventHelper {
    private static int TOP_MARGIN = 40;
    private static int BOTTOM_MARGIN = 40;
    private static int LEFT_MARGIN = 40;
    private static int RIGHT_MARGIN = 40;
    private static int LARGE_FONT_SIZE = 16;
    private static int DEFAULT_FONT_SIZE = 12;
    
    private static Color BLACK = new Color(0, 0, 0);
    
    private final ByteArrayOutputStream outputStream;
    private final Document document;
    private final PdfWriter writer;
    private final BaseFont footerFont;

    // Mappings from component ids to names and to subject types (obviously the latter
    // is only applicable to subject components)
    private Map<Long, String> componentNameMap = new HashMap<>();
    private Map<String, Set<SubjectType>> componentTypeMap = new HashMap<>();

    // This does nothing to the predicate *except* strip out the TRUE and TRUE
    // fluff that we use to structure the predicates built by console
    private static final ITransformer DEFAULT_TRANSFORMER = new DefaultTransformer();
    
    /**
     * Default constructor
     */

    public PDFDomainObjectFormatter() throws IOException {
        footerFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        
        document = new Document(PageSize.LETTER, LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);

        outputStream = new ByteArrayOutputStream();
        writer = PdfWriter.getInstance(document, outputStream);
        
        writer.setPageEvent(this);
    }

    public byte[] getPDF() {
        return outputStream.toByteArray();
    }
    
    public void formatDef(IHasId obj) {
        formatDef(Collections.singletonList(obj));
    }

    public void formatDef(Collection<? extends IHasId> objects) {
        List<IDPolicy> policies = new ArrayList<>();
        List<IDPolicy> subPolicies = new ArrayList<>();
        List<IDSpec> components = new ArrayList<>();

        
        for (IHasId id : objects) {
            if (id instanceof IDPolicy) {
                IDPolicy policy = (IDPolicy)id;
                
                if (isSubPolicy(policy)) {
                    subPolicies.add(policy);
                } else {
                    policies.add(policy);
                }
            } else {
                IDSpec spec = (IDSpec)id;

                // Components can be referenced either by name or by id in a policy, but
                // we want the PDF to use names always. Hence, the mapping
                componentNameMap.put(spec.getId(), spec.getName());

                // Helps us find components of a particular type
                SubjectComponentIdentifier visitor = new SubjectComponentIdentifier();
                spec.accept(visitor, IPredicateVisitor.PREORDER);

                // Generally each component will be of just one type, but it's barely possible that a component could have
                // several, so let's be prepared.
                componentTypeMap.put(spec.getName(), visitor.getSubjectTypes());
                
                components.add(spec);
            }
        }

        document.open();

        boolean first = true;
        for (IDPolicy policy : policies) {
            if (!first) {
                addLine();
            } else {
                first = false;
            }
            formatPolicy(policy);
        }
        
        for (IDPolicy policy : subPolicies) {
            addLine();
            formatPolicy(policy);
        }

        for (IDSpec component : components) {
            addLine();
            formatSpec(component);
        }
        
        document.close();
    }

    private void formatSpec(IDSpec spec) {
        addSection("Component - " + spec.getName());
        addUnindentedDetail("Description", spec.getDescription());

        spec.setPredicate(Predicates.transform(spec, DEFAULT_TRANSFORMER));
        PredicateVisitor visitor = new PredicateVisitor(document);
        spec.accept(visitor, IPredicateVisitor.PREPOSTORDER);
        visitor.close();
    }
    
    private void formatPolicy(IDPolicy policy) {
        formatPolicyMetadata(policy);

        formatPolicyEffect((IDEffectType)policy.getMainEffect());

        formatPolicyTarget(policy.getTarget());

        formatPolicyExceptions(policy.getPolicyExceptions());
        
        formatPolicyConditions(policy.getConditions());

        formatPolicyObligations(policy, EffectType.ALLOW);
        
        formatPolicyObligations(policy, EffectType.DENY);

        // These never exist, but are allowed in the language
        formatPolicyObligations(policy, EffectType.DONT_CARE);

        formatPolicyTags(policy.getTags());
        
    }

    private void formatPolicyMetadata(IDPolicy policy) {
        addSection("Policy - " + policy.getName());

        if (policy.getDescription() != null) {
            addUnindentedDetail("Description", policy.getDescription());
        }
    }

    private void formatPolicyTags(Collection<IPair<String, String>> tags) {
        ArrayList<String> tagList = new ArrayList<>();

        // The console Policy Studio uses tags in a wierd way - the key and value are the same.
        // So, to indicate tag "foo" it has foo=foo. Rather than show this mess, we'll just take the
        // value
        for (IPair<String, String> tag : tags) {
            tagList.add(tag.second());
        }

        if (!tagList.isEmpty()) {
            addSection("Tags");
            addDetail(StringUtils.join(tagList, ", "));
        }
    }
    
    private void formatPolicyEffect(IDEffectType effect) {
        addSection(effect.toString().toUpperCase());
    }

    private void formatPolicyTarget(ITarget target) {
        // Actions
        IPredicate actions = target.getActionPred();

        if (actions != null && !isAlwaysTrue(actions)) {
            actions = Predicates.transform(actions, DEFAULT_TRANSFORMER);
            
            PredicateVisitor visitor = new PredicateVisitor(document);
            addSection("When Performing Actions");
            actions.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }
        
        IPredicate subject = target.getSubjectPred();
        
        // Extract users
        IPredicate users = Predicates.transform(subject, new SubjectTransformer(SubjectType.USER));
        if (users != null && !isAlwaysTrue(users)) {
            users = Predicates.transform(users, DEFAULT_TRANSFORMER);
            
            PredicateVisitor visitor = new PredicateVisitor(document);
            addSection("By Users");
            users.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }

        // Extract hosts
        IPredicate hosts = Predicates.transform(subject, new SubjectTransformer(SubjectType.HOST));
        if (hosts != null && !isAlwaysTrue(hosts)) {
            hosts = Predicates.transform(hosts, DEFAULT_TRANSFORMER);
            
            PredicateVisitor visitor = new PredicateVisitor(document);
            addSection("On Hosts");
            hosts.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }
        
        // Extract applications
        IPredicate applications = Predicates.transform(subject, new SubjectTransformer(SubjectType.APP));
        if (applications != null && !isAlwaysTrue(applications)) {
            applications = Predicates.transform(applications, DEFAULT_TRANSFORMER);
            
            PredicateVisitor visitor = new PredicateVisitor(document);
            addSection("With Applications");
            applications.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }

        // From Resource
        IPredicate fromResource = target.getFromResourcePred();
        if (fromResource != null && !isAlwaysTrue(fromResource)) {
            fromResource = Predicates.transform(fromResource, DEFAULT_TRANSFORMER);
            
            PredicateVisitor visitor = new PredicateVisitor(document);
            addSection("On Resource(s)");
            fromResource.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }

        // To Resource
        IPredicate toResource = target.getToResourcePred();
        if (toResource != null && !isAlwaysTrue(toResource)) {
            toResource = Predicates.transform(toResource, DEFAULT_TRANSFORMER);
            
            PredicateVisitor visitor = new PredicateVisitor(document);
            addSection("To Resource");
            toResource.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }
    }

    private void formatPolicyExceptions(IPolicyExceptions policyExceptions) {
        if (policyExceptions == null || policyExceptions.getPolicies().isEmpty()) {
            return;
        }

        addSection("Subpolicies");

        for (IPolicyReference reference : policyExceptions.getPolicies()) {
            addDetail(reference.getReferencedName());
        }
    }
    
    private void formatPolicyConditions(IPredicate conditions) {
        if (conditions == null) {
            return;
        }
        
        addSection("When");

        // Time based conditions

        ConditionsPredicateVisitor conditionsVisitor = new ConditionsPredicateVisitor();
        conditions.accept(conditionsVisitor, IPredicateVisitor.PREPOSTORDER);

        if (conditionsVisitor.getStartDateTime() != null && conditionsVisitor.getEndDateTime() != null) {
            addDetail("Active", conditionsVisitor.getStartDateTime() + " - " + conditionsVisitor.getEndDateTime());
        }

        if (conditionsVisitor.getStartTime() != null && conditionsVisitor.getEndTime() != null) {
            addDetail("Active time", conditionsVisitor.getStartTime() + " through " + conditionsVisitor.getEndTime());
        }

        if (!conditionsVisitor.getDaysOfWeek().isEmpty()) {
            addDetail("On days", StringUtils.join(conditionsVisitor.getDaysOfWeek(), ", "));
        }

        if (conditionsVisitor.getTimeZone() != null) {
            addDetail("Time zone", conditionsVisitor.getTimeZone());
        }
        
        // Access based condition
        if (conditionsVisitor.getRemoteAccess() != null) {
            if (conditionsVisitor.getRemoteAccess() == 0) {
                addDetail("Accessing locally");
            } else if (conditionsVisitor.getRemoteAccess() == 1) {
                addDetail("Accessing remotely");
            }
        }
            
        // HB based condition
        if (conditionsVisitor.getTimeSinceLastHeartbeat() != null && conditionsVisitor.getTimeSinceLastHeartbeat() > 0) {
            addDetail("Last heartbeat occurred less than " +
                      conditionsVisitor.getTimeSinceLastHeartbeat() +
                      " seconds ago");
        }
        
        // Freeform condition

        // Gross. Delete all the active from/to, access based, and hb based conditions. Show the rest
        // This is not technically accurate, as one of these could appear in the free-form text box, but
        // (fingers crossed) if it does we probably handled it above (unless it appears twice).
        //
        // PolicyStudio uses a particular tree structure for the data to sort this out, but
        // I'd prefer not to use that as it could change without warning
        IPredicate freeform = Predicates.transform(conditions, new DefaultTransformer() {
            @Override
            public IPredicate transformRelation(IRelation pred) {
                IExpression lhs = pred.getLHS();
                
                // Possibly be more specific about TimeAttribute (?)
                if (lhs instanceof TimeAttribute ||
                    lhs instanceof RemoteAccessAttribute ||
                    lhs instanceof HeartbeatAttribute) {
                    return null;
                }
                
                return pred;
            }
        });

        if (freeform != null && !isAlwaysTrue(freeform)) {
            addSection("Additional Conditions");
            PredicateVisitor visitor = new PredicateVisitor(document);
            freeform.accept(visitor, IPredicateVisitor.PREPOSTORDER);
            visitor.close();
        }
    }

    private void formatPolicyObligations(IDPolicy policy, IEffectType effect) {
        Collection<IObligation> obligations = policy.getObligations(effect);

        if (obligations == null || obligations.isEmpty()) {
            return;
        }

        addSection("Obligations on " + effect);

        for (IObligation obligation : obligations) {
            if (obligation instanceof IDObligation) {
                addDetail(((IDObligation)obligation).toPQL());
            } else {
                addDetail(obligation.getType());
            }
        }
    }
    
    private static final Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA, LARGE_FONT_SIZE, Font.BOLD, BLACK);
    
    private void addSection(String sectionName) {
        Paragraph p = new Paragraph(sectionName, SECTION_FONT);
        document.add(p);
    }

    private static final Font DETAIL_TAG_FONT = FontFactory.getFont(FontFactory.HELVETICA, DEFAULT_FONT_SIZE, Font.BOLD, BLACK);
    private static final Font DETAIL_FONT = FontFactory.getFont(FontFactory.HELVETICA, DEFAULT_FONT_SIZE, Font.NORMAL, BLACK);
    private static final float DETAIL_INDENTATION = 50.0f;
    
    private void addDetail(String detail) {
        if (detail == null || detail.length() == 0) {
            return;
        }
        Paragraph p = new Paragraph(detail, DETAIL_FONT);
        p.setIndentationLeft(DETAIL_INDENTATION);
        document.add(p);
    }

    private void addDetail(String tag, String detail) {
        addDetail(tag, detail, DETAIL_INDENTATION);
    }

    private void addUnindentedDetail(String tag, String detail) {
        addDetail(tag, detail, 0.0f);
    }
    
    private void addDetail(String tag, String detail, float indentation) {
        if (detail == null || detail.length() == 0) {
            return;
        }
        
        Paragraph p = new Paragraph(tag + ": " , DETAIL_FONT);
        p.setIndentationLeft(indentation);
        Chunk c = new Chunk(detail, DETAIL_FONT);
        p.add(c);
        document.add(p);
    }

    private static final float LINE_WIDTH = 2.0f;
    private static final Color LINE_COLOR = Color.GRAY;
    
    private void addLine() {
        Paragraph pre = new Paragraph();
        pre.setSpacingAfter(20.0f);
        document.add(pre);
        document.add(new LineSeparator(LINE_WIDTH, 100.0f, LINE_COLOR, Element.ALIGN_CENTER, 0));
        Paragraph post = new Paragraph();
        post.setSpacingBefore(10.0f);
        document.add(post);
    }
    
    private static final float FOOTER_FONT_SIZE = DEFAULT_FONT_SIZE;

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        float footerBase = document.bottom() - (FOOTER_FONT_SIZE * 1.5f);
        
        DateFormat format = DateFormat.getDateInstance();
        String text = "Generated: " + format.format(new Date());
        
        cb.beginText();
        
        cb.setFontAndSize(footerFont, FOOTER_FONT_SIZE);
        cb.setTextMatrix(document.left(), footerBase);
        cb.showText(text);

        text = "Page " + writer.getPageNumber();
        float textSize = footerFont.getWidthPoint(text, FOOTER_FONT_SIZE);
        
        cb.setFontAndSize(footerFont, FOOTER_FONT_SIZE);
        float tweak = footerFont.getWidthPoint("0", FOOTER_FONT_SIZE);
        cb.setTextMatrix(document.right() - textSize - tweak, footerBase);
        cb.showText(text);
        cb.endText();
    }
    
    private boolean isSubPolicy(IDPolicy p) {
        return p.hasAttribute(IDPolicy.EXCEPTION_ATTRIBUTE);
    }

    private boolean isAlwaysTrue(IPredicate pred) {
        // The only thing that matches "null" is the TRUE predicate
        try {
            return pred.match(null);
        } catch (Exception e) {
            // This error might just indicate a lack of evaluation
            // context, so it's probably not serious. We just care if
            // the predicate is equivalent to "null" and this error
            // indicates that there is *something* there
            return false;
        }
    }

    private static class ConditionsPredicateVisitor extends DefaultPredicateVisitor {
        private static DateTimeFormatter ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm:ss a");

        // All these date/time values will be stored without a time zone. Technically there
        // is no reason why they can't all have one but (a) it makes for a messy looking page
        // and (b) our authoring tool lets you apply one time zone to everything, so you'd have
        // to be writing the policy by hand
        private String startDateTime = null;
        private String startTime = null;
        private String endDateTime = null;
        private String endTime = null;
        private ZoneId timeZone;
        private List<String> daysOfWeek = new ArrayList<>();
        private Long remoteAccess = null;
        private Long timeSinceLastHeartbeat = null;

        public String getStartDateTime() {
            return startDateTime;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndDateTime() {
            return endDateTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public List<String> getDaysOfWeek(){
            return daysOfWeek;
        }
        
        public String getTimeZone() {
            if (timeZone == null) {
                return null;
            }
            
            return timeZone.toString();
        }

        public Long getRemoteAccess() {
            return remoteAccess;
        }

        public Long getTimeSinceLastHeartbeat() {
            return timeSinceLastHeartbeat;
        }

        @Override
        public void visit(IRelation rel) {
            IExpression lhs = rel.getLHS();
            IExpression rhs = rel.getRHS();
            RelationOp op = rel.getOp();
            
            Object val = null;
            
            if (rhs instanceof Constant) {
                val = ((Constant)rhs).getValue().getValue();
            } else {
                return;
            }
            
            if (lhs instanceof TimeAttribute) {
                TimeAttribute timeExpression = (TimeAttribute)lhs;

                if (val instanceof PartialDateTime && timeZone == null) {
                    timeZone = ((PartialDateTime)val).getZoneId();
                }

                if (val instanceof ZonedDateTime && timeZone == null) {
                    timeZone = ((ZonedDateTime)val).getZone();
                }
                if (timeExpression == TimeAttribute.IDENTITY) {
                    
                    String formattedDateTime = (val instanceof ZonedDateTime) ?
                                               ZONED_DATE_TIME_FORMATTER.format((ZonedDateTime)val):
                                               ((PartialDateTime)val).toString();

                    if (op == RelationOp.GREATER_THAN || op == RelationOp.GREATER_THAN_EQUALS) {
                        startDateTime = formattedDateTime;
                    } else {
                        endDateTime = formattedDateTime;
                    }
                } else if (timeExpression == TimeAttribute.TIME) {
                    PartialDateTime partialTime = (PartialDateTime)val;
                    
                    if (op == RelationOp.GREATER_THAN || op == RelationOp.GREATER_THAN_EQUALS) {
                        startTime = partialTime.getValueAsString();
                    } else {
                        endTime = partialTime.getValueAsString();
                    }
                } else if (timeExpression == TimeAttribute.WEEKDAY) {
                    daysOfWeek.add(((PartialDateTime)val).getValueAsString());
                }
            } else if (lhs instanceof RemoteAccessAttribute) {
                remoteAccess = (Long)val;
            } else if (lhs instanceof HeartbeatAttribute) {
                timeSinceLastHeartbeat = (Long)val;
            }
        }
    }

    private class SubjectComponentIdentifier extends DefaultPredicateVisitor {
        private Set<SubjectType> types = new HashSet<>();

        public Set<SubjectType> getSubjectTypes() {
            return types;
        }
        
        @Override
        public void visit(IRelation rel) {
            IExpression lhs = rel.getLHS();
            IExpression rhs = rel.getRHS();

            if (lhs instanceof IDSubjectAttribute) {
                types.add(((IDSubjectAttribute)lhs).getSubjectType());
            }

            if (rhs instanceof IDSubjectAttribute) {
                types.add(((IDSubjectAttribute)rhs).getSubjectType());
            }
        }
    }
    
    private class SubjectTransformer extends DefaultTransformer {
        private SubjectType subjectType;

        public SubjectTransformer(SubjectType subjectType) {
            this.subjectType = subjectType;
        }

        @Override
        public IPredicate transformRelation(IRelation relation) {
            IExpression lhs = relation.getLHS();
            IExpression rhs = relation.getRHS();

            if ((lhs instanceof IDSubjectAttribute && ((IDSubjectAttribute)lhs).getSubjectType() == subjectType) ||
                (rhs instanceof IDSubjectAttribute && ((IDSubjectAttribute)rhs).getSubjectType() == subjectType)) {
                return relation;
            }

            return null;
            
        }

        @Override
        public IPredicate transformReference(IPredicateReference pred) {
            if (pred instanceof IDSpecRef) {
                IDSpecRef ref = (IDSpecRef)pred;
                     
                String name;
                if (ref.isReferenceByName()) {
                    name = ref.getReferencedName();
                } else {
                    name = componentNameMap.get(ref.getReferencedID());
                }

                if (name == null) {
                    return null;
                }
                
                Set<SubjectType> types = componentTypeMap.get(name);

                if (types != null && types.contains(subjectType)) {
                    return pred;
                }
            }

            return null;
        }
    }
    
    /**
     * A lot of this is taken from DomainObjectFormatter.PredicateVisitor. The main
     * difference being that this one builds a string rather than calling "put"
     */
    private static final Font OPERATORS_FONT = FontFactory.getFont(FontFactory.HELVETICA, DEFAULT_FONT_SIZE, Font.ITALIC, BLACK);
    private static final Font NAMES_FONT = FontFactory.getFont(FontFactory.HELVETICA, DEFAULT_FONT_SIZE, Font.BOLD, BLACK);
    private static final Font PARENS_FONT = FontFactory.getFont(FontFactory.HELVETICA, DEFAULT_FONT_SIZE, Font.NORMAL, BLACK);
    
    private class PredicateVisitor implements IPredicateVisitor {
        private final Stack<String> opStack = new Stack<String>();
        private final Stack<Boolean> separatorStack = new Stack<Boolean>();
        private Paragraph paragraph;
        private Document document;
        
        PredicateVisitor(Document document) {
            this.document = document;
            this.paragraph = new Paragraph();
            this.paragraph.setIndentationLeft(DETAIL_INDENTATION);
        }

        public void close() {
            document.add(paragraph);
        }
        
        /**
         * Formats a composite predicate.
         *
         * @param pred the composite predicate to format.
         * @param preorder true when it's a pre-order visit call, false otherwise.
         */
        public void visit(ICompositePredicate pred, boolean preorder) {
            visit(pred, pred.getOp().toString(), preorder);
        }

        public void visit(ICompositePredicate pred, String opStr, boolean preorder ) {
            if ( preorder ) {
                addInfixOps();
                if ( pred.predicateCount() == 1 ) {
                    // If there's only one predicate then this is a prefix op. Must be NOT...
                    Chunk chunk = new Chunk(opStr, OPERATORS_FONT);
                    paragraph.add(chunk);
                    chunk = new Chunk(" (", PARENS_FONT);
                    paragraph.add(chunk);
                } else {
                    Chunk chunk = new Chunk("(", PARENS_FONT);
                    paragraph.add(chunk);
                }
                opStack.push(opStr);
                separatorStack.push(true);
            } else {
                Chunk chunk = new Chunk(")", PARENS_FONT);
                paragraph.add(chunk);
                opStack.pop();
                separatorStack.pop();
            }
        }

        /**
         * Formats a spec reference.
         *
         * @param pred the predicate reference to format.
         */
        public void visit(IPredicateReference pred) {
            addInfixOps();

            IDSpecRef ref = (IDSpecRef)pred;

            String referencedName;
            
            if (ref.isReferenceByName()) {
                referencedName = ref.getReferencedName();
            } else {
                referencedName = componentNameMap.get(ref.getReferencedID());
            }

            if (referencedName == null) {
                referencedName = "Unknown Component/Entity";
            }
            
            Chunk chunk = new Chunk(referencedName, NAMES_FONT);
            paragraph.add(chunk);
        }

        /**
         * Formats a relation spec.
         *
         * @param spec the relation spec to format.
         */
        public void visit(IRelation rel) {
            addInfixOps();
            formatSingleExpression(paragraph, rel.getLHS());
            Chunk chunk = new Chunk(" " + rel.getOp() + " ", OPERATORS_FONT);
            paragraph.add(chunk);
            formatSingleExpression(paragraph, rel.getRHS());
        }

        /**
         * Formats a generic predicate.
         *
         * @param pred the generic predicate to format.
         */
        public void visit(IPredicate pred) {
            addInfixOps();

            String s = pred.toString();
            
            if (pred == PredicateConstants.TRUE) {
                s = "ALWAYS";
            } else if (pred == PredicateConstants.FALSE) {
                s = "NEVER";
            }
            
            Chunk chunk = new Chunk(s, NAMES_FONT);
            paragraph.add(chunk);
        }

        protected void formatSingleExpression(Paragraph paragraph, IExpression expr) {
            IExpressionVisitor ev = new IExpressionVisitor() {
                
                public void visit(IAttribute attr) {
                    Chunk chunk = new Chunk(" " + attr.getName() + " ", NAMES_FONT);
                    paragraph.add(chunk);
                }
                
                public void visit(Constant constant) {
                    Chunk chunk = new Chunk(" " + constant.toString() + " ", NAMES_FONT);
                    paragraph.add(chunk);
                }
                
                public void visit(IFunctionApplication func) {
                    Chunk chunk = new Chunk(" function(" + func.getServiceName() + ", " + func.getFunctionName());
                    paragraph.add(chunk);

                    for (IExpression argument : func.getArguments()) {
                        formatSingleExpression(paragraph, argument);
                    }
                }
                
                public void visit(IExpression expression) {
                    Chunk chunk = new Chunk(" " + expr.toString() + " ", NAMES_FONT);
                    paragraph.add(chunk);
                }
                
                public void visit(IExpressionReference ref) {
                    Chunk chunk = new Chunk(" " + ref.getPrintableReference() + " ", NAMES_FONT);
                    paragraph.add(chunk);
                }
                
            };
            expr.acceptVisitor(ev, IExpressionVisitor.PREORDER);
        }
     
        private void addInfixOps() {
            if ( separatorStack.isEmpty() ) {
                return;
            }
            if (!separatorStack.peek()) {
                // The two stacks grow and shrink at the same time:
                assert !opStack.empty();

                Chunk chunk = new Chunk(" " + opStack.peek() + " ", OPERATORS_FONT);
                paragraph.add(chunk);
            } else {
                separatorStack.pop();
                separatorStack.push(false);
            }
        }

        
    }
}
