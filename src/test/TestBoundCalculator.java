package test;

import static org.junit.Assert.*;

import org.junit.Test;

import jade.core.AID;
import model.History;
import negotiation.baserate.HomeBound;

public class TestBoundCalculator {

	@Test
	public void TestHome() {
		History hist= new History();
		HomeBound bound = new HomeBound(hist);
//		 double[] range=bound.calcBounds(new AID("ret1", false), 150, 14);
//		 for(double val :range)
//			 System.out.println("range "+val);
	}

}
