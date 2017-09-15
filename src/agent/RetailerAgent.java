package agent;

import agent.HomeAgent.NotificationRecievingBehavior;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class RetailerAgent extends TradeAgent {

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		
		// Registration with the DF 
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("RetailerAgent"); 
		sd.setName(getName());
		sd.setOwnership("TradeNetwork");
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		try {
			DFService.register(this,dfd);
			
			// Does this need to be static in the home agent for this agent to use this?
			//NotificationRecievingBehavior notificationRecievingBehavior = new NotificationRecievingBehavior(this);
			//addBehaviour(notificationRecievingBehavior);
			
		} catch (FIPAException e) {
			myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
			doDelete();
		}
	}
	
	
}
