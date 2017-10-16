package negotiation;

import jade.core.AID;

public class Transaction {
    private AID client; 
    private int units; 
	
	public Transaction(AID client, int units) {
		this.client = client;
		this.units = units;
	}
	
	public AID getClient() {
		return client;
	}
	
	public int getUnits() {
		return units;
	}
}
