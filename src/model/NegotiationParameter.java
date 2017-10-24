package model;

import java.io.Serializable;
import annotations.Adjustable;

@Adjustable
public class NegotiationParameter implements Serializable{
	@Adjustable private double maxNegotiationTime=10;
	@Adjustable private double ParamK=0.01;
	@Adjustable private double ParamBeta=0.5;
	
	public NegotiationParameter(){
	}
	
	public double getMaxNegotiationTime() {
		return maxNegotiationTime;
	}
	public void setMaxNegotiationTime(double maxNegotiationTime) {
		this.maxNegotiationTime = maxNegotiationTime;
	}
	public double getParamK() {
		return ParamK;
	}
	public void setParamK(double paramK) {
		ParamK = paramK;
	}
	public double getParamBeta() {
		return ParamBeta;
	}
	public void setParamBeta(double paramBeta) {
		ParamBeta = paramBeta;
	}
}
