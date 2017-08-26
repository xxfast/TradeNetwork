package agent;

import FIPA.DateTime;
import behavior.DiscoverBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.NotificationMessage;

public class ApplianceAgent extends Agent { 

	private AID homeAgent;
	
	protected void setup() {
		DiscoverBehaviour db = new DiscoverBehaviour();
		db.setToDiscover(homeAgent.getName());
		addBehaviour(db);
		addBehaviour(new NotifyingBehavior());
	}
	
	private class NotifyingBehavior extends OneShotBehaviour{
		
		@Override
		public void action() {
			ACLMessage notificationMessage = new NotificationMessage(1,1,new DateTime());
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
