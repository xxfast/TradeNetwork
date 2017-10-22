package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import FIPA.DateTime;
import model.Demand;
import model.Offer;
import negotiation.Strategy.Item;

public class TestModel {

	@Test
	public void TestOffer() {
		Map<Item,Double> ret1= new HashMap<Item,Double>();
		ret1.put(Item.PRICE, new Double(29));
		ret1.put(Item.DURATION, new Double(20));
		
		
	
		
		Demand dem = new Demand(5,(short)2,3);
		
		Offer o1= new Offer(ret1);
		
		o1.setDemand(dem);
		o1.setOwner("ret1");
		
//		System.out.println("offer "+o1.getContent());
		
		Offer o2 = new Offer();
		o2.setContent(o1.getContent());
//		System.out.println("copy "+o2.getContent());
	}

}
