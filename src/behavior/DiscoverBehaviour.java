package behavior;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

public class DiscoverBehaviour extends OneShotBehaviour {

	private String toDiscover;
	private AID discoverd;
	
	@Override
	public void action() {
		discover();
	}
	
	public void discover(){
		AMSAgentDescription[] agents = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this.getAgent() , new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println("Problem searching AMS: " + e);
			e.printStackTrace();
		}
		AID myID = this.getAgent().getAID();
		for (int i = 0; i < agents.length; i++) {
			AID agentID = agents[i].getName();
			if(!agentID.equals(myID)){
				if(agents[i].getClass().getName().equals(getToDiscover())){
					discoverd = agents[i].getName();
					break;
				}
			}
		}
	}

	public AID getDiscoverd() {
		return discoverd;
	}

	public void setDiscoverd(AID discoverd) {
		this.discoverd = discoverd;
	}

	public String getToDiscover() {
		return toDiscover;
	}

	public void setToDiscover(String toDiscover) {
		this.toDiscover = toDiscover;
	}

}
