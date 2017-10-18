package negotiation.baserate;

import jade.core.AID;

public class HomeBound extends BoundCalc {
	public HomeBound(String baseRateDir) {
		super(baseRateDir);
	}
	
	public HomeBound() {
		super();
	}

	@Override
	public double[] calcBounds(AID id, int units, int time) {
		double lowerBound = getStdRate()[time];
		double upperBound = getStdRate()[time];
		
		int pastUnitTransactions = this.getHistory().getTotalUnitsTradedForClient(id);
		double pastMoneyTransactions = this.getHistory().getTotalMoneyTradedForClient(id);
		int pastTransactions = this.getHistory().getTotalTransactionsForClient(id);
		
		// To make the home initially ask less than the standard.
		// TODO Think of appropriate value here
		lowerBound *= 0.6;
		
		// Home reduces the initial asking price based on the amount of units
		// Home probably wouldn't calculate this
		//initial *= (1-getUnitAmountDiscount(units));
		
		// Home reduces the initial asking price based on amount of units traded in history
		// Home probably wouldn't record this
		//initial *= (1-getUnitHistoryDiscount(pastUnitTransactions));
		
		// Home reduces the initial asking price based on amount of transactions with agent in history
		lowerBound *= (1-getAIDHistoryDiscount(pastTransactions));
		
		
		// Increases the upper bound to 110% to get the the max the home is willing to pay
		// TODO Think of appropriate value here
		upperBound *= 1.1;
		
		// Add the transaction to memory
		this.getHistory().newTransaction(id, units);	
		
		return new double[] {lowerBound, upperBound};
	}

	// Discount based on  amount of units traded in this trade
	// Home probably wont consider this - it's not a company
	@Override
	public double getUnitAmountDiscount (int units) {
		return 0;
	}
	
	// Discount based on amount of units traded in history
	// Home probably wont recall this
	@Override
	public double getUnitHistoryDiscount (int units) {
		return 0;
	}
	
	// Discount based on amount of transactions in history
	@Override
	public double getAIDHistoryDiscount (int transactions) {
		if (transactions >= 10) {
			return 0.10;
		} else if (transactions >= 5) {
			return 0.05;
		} else if (transactions > 0) {
			return 0.025;
		} else {
			return 0;
		}
	}
}
