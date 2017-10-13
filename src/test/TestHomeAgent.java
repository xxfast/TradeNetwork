package test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.negotiator.AgentNegotiator.OfferStatus;
import negotiation.negotiator.HomeAgentNegotiator;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.TimeWeightedFunction;
import negotiation.tactic.TimeWeightedPolynomial;

public class TestHomeAgent {
	private final boolean INC=false;//customer mentality
	private double maxNegotiationTime=10;
	@Test
	public void TestScoringFunction() {
		double min=20;
		double max=40;
		double oldval=0;
		double newval=1;
		
		for(int i=0;i<=max-min;i++)
		{
			oldval=newval;
			newval=Function.custscoreFunction(min+i, min, max);
			assertTrue("score should be between 0 and 1",newval>=0 && newval<=1);			
			assertTrue("score shud be less than prev score",newval<=oldval);
		}
	}
	
	@Test
	public void TestHomeNegotiator() {
		double WFParamK=0.3;
		double WFParamBeta=0.5;  //Beta <1 competitive Beta >1 passive		
		
		//create TWfunction
		TimeWeightedFunction poly = new TimeWeightedPolynomial(WFParamK, WFParamBeta, this.maxNegotiationTime);
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		double timeTWeight=1;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(timeTWeight));
		try {
			priceStrat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR "+e.getMessage());
			
		}
		
		//add Strategy to negotiator's strategies
		ArrayList<Strategy> strats=new ArrayList<>();
		strats.add(priceStrat);
		
		//create score weights for negotiating items
		//ATM only price is considered so given full weight
		Map<Strategy.Item,Double>scoreWeights= new HashMap<>();
		//add only price item
		scoreWeights.put(Item.PRICE, new Double(1));
		
		//create price range for each offer item-obtain from source
		Map<Strategy.Item,Issue> itemissue = new HashMap<>();
		//only add price issue since we are only focusing on price
		itemissue.put(Strategy.Item.PRICE, new Issue(40, 20));
		
		//create negotiator with params
		HomeAgentNegotiator neg= new HomeAgentNegotiator( this.maxNegotiationTime, itemissue, strats, scoreWeights);
		
		Offer off=neg.generateOffer();
		double val=off.getOfferValue(Item.PRICE);
		
			
		neg.nextIteration();
		off=neg.generateOffer();
		double val2=off.getOfferValue(Item.PRICE);
		assertTrue("Next offer always higher than prev offer fro customer",val2>val);

		
	}
	@Test
	public void TestHomeNegotiatorIntepret() {
		double WFParamK=0.3;
		double WFParamBeta=0.5;  //Beta <1 competitive Beta >1 passive		
		
		//create TWfunction
		TimeWeightedFunction poly = new TimeWeightedPolynomial(WFParamK, WFParamBeta, this.maxNegotiationTime);
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		double timeTWeight=1;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(timeTWeight));
		try {
			priceStrat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR "+e.getMessage());
			
		}
		
		//add Strategy to negotiator's strategies
		ArrayList<Strategy> strats=new ArrayList<>();
		strats.add(priceStrat);
		
		//create score weights for negotiating items
		//ATM only price is considered so given full weight
		Map<Strategy.Item,Double>scoreWeights= new HashMap<>();
		//add only price item
		scoreWeights.put(Item.PRICE, new Double(1));
		
		//create price range for each offer item-obtain from source
		Map<Strategy.Item,Issue> itemissue = new HashMap<>();
		//only add price issue since we are only focusing on price
		itemissue.put(Strategy.Item.PRICE, new Issue(40, 20));
		
		//create negotiator with params
		HomeAgentNegotiator neg= new HomeAgentNegotiator( this.maxNegotiationTime, itemissue, strats, scoreWeights);
		
		Map<Item,Double> ret1= new HashMap<Item,Double>();
		ret1.put(Item.PRICE, new Double(29));
		
		Map<Item,Double> ret2= new HashMap<Item,Double>();
		ret2.put(Item.PRICE, new Double(25));
		
		Map<Item,Double> ret3= new HashMap<Item,Double>();
		ret3.put(Item.PRICE, new Double(32));
		//dummy offers from different retailers
		Offer o1= new Offer(ret1);
		o1.setOwner("ret1");
		
		Offer o2= new Offer(ret2);
		o2.setOwner("ret2");
		
		Offer o3 = new Offer(ret3);
		o3.setOwner("ret3");
		

	
		
		
		List<Offer> offers = new ArrayList<>();
		offers.add(o1);
		offers.add(o2);
		offers.add(o3);
		//test accept
		Map<Offer,HomeAgentNegotiator.OfferStatus> offerstatus =neg.interpretOffers(offers);
		
		for(Map.Entry<Offer, OfferStatus> entry :offerstatus.entrySet())
		{
			if(entry.getKey().getOwner().equals("ret2"))
			{
				assertTrue("ret2 should be accepted",entry.getValue().equals(OfferStatus.ACCEPT));
			}
			else
				assertTrue("ret should be rejected",entry.getValue().equals(OfferStatus.REJECT));
			
//			System.out.println("sender-"+entry.getKey().getOwner()+" status-"+entry.getValue());
		}
		//test counter offer
		ret1.put(Item.PRICE, new Double(30));
		ret2.put(Item.PRICE, new Double(31));
		ret3.put(Item.PRICE, new Double(32));
		
		
		offerstatus =neg.interpretOffers(offers);
//		System.out.println("Make Counter");
		for(Map.Entry<Offer, OfferStatus> entry :offerstatus.entrySet())
		{
			assertTrue("ret should be made counter offers",entry.getValue().equals(OfferStatus.COUNTER));
			
//			System.out.println("sender-"+entry.getKey().getOwner()+" status-"+entry.getValue());
		}
		
		System.out.println();
	
		Offer offer;
		for(;neg.getCurrentTime()<maxNegotiationTime;)
		{
			offer=neg.generateOffer();
			neg.nextIteration();
			
//			System.out.println("offer "+offer.getContent());
		}
		//test final offer, accept ret1
		ret1.put(Item.PRICE, new Double(35));
		ret2.put(Item.PRICE, new Double(37));
		ret3.put(Item.PRICE, new Double(38));
		offerstatus=neg.interpretOffers(offers);
		
		for(Map.Entry<Offer, OfferStatus> entry :offerstatus.entrySet())
		{
			if(entry.getKey().getOwner().equals("ret1"))
			{
				assertTrue("ret1 should be accepted",entry.getValue().equals(OfferStatus.ACCEPT));
			}
			else
				assertTrue("ret should be rejected",entry.getValue().equals(OfferStatus.REJECT));
			
//			System.out.println("sender-"+entry.getKey().getOwner()+" status-"+entry.getValue());
		}
		
		//test reject all after maxtime
		ret1.put(Item.PRICE, new Double(35));
		ret2.put(Item.PRICE, new Double(37));
		ret3.put(Item.PRICE, new Double(38));
		offerstatus=neg.interpretOffers(offers);
		for(Map.Entry<Offer, OfferStatus> entry :offerstatus.entrySet())
		{
			assertTrue("ret should be made counter offers",entry.getValue().equals(OfferStatus.REJECT));
			
//			System.out.println("sender-"+entry.getKey().getOwner()+" status-"+entry.getValue());
		}
		
	}

}
