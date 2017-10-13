package negotiation.tactic;

public class TimeWeightedPolynomial extends TimeWeightedFunction {

	public TimeWeightedPolynomial(double k, double beta, double timeMax) {
		super(k, beta, timeMax);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getValue(double time) {
		// TODO Auto-generated method stub
		//polynomial used to evaluate value at time 
		double val=0;
		double x=(Math.min(time, TimeMax)/TimeMax);
		
		val=K+(1-K)*(Math.pow(x, 1/Beta));
		return val;
	}
	
	
	

}
