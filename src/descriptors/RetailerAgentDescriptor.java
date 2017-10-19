package descriptors;

public class RetailerAgentDescriptor extends TradeAgentDescriptor {
	private double maxNegotiationTime;
	private double paramK;
	private double paramBeta;
	
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
		return String.format("[RetailerAgent: "+super.getName() +",\n \t Param K: "+ getParamK() +",\n \t Param Beta: "+ getParamBeta()+",\n \t ]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getParamK(),getParamBeta()};
		return toReturn;
	}
	
}
