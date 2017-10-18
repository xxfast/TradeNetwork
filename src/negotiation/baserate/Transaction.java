package negotiation.baserate;

import jade.core.AID;

public class Transaction {
    private int units;
    private double rate;
	
	public Transaction() {
	}
	
	public void setUnits(int units) {
		this.units = units;
	}
	
	public int getUnits() {
		return units;
	}
	
	public void setRate(double finalRate) {
		this.rate = finalRate;
	}

	public double getRate() {
		return this.rate;
	}
}
