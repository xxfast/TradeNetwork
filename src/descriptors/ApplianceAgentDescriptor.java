package descriptors;

import annotations.Adjustable;
import interfaces.IOwnable;
import jade.core.AID;
import model.Demand;

@Adjustable(label = "An agent representing a single appliance")
public class ApplianceAgentDescriptor extends TradeAgentDescriptor implements IOwnable {
	
	@Adjustable private AID owner;
	@Adjustable private Demand startingDemand;

	public Demand getStartingDemand() {
		return startingDemand;
	}

	public void setStartingDemand(Demand startingDemand) {
		this.startingDemand = startingDemand;
	}
	public AID getOwner() {
		return owner;
	}

	public void setOwner(AID owner) {
		this.owner = owner;
	}
	
	public String getDescription() {
		return String.format("[ApplianceAgent: "+super.getName()+"-> ["+getOwner().getLocalName()+"] ,\n \t Starting Demand: "+getStartingDemand().getContent()+"]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getOwner(),startingDemand};
		return toReturn;
	}

	
}
