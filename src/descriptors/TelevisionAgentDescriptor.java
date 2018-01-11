package descriptors;

import annotations.Adjustable;

public class TelevisionAgentDescriptor extends ApplianceAgentDescriptor {
	@Adjustable private int energyUsage = 2;

	
	public int getEnergyUsage() {
		return energyUsage;
	}

	public void setEnergyUsage(int energyUsage) {
		this.energyUsage = energyUsage;
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{super.toArray()[0],super.toArray()[1],energyUsage};
		return toReturn;
	}
	

}
