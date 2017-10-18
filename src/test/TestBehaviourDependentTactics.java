package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import model.Offer;
import negotiation.Issue;
import negotiation.NegotiationThread;
import negotiation.Strategy.Item;
import negotiation.tactic.behaviour.AverageTitForTat;
import negotiation.tactic.behaviour.RelativeTitForTat;

public class TestBehaviourDependentTactics {
	public final double Delta=0.001;
	@Test
	public void TestRelativeTitForTat() {
		NegotiationThread th = new NegotiationThread();
		
		RelativeTitForTat rel = new RelativeTitForTat( Item.PRICE,th);
		Issue issue= new Issue(30, 18);
		double val=rel.getCounterValue(3, issue);
		
		assertEquals("no counter generated since insufficient hostory",0,val,Delta);
		
		//testing for customer
		//add some dummy history
		
		Map<Item,Double> ret1= new HashMap<Item,Double>();
		ret1.put(Item.PRICE, new Double(29));
		
		Offer o1= new Offer(ret1);
		o1.setOwner("ret1");
		
		Map<Item,Double> cust= new HashMap<Item,Double>();
		cust.put(Item.PRICE, new Double(20));
		
		Offer c1= new Offer(cust);
		c1.setOwner("cust");
		
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
		
		val=rel.getCounterValue(1, issue);
		double exp=((double)25/(double)24)*21.5;
		
		
		assertEquals("should be same as 23.22", exp,val,Delta);
		
		
		
		exp=((double)27/(double)25)*21.5;
		val=rel.getCounterValue(2, issue);
		assertEquals("should be same ", exp,val,Delta);
		
	}
	@Test
	public void TestAverageTitForTat() {
		NegotiationThread th = new NegotiationThread();
		double val=0;
		AverageTitForTat rel = new AverageTitForTat( Item.PRICE,th);
		Issue issue= new Issue(30, 18);
		val=rel.getCounterValue(1, issue);
		
		assertEquals("no counter generated since insufficient hostory",0,val,Delta);
		
		//testing for customer
		//add some dummy history
		
		Map<Item,Double> ret1= new HashMap<Item,Double>();
		ret1.put(Item.PRICE, new Double(29));
		
		Offer o1= new Offer(ret1);
		o1.setOwner("ret1");
		
		Map<Item,Double> cust= new HashMap<Item,Double>();
		cust.put(Item.PRICE, new Double(20));
		
		Offer c1= new Offer(cust);
		c1.setOwner("cust");
		
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
		
		double exp=((double)25/(double)24)*21.5;
//		System.out.println(th.toString());
		val=rel.getCounterValue(1, issue);
		
		assertEquals("should be same ", exp,val,Delta);
		
		exp=((double)27/(double)24)*21.5;
		val=rel.getCounterValue(2, issue);
//		System.out.println("val "+val);
		assertEquals("should be same ", exp,val,Delta);
		
		exp=((double)29/(double)24)*21.5;
		val=rel.getCounterValue(3, issue);
//		System.out.println("val "+val);
		assertEquals("should be same ", exp,val,Delta);
		
	}

}
