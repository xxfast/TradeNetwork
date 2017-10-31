package negotiation.tactic;

import negotiation.Issue;

public abstract class Tactic {
	public enum Type
	{
		TIMEDEPENDENT,
		RESOURCEDEPENDENT,
		BEHAVIOURDEPENDENT,
		COMBINATION
	}
	
	public abstract double nextValue(Issue issue);

}
