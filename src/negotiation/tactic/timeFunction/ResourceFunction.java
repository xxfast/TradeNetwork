package negotiation.tactic.timeFunction;

public abstract class ResourceFunction extends Function {
	protected double K;
	
	/**
	 * @param k
	 */
	public ResourceFunction(double k) {
		K = k;
	}

	public double getValue(double time)
	{
		double val=0;
		val=K+(1-K)*Math.exp(-calculateResource(time));
		return val;
	}
	
	protected abstract double calculateResource(double time);
}
