package descriptors;

import jade.core.AID;

public class HomeAgentDescriptor extends TradeAgentDescriptor {

	private AID schedulerAgent;
	private double maxNegotiationTime;
	private double paramK;
	private double paramBeta;

	public AID getSchedulerAgent() {
		return schedulerAgent;
	}

	public void setSchedulerAgent(AID schedulerAgent) {
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
		return String.format("[HomeAgent:" +  getName() + " -> ["+ getSchedulerAgent().getLocalName() +","+getMaxNegotiationTime()+","+getParamK()+","+getParamBeta()+"] ]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getSchedulerAgent(),getMaxNegotiationTime(),getParamK(),getParamBeta()};
		return toReturn;
	}
	
}
