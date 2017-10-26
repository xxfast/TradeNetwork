package agent.appliances;

import agent.ApplianceAgent;
import interfaces.Object2ApplianceAgentInterface;
import model.Demand;

public class RefrigeratorAgent extends ApplianceAgent implements Object2ApplianceAgentInterface{
	
	// This is the energy required per hour to run the fridge -> changeable
	private int energyUsage = 2;
	
	public Demand getCurrentDemand() {
		return new Demand(energyUsage,this.selfTime.getHourOfDay());
	}
}
