package test;

import static org.junit.Assert.*;

import org.junit.Test;

import negotiation.Issue;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.TimeWeightedExponential;
import negotiation.tactic.TimeWeightedPolynomial;

public class TestTimeDependentTactics {
	public final double Delta=0.001;

	@Test
	public void TestPolynomial() {
		double k=0.3;
		double beta=0.5;
		double maxTime=10;
		TimeWeightedPolynomial poly = new TimeWeightedPolynomial(k, beta, maxTime);
		double time=3;
		double ans=0.363;	
		
		assertEquals(ans, poly.getValue(time), Delta);
		
		time=0;
		
		assertEquals("Time 0 should give K->"+k,k, poly.getValue(time),Delta);
		
		time=10;
		
		assertEquals("Time 10 should give 1",1, poly.getValue(time),Delta);
	}
	
	@Test
	public void TestExponential() {
		double k=0.3;
		double beta=0.5;
		double maxTime=10;
		TimeWeightedExponential poly = new TimeWeightedExponential(k, beta, maxTime);
		double time=3;
		double ans=0.365;	
		
		assertEquals(ans, poly.getValue(time), Delta);
		time=0;
		
		assertEquals("Time 0 should give K->"+k,k, poly.getValue(time),Delta);
		
		time=10;
		
		assertEquals("Time 10 should give 1",1, poly.getValue(time),Delta);
	}
	
	@Test
	public void TestTacticExponentialWeight() {
		double k=0.3;
		double beta=0.5;
		double maxTime=5;
		TimeWeightedExponential poly = new TimeWeightedExponential(k, beta, maxTime);
		
		//testing supplier version
		TimeDependentTactic supplier = new TimeDependentTactic(poly, true);
		
		Issue issue= new Issue(0, 30, 18);
		double oldVal=0;
		double newVal=30;
		for(int i=0;i<=maxTime;i++)
		{
			oldVal=newVal;
			newVal=supplier.nextValue(issue);
			assertTrue("all values betwwen 18 and 30",newVal<=30 && newVal>=18);
			assertTrue("newVal always lower than oldVal",newVal<=oldVal);
			
			issue.incIteration();
		}
		assertEquals("Final Supplier val is 18",18,newVal,Delta);
		
		//testing customer version
		TimeDependentTactic customer = new TimeDependentTactic(poly, false);
		
		 issue= new Issue(0, 30, 18);
		 oldVal=0;
		 newVal=18;
		for(int i=0;i<=maxTime;i++)
		{
			oldVal=newVal;
			newVal=customer.nextValue(issue);
			assertTrue("all values betwwen 18 and 30",newVal<=30 && newVal>=18);
			
			assertTrue("newVal always higher than oldVal",newVal>=oldVal);
			
			issue.incIteration();
		}
		assertEquals("Final Customer val is 30",30,newVal,Delta);
		
		
		
	}
	
	@Test
	public void TestTacticPolynomialWeight() {
		double k=0.3;
		double beta=0.5;
		double maxTime=5;
		TimeWeightedPolynomial poly = new TimeWeightedPolynomial(k, beta, maxTime);
		
		//testing supplier version
		TimeDependentTactic supplier = new TimeDependentTactic(poly, true);
		
		Issue issue= new Issue(0, 30, 18);
		double oldVal=0;
		double newVal=30;
		for(int i=0;i<=maxTime;i++)
		{
			oldVal=newVal;
			newVal=supplier.nextValue(issue);
			assertTrue("all values betwwen 18 and 30",newVal<=30 && newVal>=18);
			assertTrue("newVal always lower than oldVal",newVal<=oldVal);
			
			issue.incIteration();
		}
		assertEquals("Final Supplier val is 18",18,newVal,Delta);
		
		//testing customer version
		TimeDependentTactic customer = new TimeDependentTactic(poly, false);
		
		 issue= new Issue(0, 30, 18);
		 oldVal=0;
		 newVal=18;
		for(int i=0;i<=maxTime;i++)
		{
			oldVal=newVal;
			newVal=customer.nextValue(issue);
			assertTrue("all values betwwen 18 and 30",newVal<=30 && newVal>=18);
			
			assertTrue("newVal always higher than oldVal",newVal>=oldVal);
			
			issue.incIteration();
		}
		assertEquals("Final Customer val is 30",30,newVal,Delta);
		
		
		
	}

}
