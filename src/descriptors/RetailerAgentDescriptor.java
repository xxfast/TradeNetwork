package descriptors;

import annotations.Adjustable;
import negotiation.tactic.Tactic;

@Adjustable(label = "An agent representing a single retailer")
public class RetailerAgentDescriptor extends TradeAgentDescriptor {
	@Adjustable private double maxNegotiationTime;
	@Adjustable private double paramK;
	@Adjustable private double paramBeta;
	@Adjustable private Tactic.Type tacticType;
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

	public Tactic.Type getTacticType() {
		return tacticType;
	}

	public void setTacticType(Tactic.Type tacticType) {
		this.tacticType = tacticType;
	}

	public String getDescription() {
		return String.format("[RetailerAgent: "+super.getName() +",\n \t Max Negotiation time: "+ getMaxNegotiationTime()+",\n \t Param K: "+ getParamK() +",\n \t Param Beta: "+ getParamBeta()+",\n \t"+",\n \t Tactic: "+ getTacticType()+",\n \t ]");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getMaxNegotiationTime(),getParamK(),getParamBeta(),getTacticType()};
		return toReturn;
	}
	
}
