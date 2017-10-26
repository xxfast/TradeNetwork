package agent;

import java.util.Date;

import FIPA.DateTime;
import agent.HomeAgent.TimeKeepingBehavior;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import model.Demand;
import model.Time;
import simulation.Simulation;

/**
 * @author Isuru
 * Represents a single Appliance inside a single home
 */
public class ApplianceAgent extends TradeAgent implements Object2ApplianceAgentInterface { 
	
	private AID home;
	private Demand startDemand; 
	
	public ApplianceAgent() {
		this.registerO2AInterface(Object2ApplianceAgentInterface.class, this);
	}
	
	/**
	 *  Sets up the Appliance Agent
	 *  Arguments provided, are assumed to follow the same order as defined in the ApplianceAgentDescriptor 
	 */
	protected void setup() {
		super.setup();
		this.setMuted(true);
		Object[] args = getArguments();
		if( args[0] instanceof AID)
			setHome((AID)args[0]);
		else
			setHome(new AID((String)args[0],AID.ISLOCALNAME));
		if( args[1] instanceof Demand)
			setStartDemand((Demand)args[1]);
		else
			setStartDemand(new Demand(Integer.valueOf((String) args[1])));
		if( getHome() != null) 
			StartDemanding();
		addBehaviour(new TimeAskingBehaviour(this));
	}
	
	/**
	 * Start the demanding behavior of the appliance based off the starting demand
	 */
	public void StartDemanding() {
		addBehaviour(new DemandingBehaviour(this));
	}
	
	private class TimeAskingBehaviour extends TickerBehaviour{
		
		public TimeAskingBehaviour (Agent a) {
			super(a, Simulation.Time / 5);
		}
		
		@Override
		protected void onTick() {
			say("Asking my home agent for the time");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(home);
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			myAgent.addBehaviour( new AchieveREInitiator(myAgent,msg){
				protected void handleInform(ACLMessage inform) {
					Time time = new Time(inform);
					ApplianceAgent.this.say(time.toString());
				}
			});
		}

	}

	
	/**
	 * Represents the Demanding behavior, where the agent periodically made a demand request to the scheduling agent
	 *
	 */
	private class DemandingBehaviour extends TickerBehaviour{
		
		public DemandingBehaviour (Agent a) {
			super(a, Simulation.Time / 5);
		}
		
		@Override
		protected void onTick() {
			Demand myDemand = getCurrentDemand();
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

	public Demand getCurrentDemand() {
		return new Demand(1);
	}




}
