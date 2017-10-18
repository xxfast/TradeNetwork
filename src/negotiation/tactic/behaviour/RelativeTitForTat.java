package negotiation.tactic.behaviour;

import negotiation.Issue;
import negotiation.NegotiationThread;
import negotiation.Strategy.Item;

public class RelativeTitForTat extends TitForTat{

	public RelativeTitForTat(Item OfferItem) {
		super(OfferItem);
		// TODO Auto-generated constructor stub
	}
	
	public RelativeTitForTat( Item offerItem,NegotiationThread thread) {
		super(offerItem, thread);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double generateCounterValue(int range,Issue nextIssue) {
		// TODO Auto-generated method stub
		
		int n=negotiationThread.size()-1;
		int index=n-(2*range);
		double first=negotiationThread.get(index).getOfferValue(this.OfferItem);
	
		double second=negotiationThread.get((index)+2).getOfferValue(this.OfferItem);

		double lastOff=negotiationThread.get(n-1).getOfferValue(this.OfferItem);
		
		double val=(first/second)*lastOff;
		
		return Math.min(Math.max(val, nextIssue.getMinVal()),nextIssue.getMaxVal());
	}

}
