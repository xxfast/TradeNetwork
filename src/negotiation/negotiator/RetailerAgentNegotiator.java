package negotiation.negotiator;

import java.util.ArrayList;
import java.util.Map;

import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;

public class RetailerAgentNegotiator extends AgentNegotiator {

	public RetailerAgentNegotiator(double maxNegotiationTime, Map<Item, Issue> itemIssue,
			ArrayList<Strategy> strategies, Map<Item, Double> scoreWeights) {
		super(maxNegotiationTime, itemIssue, strategies, scoreWeights);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double scoreFunction(double nextVal, double minVal, double maxVal) {
		// TODO Auto-generated method stub
		//simple increasing linear score function for supplier
		double val=0;
		double m=1/(maxVal-minVal);
		val=m*nextVal-m*(maxVal)+1;
		return val;
	}
	
	public OfferStatus interpretOffer(Offer offer)
	{
		//generate counter offer
		Offer counter=this.generateOffer();
		//check if iterations have exceeded
		if(currentTime>maxNegotiationTime)
		{
			//if it has then reject all offers, negotiation failed in time
			
			return OfferStatus.REJECT;
		}
		OfferStatus stat;
		//check if offer better than counter offer
		if(isBetterOffer(offer, counter))
		{
			stat=OfferStatus.ACCEPT;
		}
		else
			stat=OfferStatus.COUNTER;
		
		//update issues
		nextIteration();
		
		return stat;
		
	}
	private boolean isBetterOffer(Offer offer,Offer counterOffer)
	{
		double score=evalScore(counterOffer);
		return score<evalScore(offer);		
	}

}
