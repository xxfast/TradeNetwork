package test;

import static org.junit.Assert.*;

import org.junit.Test;

import negotiation.Issue;
import negotiation.tactic.ResourceDependentTactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.timeFunction.ResourceAgentsFunction;
import negotiation.tactic.timeFunction.ResourceTimeFunction;
import negotiation.tactic.timeFunction.TimeWeightedExponential;
import negotiation.tactic.timeFunction.TimeWeightedPolynomial;

public class TestResourceDependentTactics {
	public final double Delta=0.001;

	@Test
	public void TestTacticResourceAgents() {
		double k=0.3;
		int activeAgents=6;
		double maxTime=5;
		ResourceAgentsFunction resrc = new ResourceAgentsFunction(k,activeAgents);
		
		//testing supplier version
		ResourceDependentTactic supplier = new ResourceDependentTactic(resrc, true);
		
		Issue issue= new Issue(0, 30, 18);
		double oldVal=0;
		double newVal=30;
		for(int i=0;i<=maxTime;i++)
		{
			oldVal=newVal;
			newVal=supplier.nextValue(issue);
			assertTrue("all values betwwen 18 and 30",newVal<=30 && newVal>=18);
			assertTrue("newVal always lower than oldVal",newVal<=oldVal);
		
			resrc.setActiveAgents(--activeAgents);
			issue.incIteration();
		}
		newVal=supplier.nextValue(issue);
		assertEquals("Final Supplier val is 18",18,newVal,Delta);
		
		//testing customer version
		ResourceDependentTactic customer = new ResourceDependentTactic(resrc, false);
		
		 activeAgents=6;
		 resrc.setActiveAgents(activeAgents);
		 issue= new Issue(0, 30, 18);
		 oldVal=0;
		 newVal=18;
		 
		for(int i=0;i<=maxTime;i++)
		{
			oldVal=newVal;
			newVal=customer.nextValue(issue);
			assertTrue("all values betwwen 18 and 30",newVal<=30 && newVal>=18);
			assertTrue("newVal always higher than oldVal",newVal>=oldVal);
			resrc.setActiveAgents(--activeAgents);
			issue.incIteration();
		}
		newVal=customer.nextValue(issue);
		assertEquals("Final Customer val is 30",30,newVal,Delta);
		
		
		
	}
	
	@Test
	public void TestTacticResourceTime() {
		double k=0.3;
	
		double maxTime=5;
		ResourceTimeFunction time = new ResourceTimeFunction(k, maxTime);
		
		//testing supplier version
		ResourceDependentTactic supplier = new ResourceDependentTactic(time, true);
		
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
		ResourceDependentTactic customer = new ResourceDependentTactic(time, false);
		
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
