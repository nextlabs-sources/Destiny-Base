import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.LogManager;

import org.apache.openaz.pepapi.Action;
import org.apache.openaz.pepapi.Environment;
import org.apache.openaz.pepapi.Obligation;
import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepAgentFactory;
import org.apache.openaz.pepapi.PepPermissionsResponse;
import org.apache.openaz.pepapi.Resource;
import org.apache.openaz.pepapi.Subject;
import org.apache.openaz.pepapi.std.StdPepAgentFactory;
import org.apache.openaz.xacml.api.Decision;

import com.nextlabs.openaz.utils.Constants;

public class CheckPermissions {
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().reset();

		/*
		 * Setting up a pepagent, this involves establishing a secure connection
		 * with PDP
		 */
		String configPath = CheckPermissions.class.getResource("/" + System.getProperty("config.file")).toURI().getPath();
		PepAgentFactory pepAgentFactory = new StdPepAgentFactory(configPath);
		PepAgent pepAgent = pepAgentFactory.getPepAgent();

        // Queries won't work until the PDP is fully initialized. Wait a little.
        Thread.sleep(4 * 1000);

        /*
         * Building a permissions request is exactly like building a regular request,
         * except there is no Action. We'll use the same code as in AllowITDeptUsersToViewTickets
         */
        
		/*
		 * Build a Subject object with subject id(unique id for subject). This
		 * will typically be a sid(windowsSid/unixId) or email. This is matched
		 * up against the contents of the policies, is a vital part of policy
		 * evaluation, and is thus required. Additional attributes can be
		 * populated using addAttribute
		 */
		Subject user = Subject.newInstance("chris.webber@hdesk.com");
		user.addAttribute("user_id", "chris.webber");
		user.addAttribute("department", "IT");
		user.addAttribute("roles", "Support Representative");
		user.addAttribute("assigned_prod_area", "SharePoint");

		/*
		 * Build a Resource object with resource id(This will typically be the
		 * name of the resource e.g. ./foo.txt) and populate all the available
		 * resource attributes
		 */
		Resource resource = Resource.newInstance("Ticket:1103");
		resource.addAttribute(Constants.ID_RESOURCE_RESOURCE_TYPE.stringValue(), "support_tickets");
		resource.addAttribute("ticket_id", "1103");
		resource.addAttribute("priority", "P0");
		resource.addAttribute("severity", "Complete Loss of Service");
		resource.addAttribute("prod_name", "Exchange Email");
		resource.addAttribute("category", "Authentication");
		resource.addAttribute("assigned_to", "homer.simpson");
		resource.addAttribute("created_by", "barney.gumble");
		resource.addAttribute("description", "Users are unable to login into email");

		/*
		 * Build a Environment object with environment attributes.
		 */
		Environment environment = Environment.newInstance();
		environment.addAttribute("authentication_type", "multifactor");
		environment.addAttribute("nextlabs-record-matching-policies-in-result", "true");

        PepPermissionsResponse pepPermissionsResponse = pepAgent.decidePermissions(user, resource, environment);

        printPermissionsForDecision(pepPermissionsResponse, Decision.PERMIT);
        printPermissionsForDecision(pepPermissionsResponse, Decision.DENY);
        printPermissionsForDecision(pepPermissionsResponse, Decision.NOTAPPLICABLE);
    }

    private static void printPermissionsForDecision(PepPermissionsResponse pepPermissionsResponse,
                                                    Decision decision) {
        System.out.println("=====================");
        System.out.println("=== " + decision + " actions");
        System.out.println("=====================");

        for (Action action : pepPermissionsResponse.getActionsForDecision(decision)) {
            System.out.println(action.getActionIdValue());

            if (!pepPermissionsResponse.getObligations(decision, action).isEmpty()) {
                System.out.println("Obligations");

                for (Entry<String, Obligation> obligationEntry : pepPermissionsResponse.getObligations(decision, action).entrySet()) {
                    System.out.println(obligationEntry.getKey());

                    for (Entry<String, Object[]> attrEntry : obligationEntry.getValue().getAttributeMap().entrySet()) {
                        System.out.print(" " + attrEntry.getKey() + " = ");

                        boolean isFirstEntryValue = true;
                        for (Object obj : attrEntry.getValue()) {
                            if (!isFirstEntryValue){
                                System.out.print(", ");
                            }
                            isFirstEntryValue = false;
                            System.out.print("\"" + obj + "\"");
                        }
                        
                        System.out.println("");
                    }
                }
            }

            if (!pepPermissionsResponse.getPolicies(decision, action).isEmpty()) {
                System.out.println("\nPolicies used to determine this decision:");
                
                for (String policy : pepPermissionsResponse.getPolicies(decision, action)) {
                    System.out.println("   " + policy);
                }
                System.out.println("");
            }
        }
        
        System.out.print("\n\n\n");
    }
}
