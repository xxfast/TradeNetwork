package negotiation.tactic.behaviour;

import negotiation.Issue;
import negotiation.NegotiationThread;
import negotiation.Strategy.Item;

public class AverageTitForTat extends TitForTat{

	public AverageTitForTat( Item offerItem) {
		super(offerItem);
		// TODO Auto-generated constructor stub
	}
	public AverageTitForTat(Item offerItem,NegotiationThread thread) {
		super(offerItem, thread);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double generateCounterValue(int range, Issue nextIssue) {
		// TODO Auto-generated method stub
		int n=negotiationThread.size()-1;
		double first=negotiationThread.get(n-2*range).getOfferValue(this.OfferItem);
		double last=negotiationThread.get(n).getOfferValue(this.OfferItem);
		double lastOff=negotiationThread.get(n-1).getOfferValue(this.OfferItem);
		
		double val=(first/last)*lastOff;
		return Math.min(Math.max(val, nextIssue.getMinVal()),nextIssue.getMaxVal());
	}

}
