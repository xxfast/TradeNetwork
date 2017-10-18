package test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import model.Offer;
import negotiation.Issue;
import negotiation.NegotiationThread;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.tactic.BehaviourDependentTactic;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.behaviour.RelativeTitForTat;
import negotiation.tactic.timeFunction.TimeWeightedExponential;
import negotiation.tactic.timeFunction.TimeWeightedFunction;
import negotiation.tactic.timeFunction.TimeWeightedPolynomial;
public class TestStrategy {
	public final double Delta=0.001;
	@Test
	public void TestStrategyContruction() {
		Strategy strat = new Strategy(Strategy.Item.PRICE);
		//testing customer behaviour
		double k=0.3;
		double beta=0.5;
		double maxTime=10;
		TimeWeightedFunction func = new TimeWeightedPolynomial(k, beta, maxTime);
		//tactic for customer
		Tactic timedep = new TimeDependentTactic(func, false);
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(timedep, new Double(1));
		
		try {
			strat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("Strat for price created",Strategy.Item.PRICE,strat.getItem());
		assertEquals("Initial value is 0", 0,strat.getCurrentVal(),Delta);
		//test new issue
		Issue issue = new Issue(0,40,20);
		
		//check if issue added successfully
		assertTrue("Adding issue should succeed",strat.generateOfferValue(issue));
	}
	@Test
	public void TestStrategyOperation() {
		Strategy strat = new Strategy(Strategy.Item.PRICE);
		//testing customer behaviour
		double k=0.3;
		double beta=0.5;
		double maxTime=10;
		TimeWeightedFunction func = new TimeWeightedPolynomial(k, beta, maxTime);
		//tactic for customer
		Tactic timedep = new TimeDependentTactic(func, false);
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(timedep, new Double(1));
		
		try {
			strat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//test new issue
		Issue issue = new Issue(0,40,20);
		
		//check if issue added successfully
		assertTrue("Adding issue should succeed",strat.generateOfferValue(issue));
		
		//check if a correct value was generated
		double val=strat.getCurrentVal();
		
		assertTrue("Offer should be between 10 and 20",val>20 && val<40);
		
		assertFalse("Adding an out of sequence issue should fail",strat.generateOfferValue(new Issue(5,20,10)));
		
		//add next issue in sequence
		issue.incIteration();
		assertTrue("Adding issue should succeed",strat.generateOfferValue(issue));
		double nextval=strat.getCurrentVal();
		
		
		assertTrue("The next value has to be more than the previous",nextval>val);
	}
	@Test
	public void TestStrategyOperationWithBehaviourTactic() {
		//setting neg thread for behaviour
		NegotiationThread th = new NegotiationThread();
		
		RelativeTitForTat rel = new RelativeTitForTat( Item.PRICE,th);
			
		//testing for customer
		//add some dummy history
		
		Map<Item,Double> ret1= new HashMap<Item,Double>();	
		Offer o1= new Offer(ret1);
		o1.setOwner("ret1");
		
		Map<Item,Double> cust= new HashMap<Item,Double>();	
		Offer c1= new Offer(cust);
		c1.setOwner("cust");
		

		
		//main test*****************************************
		Strategy strat = new Strategy(Strategy.Item.PRICE);
		Issue issue= new Issue(30, 18);	
		//testing customer behaviour
		double k=0.3;
		double beta=0.5;
		double maxTime=10;
		TimeWeightedFunction func = new TimeWeightedPolynomial(k, beta, maxTime);
		TimeWeightedFunction func2 = new TimeWeightedExponential(k, beta, maxTime);
		//tactic for customer
		Tactic timedep = new TimeDependentTactic(func, false);
		Tactic timedep2 = new TimeDependentTactic(func2, false);
		Tactic behdep= new BehaviourDependentTactic(rel, 2);
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(timedep, new Double(0.2));
		tactics.put(timedep2, new Double(0.2));
		tactics.put(behdep, new Double(0.6));
		
		try {
			strat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		//check if issue added successfully
		assertTrue("Adding issue should succeed",strat.generateOfferValue(issue));
		//with no history should work as expected
		assertEquals("21.6 should be returned",21.6, strat.getCurrentVal(),Delta);
		
		//add history and check operation
		ret1.put(Item.PRICE, new Double(29));
		cust.put(Item.PRICE, new Double(20));
		th.addOffer(o1);
		th.addOffer(c1);
		
		//next iteration
		ret1.put(Item.PRICE, new Double(27));
		cust.put(Item.PRICE, new Double(21));
		th.addOffer(o1);
		th.addOffer(c1);
		
		//next iteration
		ret1.put(Item.PRICE, new Double(25));
		cust.put(Item.PRICE, new Double(21.5));
		th.addOffer(o1);
		th.addOffer(c1);
//		
//		//next iteration
		ret1.put(Item.PRICE, new Double(24));		
		th.addOffer(o1);
		
		issue.incIteration();
		strat.generateOfferValue(issue);
		assertEquals("test with history is",22.634,strat.getCurrentVal(),Delta);
	}
	
	
	

}
