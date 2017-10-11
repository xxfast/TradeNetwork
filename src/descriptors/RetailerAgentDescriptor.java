package descriptors;

public class RetailerAgentDescriptor extends TradeAgentDescriptor {
	private int energyRate;
	private int energyThreshold;
	private int energyStored;
	
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
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getEnergyRate(),getEnergyThreshold(),getEnergyStored()};
		return toReturn;
	}
	
}
