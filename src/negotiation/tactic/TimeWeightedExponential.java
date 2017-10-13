package negotiation.tactic;

public class TimeWeightedExponential extends TimeWeightedFunction{

	public TimeWeightedExponential(double k, double beta, double timeMax) {
		super(k, beta, timeMax);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getValue(double time) {
		// TODO Auto-generated method stub
		//exponential used to evaluate value at time 
		double val=0;
		double x=(Math.min(time, TimeMax)/TimeMax);
		
		double a=Math.pow((1-x), Beta)*Math.log(K);
		val=Math.exp(a);
		return val;
	}

}
