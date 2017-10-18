package negotiation.tactic;

import negotiation.Issue;
import negotiation.tactic.behaviour.TitForTat;

public class BehaviourDependentTactic extends Tactic {
	private TitForTat titForTat;
	private int range;
	
	/**
	 * @param titForTat
	 */
	public BehaviourDependentTactic(TitForTat titForTat,int range) {
		this.titForTat = titForTat;
		this.range=range;
	}
	
	@Override
	public double nextValue(Issue issue) {
		// TODO Auto-generated method stub
		return titForTat.getCounterValue(range, issue);
		
	}

	public TitForTat getTitForTat() {
		return titForTat;
	}

}
