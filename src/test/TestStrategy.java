package test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import negotiation.Issue;
import negotiation.Strategy;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.TimeWeightedFunction;
import negotiation.tactic.TimeWeightedPolynomial;
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
	
	
	

}
