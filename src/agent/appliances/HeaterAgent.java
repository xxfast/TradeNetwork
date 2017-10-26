package agent.appliances;

import agent.ApplianceAgent;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import model.Demand;
import model.Time;

public class HeaterAgent extends ApplianceAgent implements Object2ApplianceAgentInterface {
	
	// Amount of temp change for one unit of energy -> changeable
	private int strength = 2;
	// Temperature for the heater to maintain -> changeable
	private int temperatureToMaintain = 24;
	// Thermostat of the heater (can also be considered the starting rate -> changeable
	private int currentTemp = 15;
	// Hourly temperature during the day -> not changeable
	private final int[] temperature = {15,13,10,9,7,6,9,11,13,15,18,21,22,23,24,24,23,20,19,18,17,16,15,15};
	
	protected void setup() {
		super.setup();
		Object[] args = getArguments();
		setStrength((int)args[2]); 
		setTemperatureToMaintain((int)args[3]); 
		setCurrentTemp((int)args[4]); 
	}
	
	public Demand getCurrentDemand() {
		int energy = 0;	
		
		if (getCurrentTemp() < getTemperatureToMaintain()) {
			energy = (getTemperatureToMaintain() - getCurrentTemp()) / getStrength();
			setCurrentTemp(getTemperatureToMaintain());
		} else {
			setCurrentTemp(((3*getCurrentTemp()) + temperature[this.selfTime.getHourOfDay()])/4);
		}
		
		return new Demand(energy,this.selfTime.getHourOfDay());
	}

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
}
