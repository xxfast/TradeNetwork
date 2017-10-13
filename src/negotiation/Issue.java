package negotiation;

public class Issue {
	private double iteration;
	private double maxVal;
	private double minVal;
	/**
	 * @param iteration
	 * @param maxVal
	 * @param minVal

	 */
	public Issue(double maxVal, double minVal)
	{
		if(maxVal<=minVal)
		{
			this.maxVal=minVal;
			this.minVal=maxVal;
		}
		else
		{
			this.maxVal = maxVal;
			this.minVal = minVal;
		}
		this.iteration=0;
	}
	public Issue(double iteration, double maxVal, double minVal) {	
		if(maxVal<=minVal)
		{
			this.maxVal=minVal;
			this.minVal=maxVal;
		}
		else
		{
			this.maxVal = maxVal;
			this.minVal = minVal;
		}
		this.iteration = iteration;
		
	}
	public double getIteration() {
		return iteration;
	}
	public double incIteration()
	{
		return ++iteration;
	}
	public double getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(double maxVal) {
		this.maxVal = maxVal;
	}
	public double getMinVal() {
		return minVal;
	}
	public void setMinVal(double minVal) {
		this.minVal = minVal;
	}
	@Override
	public String toString() {
		return "Issue [iteration=" + iteration + ", maxVal=" + maxVal + ", minVal=" + minVal + "]";
	}
	
	
	

}
