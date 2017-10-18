package negotiation.negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.core.IsInstanceOf;

import FIPA.DateTime;
import model.Demand;
import model.Offer;
import negotiation.Issue;
import negotiation.NegotiationThread;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.tactic.BehaviourDependentTactic;
import negotiation.tactic.Tactic;

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
			protected Demand demand;
			protected NegotiationThread negotiationThread;
		
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
			public AgentNegotiator()
			{
				this.currentTime=0;
			}
			public AgentNegotiator(double maxNegotiationTime, ArrayList<Strategy> strategies, Map<Item, Double> scoreWeights) {			
				this.maxNegotiationTime = maxNegotiationTime;
				this.itemIssue=new HashMap<>();
				this.strategies = new ArrayList<>();
				//add clones of strategies, avoids single reference problems
				for(Strategy strat: strategies)
				{
					this.strategies.add(strat.clone());
				}
				this.scoreWeights = scoreWeights;
				this.myOffers= new ArrayList<>();
				this.currentTime=0;
				demand= new Demand(new DateTime());
				negotiationThread= new NegotiationThread();
				//pass negotiation thread to strategies with behaviour tactics
				passNegotiationThreadToTactics();
				
			}
			
			public double getCurrentTime() {
				return currentTime;
			}

			public abstract double scoreFunction(double nextVal,double minVal,double maxVal);
			
			protected void passNegotiationThreadToTactics()
			{
				for(Strategy strat: strategies)
				{
					for(Map.Entry<Tactic, Double> entry :strat.getTactics().entrySet())
					{
						if(entry.getKey() instanceof BehaviourDependentTactic)
						{
							BehaviourDependentTactic beh =(BehaviourDependentTactic)entry.getKey();
							beh.getTitForTat().setNegotiationThread(this.negotiationThread);
						}
					}
				}
			}
			public double evalScore(Offer offer)
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
			
			public OfferStatus interpretOffer(Offer offer)
			{
				//store offer in negotiationThread
				negotiationThread.addOffer(offer);
				
				//check if iterations have exceeded
				if(currentTime>maxNegotiationTime)
				{
					//check if the offer is the same as my previous offer- this has to be done due to protocol limitations
					// the retailer can only accept a proposal by proposing the last counter offer made
					if(evalScore(offer)==evalScore(getLastOffer()))
					{
						//retailer has accepted counter offer, so ACCEPT this offer
						return OfferStatus.ACCEPT;
					}
					//if it has then reject all offers, negotiation failed in time
					
					return OfferStatus.REJECT;
				}
				//generate counter offer
				Offer counter=this.generateOffer();
				OfferStatus stat;
				//check if offer better than counter offer
				if(isBetterOffer(offer, counter))
				{
					stat=OfferStatus.ACCEPT;
				}
				else
				{
					stat=OfferStatus.COUNTER;
					//add counter offer to neg thread
					negotiationThread.addOffer(counter);
				}
					
				
				//update issues
				nextIteration();
				
				return stat;
				
			}
			private boolean isBetterOffer(Offer offer,Offer counterOffer)
			{
				double score=evalScore(counterOffer);
				return score<evalScore(offer);		
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
			//setup intial issue which determines min and max range values
			public void setInitialIssue(Demand demand)
			{
				//TODO change for market price issue
				//simply creating a default issue based on demand
				this.demand=demand;
				for(Strategy strat:strategies)
				{
					this.itemIssue.put(strat.getItem(), new Issue(40,20));
				}
				
				
			}
			public Offer getLastOffer()
			{
				if(myOffers.size()>0)
				{
					Offer off=myOffers.get(myOffers.size()-1);
					off.setDemand(demand);
					return off;
				}
					
				else
					return null;
			}

			public Map<Strategy.Item, Issue> getItemIssue() {
				return itemIssue;
			}
			public Demand getDemand() {
				return demand;
			}
			public NegotiationThread getNegotiationThread() {
				return negotiationThread;
			}
			public void setDemand(Demand demand) {
				this.demand = demand;
			}
}
