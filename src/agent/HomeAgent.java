package agent;

import java.util.ArrayList;

import behavior.DiscoverBehaviour;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import model.NotificationMessage;

public class HomeAgent extends TradeAgent {

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	private ArrayList<NotificationMessage> messages = new ArrayList<>();
	
	protected void setup() {
		// Registration with the DF 
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("HomeAgent"); 
		sd.setName(getName());
		sd.setOwnership("TILAB");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this,dfd);
			NotificationRecievingBehavior PingBehaviour = new  NotificationRecievingBehavior(this);
			addBehaviour(PingBehaviour);
		} catch (FIPAException e) {
			myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
			doDelete();
		}
	}
	
	public class NotificationRecievingBehavior extends CyclicBehaviour{

		public NotificationRecievingBehavior(Agent a){
			super(a);
		}
		
		@Override
		public void action() {
			ACLMessage  msg = myAgent.receive();
			if(msg != null){
				ACLMessage reply = msg.createReply();
				if(msg.getPerformative()== ACLMessage.INFORM){
					String content = msg.getContent();
					if ((content != null)){
						NotificationMessage recieved = new NotificationMessage(msg);
						messages.add(recieved);
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Received Notification from "+msg.getSender().getLocalName());
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Recieved");
					}
					else{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Unexpected request ["+content+"] received from "+msg.getSender().getLocalName());
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("( UnexpectedContent ("+content+"))");
					}

				}
				else {
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Unexpected message ["+ACLMessage.getPerformative(msg.getPerformative())+"] received from "+msg.getSender().getLocalName());
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("( (Unexpected-act "+ACLMessage.getPerformative(msg.getPerformative())+") )");   
				}
				send(reply);
			}
			else {
				block();
			}
		}
	}
	
}
