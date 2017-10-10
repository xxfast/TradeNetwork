package agent;

import java.util.Date;

import FIPA.DateTime;
import annotations.Creatable;
import annotations.Customizable;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import model.Demand;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@Creatable
public class ApplianceAgent extends TradeAgent implements Object2ApplianceAgentInterface { 

	@Customizable(label="Name of the Scheduler Agent")
	private AID schedulerAgent;
	
	@Customizable(label="Starting demand")
	private Demand startDemand = new Demand(1, new DateTime()); ;
	
	protected void setup() {
		Object[] args = getArguments();
		schedulerAgent = new AID((String) args[0],AID.ISLOCALNAME);
		say("My SchedulingAgent is =("+ schedulerAgent.getName()+")");
		addBehaviour(new DemandingBehaviour(this));
	}
	
	private class DemandingBehaviour extends TickerBehaviour{
		
		public DemandingBehaviour (Agent a) {
			super(a, 1000);
		}
		
		@Override
		protected void onTick() {
			say("Making a demand to the scheduler DEMAND=("+ startDemand.getContent()+")");
			ACLMessage msg = startDemand.createACLMessage(ACLMessage.INFORM);
			msg.addReceiver(schedulerAgent);
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

	@Override
	public AID getSchedulerAgent() {
		// TODO Auto-generated method stub
		return schedulerAgent;
	}


	@Override
	public void setSchedulerAgen(AID schedulerAgent) {
		this.schedulerAgent = schedulerAgent;
		
	}




}
