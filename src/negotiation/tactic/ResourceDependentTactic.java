package negotiation.tactic;

import negotiation.Issue;
import negotiation.tactic.timeFunction.ResourceFunction;

public class ResourceDependentTactic extends Tactic {
	private ResourceFunction resourceFunction;
	private boolean natureInc;
	
	
	/**
	 * @param resourceFunction
	 * @param natureInc
	 */
	public ResourceDependentTactic(ResourceFunction resourceFunction, boolean natureInc) {
		this.resourceFunction = resourceFunction;
		this.natureInc = natureInc;
	}
	
	

	@Override
	public double nextValue(Issue issue) {
		double val=0;
		if(!natureInc)
		{
			//if decreasing nature of function use this formula
			val=issue.getMinVal()+resourceFunction.getValue(issue.getIteration())*(issue.getMaxVal()-issue.getMinVal());
		}
		else
		{
			//increasing nature
			val=issue.getMinVal()+(1-resourceFunction.getValue(issue.getIteration()))*(issue.getMaxVal()-issue.getMinVal());
		}
		return val;
	}

}
