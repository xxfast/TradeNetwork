package descriptors;

import annotations.Adjustable;
import jade.core.AID;
import model.Demand;

@Adjustable(label = "An agent representing a single appliance")
public class ApplianceAgentDescriptor extends TradeAgentDescriptor {
	
	@Adjustable(label = "Name of the Scheduler") private AID schedulerAgent;
	@Adjustable(label = "Starting Demand")private Demand startingDemand;

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
		return String.format("[ApplianceAgent: "+super.getName()+"-> ["+getSchedulerAgent()+"] ,\n \t Starting Demand: "+getStartingDemand().getContent()+"]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getSchedulerAgent(),startingDemand};
		return toReturn;
	}

	
}
