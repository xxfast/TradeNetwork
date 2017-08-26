package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ApplianceAgent extends Agent { 

	private Agent homeAgent;
	
	private class NotifyingBehavior extends OneShotBehaviour{
		
		@Override
		public void action() {
			ACLMessage notificationMessage = new ACLMessage( ACLMessage.INFORM );
			notificationMessage.setContent("");
			notificationMessage.addReceiver( new AID(homeAgent.getAID().getName(), AID.ISLOCALNAME ) );
            send( notificationMessage );
		}
		
	}

	public Agent getHomeAgent() {
		return homeAgent;
	}

	public void setHomeAgent(Agent homeAgent) {
		this.homeAgent = homeAgent;
	}

}
