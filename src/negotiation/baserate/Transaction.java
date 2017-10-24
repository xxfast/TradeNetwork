package negotiation.baserate;

import jade.core.AID;

public class Transaction {
    private int units;
    private double rate;
    private int rounds;
	
	public Transaction() {
	}

	
	/**
	 * @param units
	 * @param rate
	 * @param rounds
	 */
	public Transaction(int units, double rate, int rounds) {
		this.units = units;
		this.rate = rate;
		this.rounds = rounds;
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


	public int getRounds() {
		return rounds;
	}


	public void setRounds(int rounds) {
		this.rounds = rounds;
	}


	@Override
	public String toString() {
		return "Transaction [units=" + units + ", rate=" + rate + ", rounds=" + rounds + "]";
	}
	
	
}
