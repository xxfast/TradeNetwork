package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TradeAgent extends Agent {

	protected void setup() {
		System.out.println(getDescription() + ": " + "initialising");
	}
	
	public String getDescription(){
		return getLocalName();
	}
	
	public AID getAgentFromAMS(String localname)
	{
		AID agent=null;
		SearchConstraints c = new SearchConstraints();
		c.setMaxResults((long) -1);
		AMSAgentDescription[] agents=null;
		try {
			 agents= AMSService.search(this, new AMSAgentDescription(), c);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < agents.length; i++) {
			if(agents[i].getName().getLocalName().equals(localname))
			{
				agent=agents[i].getName();
			}
			
		}
		return agent;
	}
	//get agents for service
	public DFAgentDescription[] getServiceAgents( String service ) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType( service );
		dfd.addServices(sd);
		try
		{
			DFAgentDescription[] result = DFService.search(this, dfd);
			return result;
		}
		catch (Exception fe) {}
		return null;
	}
}
