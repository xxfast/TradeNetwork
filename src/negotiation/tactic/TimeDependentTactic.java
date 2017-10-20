package negotiation.tactic;

import negotiation.Issue;
import negotiation.tactic.timeFunction.TimeWeightedFunction;

public class TimeDependentTactic extends Tactic{
	private TimeWeightedFunction weightedFunction;
	private boolean natureInc;

	/**
	 * @param weightedFunction
	 */
	public TimeDependentTactic(TimeWeightedFunction weightedFunction,boolean inc) {		
		this.weightedFunction = weightedFunction;
		this.natureInc=inc;
	}
	
	
	
	public double nextValue(Issue issue)
	{
		double val=0;
		if(!natureInc)
		{
			//if decreasing nature of function use this formula
			val=issue.getMinVal()+weightedFunction.getValue(issue.getIteration())*(issue.getMaxVal()-issue.getMinVal());
		}
		else
		{
			//increasing nature
			val=issue.getMinVal()+(1-weightedFunction.getValue(issue.getIteration()))*(issue.getMaxVal()-issue.getMinVal());
		}
		return val;
	}
	
	
}
