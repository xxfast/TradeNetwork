package descriptors;

import annotations.Adjustable;
import jade.core.AID;
import negotiation.tactic.Tactic;

@Adjustable(label = "An agent representing a single home")
public class HomeAgentDescriptor extends TradeAgentDescriptor {
	@Adjustable private double maxNegotiationTime; // must be non-zero
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
		return String.format("[HomeAgent: "+getMaxNegotiationTime()+","+getParamK()+","+getParamBeta()+","+getTacticType()+"]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{getMaxNegotiationTime(),getParamK(),getParamBeta(),getTacticType()};
		return toReturn;
	}
	
}
