package agent;

import java.util.Date;

import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import model.Demand;
import jade.domain.FIPANames;

/**
 * @author Isuru
 * Represents a single Appliance inside a single home
 */
public class ApplianceAgent extends TradeAgent implements Object2ApplianceAgentInterface { 
	
	private AID scheduler;
	private Demand startDemand; 
	
	/**
	 *  Sets up the Appliance Agent
	 *  Arguments provided, are assumed to follow the same order as defined in the ApplianceAgentDescriptor 
	 */
	protected void setup() {
		Object[] args = getArguments();
		setScheduler(new AID((String) args[0],AID.ISLOCALNAME));
		setStartDemand((Demand) args[1]);
		if( getScheduler() != null) StartDemanding();
	}
	
	/**
	 * Start the demanding behavior of the appliance based off the starting demand
	 */
	public void StartDemanding() {
		addBehaviour(new DemandingBehaviour(this));
	}
	
	/**
	 * Represents the Demanding behavior, where the agent periodically made a demand request to the scheduling agent
	 *
	 */
	private class DemandingBehaviour extends TickerBehaviour{
		
		public DemandingBehaviour (Agent a) {
			super(a, 1000);
		}
		
		@Override
		protected void onTick() {
			say("Making a demand to the scheduler DEMAND=("+ startDemand.getContent()+")");
			ACLMessage msg = startDemand.createACLMessage(ACLMessage.INFORM);
			msg.addReceiver(scheduler);
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 500));
			myAgent.addBehaviour( new AchieveREInitiator(myAgent,msg){
				protected void handleAgree(ACLMessage agree) {
					System.out.println(getLocalName() + ": " + agree.getSender().getName() + " has agreed to the request");
				}
				
				protected void handleInform(ACLMessage inform) {
					System.out.println(getLocalName() + ": " + inform.getSender().getName() + " successfully scheduled my Demand");
				}
			});
		}
	}

	public AID getScheduler() {
		// TODO Auto-generated method stub
		return scheduler;
	}
	
	public void setScheduler(AID scheduler) {
		this.scheduler = scheduler;
		
	}

	public Demand getStartDemand() {
		return startDemand;
	}

	public void setStartDemand(Demand startDemand) {
		this.startDemand = startDemand;
	}




}
