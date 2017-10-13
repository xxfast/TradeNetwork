package negotiation.tactic;

public abstract class TimeWeightedFunction {
	protected double K;
	protected double Beta;
	protected double TimeMax;
	/**
	 * @param k
	 * @param beta
	 * @param timeMax
	 */
	public TimeWeightedFunction(double k, double beta, double timeMax) {		
		K = k;
		Beta = beta;
		TimeMax = timeMax;
	}	
	
	public double getBeta() {
		return Beta;
	}
	public void setBeta(double beta) {
		Beta = beta;
	}
	public double getTimeMax() {
		return TimeMax;
	}
	
	public abstract double getValue(double time);
	
	
	
	

}
