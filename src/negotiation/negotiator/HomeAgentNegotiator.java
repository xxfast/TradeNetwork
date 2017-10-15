package negotiation.negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.lang.acl.ACLMessage;
import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;

public class HomeAgentNegotiator extends AgentNegotiator{

		/**
		 * @param wFParamK
		 * @param wFParamBeta
		 * @param maxNegotiationTime
		 * @param minPrice
		 * @param maxPrice
		 * @param strategies
		 * @param scoreWeights
		 */
		public HomeAgentNegotiator(double maxNegotiationTime, Map<Item,Issue> itemIssue, ArrayList<Strategy> strategies, Map<Item, Double> scoreWeights) {			
			super(maxNegotiationTime,itemIssue,strategies,scoreWeights);
			
		}
		
		public double getCurrentTime() {
			return currentTime;
		}

		public double scoreFunction(double nextVal,double minVal,double maxVal)
		{
			//simple decreasing linear score function for customer
			double val=0;
			double m=-1/(maxVal-minVal);
			val=m*nextVal-m*(minVal)+1;
			return val;
		}
		
		
		
	
	
		
		public Map<Offer,OfferStatus> interpretOffers(List<Offer> offers)
		{
			//interpret all offers for current issue i.e iteration
			Map<Offer,OfferStatus> results= new HashMap<>();
			//generate counter offer
			Offer counter=this.generateOffer();
			//check if iterations have exceeded
			if(currentTime>maxNegotiationTime)
			{
				//if it has then reject all offers, negotiation failed in time
				for(Offer off: offers)
				{
					results.put(off, OfferStatus.REJECT);
				}
				return results;
			}
			
			//check if any score of any offer is more than my next counter offer
			Offer bestOffer=checkOffers(offers,counter);
			//if we have a better one
			if(bestOffer!=null)
			{
				//construct results accepting bestOffer
				for(Offer off: offers)
				{
					//accept best offer and reject rest
					if(off.getOwner().equals(bestOffer.getOwner()))
						results.put(off, OfferStatus.ACCEPT);
					else
						results.put(off, OfferStatus.REJECT);
				}
			}
			else
			{
				//make counter offers to all offers
				for(Offer off: offers)
				{
					results.put(off, OfferStatus.COUNTER);
				}
				
			}
			//update to next iteration
			nextIteration();
			return results;
		}
		private Offer checkOffers(List<Offer> offers,Offer counterOffer)
		{
			//determine best offer based on score of counter offer
			//if multiple best select one with higher score, if equal select 1st one
			//return null if none are good
			
			double myscore=evalScore(counterOffer);
			//list to store all good offers
			List<Offer> goodOffers = new ArrayList<>();
			
			for(Offer off:offers)
			{
				
				double score=evalScore(off);
				//check if offer score more than counter offer
				if(score>myscore)
					goodOffers.add(off);
				
			}
			Offer best=null;
			//check if more than 1 good offer
			if(goodOffers.size()>1)
			{
				best=getBestOffer(goodOffers);
				
			}
			//check if only 1 good one
			else if(goodOffers.size()==1)
			{
				best=goodOffers.get(0);
			}
			
			//otherwise return null
			return best;
		}
		private Offer getBestOffer(List<Offer> goodOffers)
		{
			double bestscore=0;
			
			List<Offer> equal= new ArrayList<>();
			//determine best score
			for(Offer good:goodOffers)
			{
				
				double score=evalScore(good);
				if(score>bestscore)
					bestscore=score;		
				
			}
			//add offers which have the best score- can be many
			for(Offer good:goodOffers)
			{
				
				if(evalScore(good)==bestscore)
					//add it to equal list
					equal.add(good);
			}
			
			//simply select the 1st one, but TODO can change if adding intelligence
			return equal.get(0);
		}
//		public Map<Strategy.Item, Issue> getItemIssue() {
//			return itemIssue;
//		}
	
		
		
		

}
