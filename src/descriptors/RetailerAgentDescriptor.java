package descriptors;

import annotations.Adjustable;

@Adjustable 
public class RetailerAgentDescriptor extends TradeAgentDescriptor {
	@Adjustable private int energyRate;
	@Adjustable private int energyThreshold;
	@Adjustable private int energyStored;
	
	public int getEnergyRate() {
		return energyRate;
	}
	public void setEnergyRate(int energyRate) {
		this.energyRate = energyRate;
	}
	public int getEnergyThreshold() {
		return energyThreshold;
	}
	public void setEnergyThreshold(int energyThreshold) {
		this.energyThreshold = energyThreshold;
	}
	public int getEnergyStored() {
		return energyStored;
	}
	public void setEnergyStored(int energyStored) {
		this.energyStored = energyStored;
	}
	
	public String getDescription() {
		return String.format("[RetailerAgent: "+super.getName() +",\n \t Energy Rate: "+ getEnergyRate() +",\n \t Energy Threshold: "+ getEnergyThreshold()+",\n \t Energy Stored: "+ getEnergyStored() +"]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getEnergyRate(),getEnergyThreshold(),getEnergyStored()};
		return toReturn;
	}
	
}
