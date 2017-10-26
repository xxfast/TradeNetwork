package descriptors;

import annotations.Adjustable;

public class RefrigeratorAgentDescriptor extends ApplianceAgentDescriptor {
	@Adjustable private int energyUsage = 2;

	public int getEnergyUsage() {
		return energyUsage;
	}

	public void setEnergyUsage(int energyUsage) {
		this.energyUsage = energyUsage;
	}
	
	public String getDescription() {
		return String.format("[RefrigeratorAgent: "+super.getName()+"-> ["+getOwner().getLocalName()+"] ,\n \t Starting Demand: "+getStartingDemand().getContent()+"]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{super.toArray()[0],super.toArray()[1],energyUsage};
		return toReturn;
	}

}
