package descriptors;

import annotations.Adjustable;
import jade.core.AID;
import model.Demand;

public class ApplianceAgentDescriptor extends TradeAgentDescriptor {
	
	@Adjustable(label = "Name of the Scheduler") private AID schedulerAgent;
	@Adjustable(label = "Starting Demand") private Demand startingDemand;

	public Demand getStartingDemand() {
		return startingDemand;
	}

	public void setStartingDemand(Demand startingDemand) {
		this.startingDemand = startingDemand;
	}
	public AID getSchedulerAgent() {
		return schedulerAgent;
	}

	public void setSchedulerAgent(AID schedulerAgent) {
		this.schedulerAgent = schedulerAgent;
	}
	
	public String getDescription() {
		return String.format("[ApplianceAgent: "+super.getName()+"-> ["+getSchedulerAgent().getLocalName() +"] ,\n \t Starting Demand: "+getStartingDemand().getContent()+"]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getSchedulerAgent(),startingDemand};
		return toReturn;
	}

	
}
