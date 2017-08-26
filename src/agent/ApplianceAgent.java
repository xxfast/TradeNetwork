package agent;

import behavior.DiscoverBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ApplianceAgent extends Agent { 

	private AID homeAgent;
	
	protected void setup() {
		DiscoverBehaviour db = new DiscoverBehaviour();
		db.setToDiscover(homeAgent.getName());
		addBehaviour(db);
	}
	
	private class NotifyingBehavior extends OneShotBehaviour{
		
		@Override
		public void action() {
			ACLMessage notificationMessage = new ACLMessage( ACLMessage.INFORM );
			notificationMessage.setContent("");
			notificationMessage.addReceiver( new AID(homeAgent.getName(), AID.ISLOCALNAME ) );
            send( notificationMessage );
		}
		
	}

	public AID getHomeAgent() {
		return homeAgent;
	}

	public void setHomeAgent(AID homeAgent) {
		this.homeAgent = homeAgent;
	}

}
