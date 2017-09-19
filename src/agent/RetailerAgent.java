package agent;

// Used to make the agent a Service Provider Agent
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;

// Used to make the agent a ContractNetResponder Agent
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

// Used to log exceptions
import jade.util.Logger;

public class RetailerAgent extends TradeAgent {

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		// Describes the agent as a retail agent
		setupServiceProviderComponent();
		
		// Add the ContractNetResponder behaviour
		
		
		
	}
	
	private void setupServiceProviderComponent () {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription(); 
		
		// Maybe also add another service description of electricity buyer for when we implement agents buying power
		
		// Create service description
		sd.setType("RetailerAgent"); 
		sd.setName(getName());
		sd.setOwnership("TradeNetwork");
		
		// Add service to agent description
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		// Try to register the agent with its description
		try {
			DFService.register(this,dfd);	
		} catch (FIPAException e) {
			myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
			doDelete();
		}
	}
	
	
	
	
	
}
