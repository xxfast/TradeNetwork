package behavior;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

public class DiscoverBehaviour extends OneShotBehaviour {

	private Agent owner;
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
			agents = AMSService.search(this.getOwner() , new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println("Problem searching AMS: " + e);
			e.printStackTrace();
		}
		AID myID = this.getOwner().getAID();
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

	private Agent getOwner() {
		return owner;
	}

	private void setOwner(Agent owner) {
		this.owner = owner;
	}

	public AID getDiscoverd() {
		return discoverd;
	}

	public void setDiscoverd(AID discoverd) {
		this.discoverd = discoverd;
	}

	private String getToDiscover() {
		return toDiscover;
	}

	private void setToDiscover(String toDiscover) {
		this.toDiscover = toDiscover;
	}

}
