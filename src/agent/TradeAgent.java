package agent;

import jade.core.Agent;

public class TradeAgent extends Agent {

	protected void setup() {
		System.out.println(getDescription() + ": " + "initialising");
	}
	
	public String getDescription(){
		return getLocalName();
	}
}
