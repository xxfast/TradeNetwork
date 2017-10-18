package descriptors;

import jade.core.AID;

public class HomeAgentDescriptor extends TradeAgentDescriptor {

	private AID schedulerAgent;

	public AID getSchedulerAgent() {
		return schedulerAgent;
	}

	public void setSchedulerAgent(AID schedulerAgent) {
		this.schedulerAgent = schedulerAgent;
	}
	
	public String getDescription() {
		return String.format("[HomeAgent:" +  getName() + " -> ["+ getSchedulerAgent().getLocalName() +"] ]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getSchedulerAgent()};
		return toReturn;
	}
	
}
