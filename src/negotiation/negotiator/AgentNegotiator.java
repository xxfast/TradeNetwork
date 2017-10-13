package negotiation.negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;

public abstract class AgentNegotiator {
	//NOTES
			//time is considered as number of iterations in a negotiation
			//Negotiation
			/////////////////				
			protected double maxNegotiationTime;
			protected Map<Strategy.Item,Issue> itemIssue;
			//strategies for each value in proposal
			protected ArrayList<Strategy> strategies;
			protected Map<Strategy.Item,Double> scoreWeights;
			protected ArrayList<Offer> myOffers;
			protected double currentTime;
		
			///////////////
			public enum OfferStatus{
				ACCEPT,
				REJECT,
				COUNTER
			}
			/**
			 * @param wFParamK
			 * @param wFParamBeta
			 * @param maxNegotiationTime
			 * @param minPrice
			 * @param maxPrice
			 * @param strategies
			 * @param scoreWeights
			 */
			public AgentNegotiator(double maxNegotiationTime, Map<Item,Issue> itemIssue, ArrayList<Strategy> strategies, Map<Item, Double> scoreWeights) {			
				this.maxNegotiationTime = maxNegotiationTime;
				this.itemIssue=itemIssue;
				this.strategies = strategies;
				this.scoreWeights = scoreWeights;
				this.myOffers= new ArrayList<>();
				this.currentTime=0;
				
			}
			
			public double getCurrentTime() {
				return currentTime;
			}

			public abstract double scoreFunction(double nextVal,double minVal,double maxVal);
			
			
			protected double evalScore(Offer offer)
			{
				//agent scoring mechanism logic
				double score=0;
				Map<Strategy.Item,Double> offerVals=offer.getOfferValues();
				for(Map.Entry<Strategy.Item, Double> entry:scoreWeights.entrySet())
				{
					double itemVal=offerVals.get(entry.getKey());
					Issue iIssue=itemIssue.get(entry.getKey());
					score+=scoreFunction(itemVal,iIssue.getMinVal(),iIssue.getMaxVal())*entry.getValue();
				}
				return score;
			}
			
			public Offer generateOffer()
			{
				
				//ask each strategy to generate values based on issues
				Map<Strategy.Item,Double> offerVals = new HashMap<>();
				for(Strategy strat:strategies)
				{
//					System.out.println("issue "+itemIssue.get(strat.getItem()));
					if(strat.generateOfferValue(itemIssue.get(strat.getItem())))
					{
						double newVal=strat.getCurrentVal();
						offerVals.put(strat.getItem(), newVal);
					}
					
				}
				Offer offer = new Offer(offerVals);
				myOffers.add(offer);
				return offer;
			}
			
			public void nextIteration()
			{
				
					//increment iteration of all itemissues
					for(Map.Entry<Item, Issue> entry:itemIssue.entrySet())
					{
						entry.getValue().incIteration();
						
					}
					//increment current time
					currentTime++;
				
			}
			public Offer getLastOffer()
			{
				if(myOffers.size()>0)
					return myOffers.get(myOffers.size()-1);
				else
					return null;
			}

			public Map<Strategy.Item, Issue> getItemIssue() {
				return itemIssue;
			}
}
