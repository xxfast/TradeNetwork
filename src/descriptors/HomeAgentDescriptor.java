package descriptors;

import annotations.Adjustable;
import jade.core.AID;

@Adjustable(label = "An agent representing a single home")
public class HomeAgentDescriptor extends TradeAgentDescriptor {
	@Adjustable(label = "Name of the Scheduler")
	private String schedulerAgent;
	@Adjustable(label = "Maximum Negotiation Time")
	private double maxNegotiationTime;
	@Adjustable(label = "Paramaeter K")
	private double paramK;
	@Adjustable(label = "Paramaeter Beta")
	private double paramBeta;

	public String getSchedulerAgent() {
		return schedulerAgent;
	}

	public void setSchedulerAgent(String schedulerAgent) {
		this.schedulerAgent = schedulerAgent;
	}
	
	public double getMaxNegotiationTime() {
		return maxNegotiationTime;
	}

	public void setMaxNegotiationTime(double maxNegotiationTime) {
		this.maxNegotiationTime = maxNegotiationTime;
	}

	public double getParamK() {
		return paramK;
	}

	public void setParamK(double paramK) {
		this.paramK = paramK;
	}

	public double getParamBeta() {
		return paramBeta;
	}

	public void setParamBeta(double paramBeta) {
		this.paramBeta = paramBeta;
	}

	public String getDescription() {
		return String.format("[HomeAgent:" +  getName() + " -> ["+ getSchedulerAgent() +","+getMaxNegotiationTime()+","+getParamK()+","+getParamBeta()+"] ]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getSchedulerAgent(),getMaxNegotiationTime(),getParamK(),getParamBeta()};
		return toReturn;
	}
	
}
