package agent.appliances;

import agent.ApplianceAgent;
import interfaces.Object2ApplianceAgentInterface;
import model.Demand;
import model.Time;

public class TelevisionAgent extends ApplianceAgent implements Object2ApplianceAgentInterface{

	// This is the energy required per hour to run the tv -> changeable
	private int energyUsage = 2;
	
	public Demand getCurrentDemand() {
		int energy = 0;
		int time = this.selfTime.getHourOfDay();
		
		if (time < 10) {
			energy = energyUsage;
		}
		
		return new Demand(energy,this.selfTime.getHourOfDay());
	}
}
