package agent;

import FIPA.DateTime;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;

import model.NotificationMessage;

public class ApplianceAgent extends TradeAgent implements Object2ApplianceAgentInterface { 

	private AID homeAgent;
	
	protected void setup() {
		addBehaviour(new DiscoverServiceProviderBehavior());
	}
	
	private class DiscoverServiceProviderBehavior extends OneShotBehaviour{
		
		@Override
		public void action() {
			DFAgentDescription[] serviceAgents = getService("SUPPLY");
	    	for (DFAgentDescription serviceAgent : serviceAgents) {
	    		homeAgent = serviceAgent.getName();
	    		break;
	    	}
		}
		
		DFAgentDescription[] getService( String service )
		{
			DFAgentDescription dfd = new DFAgentDescription();
	   		ServiceDescription sd = new ServiceDescription();
	   		sd.setType( service );
			dfd.addServices(sd);
			try
			{
				DFAgentDescription[] result = DFService.search(ApplianceAgent.this, dfd);
				return result;
			}
			catch (Exception fe) {}
	      	return null;
		}

	}

	public AID getHomeAgent() {
		return homeAgent;
	}

	public void setHomeAgent(AID homeAgent) {
		this.homeAgent = homeAgent;
	}



}
