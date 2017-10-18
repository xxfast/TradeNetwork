package negotiation.tactic.behaviour;

import java.util.List;

import model.Offer;
import negotiation.Issue;
import negotiation.NegotiationThread;
import negotiation.Strategy.Item;

public abstract class TitForTat {
	protected NegotiationThread negotiationThread;
	protected Item OfferItem;
	

	/**
	 * @param negotiationThread
	 * @param offerItem
	 */
	public TitForTat( Item offerItem) {
		
		OfferItem = offerItem;
	}
	public TitForTat( Item offerItem,NegotiationThread thread) {
		negotiationThread=thread;
		OfferItem = offerItem;
	}
	
	public NegotiationThread getNegotiationThread() {
		return negotiationThread;
	}
	
	public void setNegotiationThread(NegotiationThread negotiationThread) {
		this.negotiationThread = negotiationThread;
	}


	public double getCounterValue(int range,Issue nextIssue)
	{
		//if insufficient history
		
		if(negotiationThread.size()>2*range)
			return generateCounterValue(range,nextIssue);
		else 
			return 0;
			
	}
	
	protected abstract double generateCounterValue(int range,Issue nextIssue);
	
	

}
