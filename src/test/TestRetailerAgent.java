package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import FIPA.DateTime;
import model.Demand;
import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.negotiator.HomeAgentNegotiator;
import negotiation.negotiator.RetailerAgentNegotiator;
import negotiation.negotiator.AgentNegotiator.OfferStatus;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.timeFunction.TimeWeightedFunction;
import negotiation.tactic.timeFunction.TimeWeightedPolynomial;

public class TestRetailerAgent {

	private final boolean INC=true;//supplier mentality
	private double maxNegotiationTime=10;
	@Test
	public void TestScoringFunction() {
		double min=20;
		double max=40;
		double oldval=0;
		double newval=0;
		
		for(int i=0;i<=max-min;i++)
		{
			oldval=newval;
			
			newval=Function.retScoreFunction(min+i, min, max);
//			System.out.println("score "+newval);
			assertTrue("score should be between 0 and 1",newval>=0 && newval<=1);			
			assertTrue("score shud be less than prev score",newval>=oldval);
		}
	}
	
	@Test
	public void TestRetailNegotiator() {
		double WFParamK=0.3;
		double WFParamBeta=0.5;  //Beta <1 competitive Beta >1 passive		
		
		//create TWfunction
		TimeWeightedFunction poly = new TimeWeightedPolynomial(WFParamK, WFParamBeta, this.maxNegotiationTime);
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		double timeTacticWeight=1;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(timeTacticWeight));
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
		
	
		
		//create negotiator with params
		RetailerAgentNegotiator neg= new RetailerAgentNegotiator( this.maxNegotiationTime,strats, scoreWeights);
		neg.setInitialIssue(new Demand(10, (short)0, 1));
		Offer off=neg.generateOffer();
		double val=off.getOfferValue(Item.PRICE);
		
			
		neg.nextIteration();
		off=neg.generateOffer();
		double val2=off.getOfferValue(Item.PRICE);
//		System.out.println("val1 "+val+" val2 "+val2);
		assertTrue("Next offer always less than prev offer for supplier",val2<val);
		
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
		double timeTacticWeight=1;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(timeTacticWeight));
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
		
	
		
		//create negotiator with params
		RetailerAgentNegotiator neg= new RetailerAgentNegotiator( this.maxNegotiationTime,  strats, scoreWeights);
		neg.setInitialIssue(new Demand(10, (short)0, 1));
		Map<Item,Double> cust1= new HashMap<Item,Double>();
		cust1.put(Item.PRICE, new Double(25));
		
		//dummy offer
		Offer o1= new Offer(cust1);
		o1.setOwner("cust1");
		
		//test counter
		OfferStatus stat=neg.interpretOffer(o1);
		assertEquals(OfferStatus.COUNTER, stat);
		
		//test accept
		cust1.put(Item.PRICE, new Double(36));
		stat=neg.interpretOffer(o1);
		assertEquals(OfferStatus.ACCEPT, stat);
		
		//check if correct iteration
		assertTrue(neg.getCurrentTime()==2);
		
		//test reject
		for(;neg.getCurrentTime()<maxNegotiationTime;)
		{
			neg.generateOffer();
			neg.nextIteration();
			
//			System.out.println("offer "+offer.getContent());
		}
		
		//shud accept 1 before last
		stat=neg.interpretOffer(o1);
		assertEquals(OfferStatus.ACCEPT, stat);
		
		//shud accept
		stat=neg.interpretOffer(o1);
		assertEquals(OfferStatus.REJECT, stat);
			
	}
	

}
