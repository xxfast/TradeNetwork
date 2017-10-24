package agent;

import java.util.Date;

import FIPA.DateTime;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import model.Demand;
import simulation.Simulation;

/**
 * @author Isuru
 * Represents a single Appliance inside a single home
 */
public class ApplianceAgent extends TradeAgent implements Object2ApplianceAgentInterface { 
	
	private AID home;
	private Demand startDemand; 
	
	
	
	/**
	 *  Sets up the Appliance Agent
	 *  Arguments provided, are assumed to follow the same order as defined in the ApplianceAgentDescriptor 
	 */
	protected void setup() {
		super.setup();
		setMuted(true);
		Object[] args = getArguments();
		setHome((AID)args[0]);
		setStartDemand((Demand) args[1]);
		if( getHome() != null) 
			StartDemanding();
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
			super(a, Simulation.Time);
		}
		
		@Override
		protected void onTick() {
			Demand myDemand = new Demand(1);
			say("Making a demand to the scheduler DEMAND=("+ myDemand.getContent()+")");
			ACLMessage msg = myDemand.createACLMessage(ACLMessage.INFORM);
			msg.addReceiver(home);
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 500));
			myAgent.addBehaviour( new AchieveREInitiator(myAgent,msg){
				protected void handleAgree(ACLMessage agree) {
					say(agree.getSender().getName() + " has agreed to the request");
				}
				
				protected void handleInform(ACLMessage inform) {
					say(inform.getSender().getName() + " successfully scheduled my Demand");
				}
			});
		}
	}

	public AID getHome() {
		return home;
	}
	
	public void setHome(AID home) {
		this.home = home;
	}

	public Demand getStartDemand() {
		return startDemand;
	}

	public void setStartDemand(Demand startDemand) {
		this.startDemand = startDemand;
	}




}
