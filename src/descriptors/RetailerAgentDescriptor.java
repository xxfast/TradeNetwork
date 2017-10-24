package descriptors;

import annotations.Adjustable;

@Adjustable(label = "An agent representing a single retailer")
public class RetailerAgentDescriptor extends TradeAgentDescriptor {
	@Adjustable private double maxNegotiationTime;
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
		return String.format("[RetailerAgent: "+super.getName() +",\n \t Max Negotiation time: "+ getMaxNegotiationTime()+",\n \t Param K: "+ getParamK() +",\n \t Param Beta: "+ getParamBeta()+",\n \t ]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getMaxNegotiationTime(),getParamK(),getParamBeta()};
		return toReturn;
	}
	
}
