package negotiation.tactic.timeFunction;

public class ResourceTimeFunction extends ResourceFunction{
	protected double maxTime;
	public ResourceTimeFunction(double k, double maxTime) {
		super(k);
		this.maxTime=maxTime;
		// TODO Auto-generated constructor stub
	}
	//time is a resource which depletes as we progress
	@Override
	protected double calculateResource(double time) {
		// TODO Auto-generated method stub
		
		return Math.max(0, maxTime-time);
	}

}
