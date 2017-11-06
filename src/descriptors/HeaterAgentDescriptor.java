package descriptors;

import annotations.Adjustable;

public class HeaterAgentDescriptor extends ApplianceAgentDescriptor {
	
	public enum Mode { ON, OFF }
	
	@Adjustable private int strength = 2;
	// Temperature for the heater to maintain -> changeable
	@Adjustable private int temperatureToMaintain = 24;
	// Thermostat of the heater (can also be considered the starting rate -> changeable
	@Adjustable private int currentTemp = 15;
	@Adjustable private Mode currentMode;
	
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public int getTemperatureToMaintain() {
		return temperatureToMaintain;
	}
	public void setTemperatureToMaintain(int temperatureToMaintain) {
		this.temperatureToMaintain = temperatureToMaintain;
	}
	public int getCurrentTemp() {
		return currentTemp;
	}
	public void setCurrentTemp(int currentTemp) {
		this.currentTemp = currentTemp;
	}

	public String getDescription() {
		return String.format("[HeaterAgent: "+super.getName()+"-> ["+getOwner().getLocalName()+"] ,\n \t Starting Demand: "+getStartingDemand().getContent() + "\n \t Current condition: " + getCurrentMode()+"]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		super.toArray();
		Object[] toReturn = new Object[]{super.toArray()[0],super.toArray()[1],strength,temperatureToMaintain,currentTemp};
		return toReturn;
	}
	public Mode getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(Mode currentMode) {
		this.currentMode = currentMode;
	}
}
