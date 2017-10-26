package agent.appliances;

import agent.ApplianceAgent;
import interfaces.Object2ApplianceAgentInterface;
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
	
	public Demand getCurrentDemand() {
		int energy = 0;	
		
		if (currentTemp < temperatureToMaintain) {
			energy = (temperatureToMaintain - currentTemp) / strength;
			currentTemp = temperatureToMaintain;
		} else {
			currentTemp = ((3*currentTemp) + temperature[this.selfTime.getHourOfDay()])/4;
		}
		
		return new Demand(energy,this.selfTime.getHourOfDay());
	}
}
