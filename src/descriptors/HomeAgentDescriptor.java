package descriptors;

import annotations.Adjustable;
import jade.core.AID;

@Adjustable(label = "An agent representing a single home")
public class HomeAgentDescriptor extends TradeAgentDescriptor {
	@Adjustable private double maxNegotiationTime; // must be non-zero
	@Adjustable private double paramK;
	@Adjustable private double paramBeta;
	
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
		return String.format("[HomeAgent: "+getMaxNegotiationTime()+","+getParamK()+","+getParamBeta()+"]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getMaxNegotiationTime(),getParamK(),getParamBeta()};
		return toReturn;
	}
	
}
