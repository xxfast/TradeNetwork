package negotiation.baserate;

import model.History;
import jade.core.AID;

public class RetailerBound extends BoundCalc {
	public RetailerBound(String baseRateDir) {
		super(baseRateDir);
	}
	
	public RetailerBound() {
		super();
	}
	
	// Assuming retailer
	@Override
	public double[] calcBounds (AID id, int units, int time, History hist){
		double lowerBound = getStdRate()[time];
		double upperBound = getStdRate()[time];
		
		int pastUnitTransactions = hist.getTotalUnitsTradedForClient(id.getName());
		double pastMoneyTransactions = hist.getTotalMoneyTradedForClient(id.getName());
		int pastTransactions = hist.getTotalTransactionsForClient(id.getName());
		
		// To make the retailer initially asks for more than the standard.
		// TODO Think of appropriate value here
		upperBound *= 1.4;			
		
		// Retailer reduces the initial asking price based on the amount of units
		upperBound *= (1-getUnitAmountDiscount(units));
		
		// Retailer reduces the initial asking price based on amount of units traded in history
		upperBound *= (1-getUnitHistoryDiscount(pastUnitTransactions));
		
		// Retailer reduces the initial asking price based on amount of transactions with agent in history
		//upperBound *= (1-getAIDHistoryDiscount(pastTransactions));
			
		// Reduces the lowerbound to 80% to get the minimum the retailer is willing to receive
		// TODO Think of appropriate value here
		lowerBound *= 0.9;
		
		// Add the transaction to memory

		return new double[] {lowerBound, upperBound};
	}
	
	// Discount based on  amount of units traded in this trade
	@Override
	public double getUnitAmountDiscount (int units) {
		if (units > 1000) {
			return 0.20;
		} else if (units > 500) {
			return 0.10;
		} else if (units > 250) {
			return 0.05;
		} else if (units > 100) {
			return 0.025;
		} else {
			return 0;
		}
	}
	
	// Discount based on amount of units traded in history
	@Override
	public double getUnitHistoryDiscount (int units) {
		if (units > 10000) {
			return 0.20;
		} else if (units > 3000) {
			return 0.10;
		} else if (units > 1000) {
			return 0.05;
		} else if (units > 500) {
			return 0.025;
		} else {
			return 0;
		}
	}
	
	// Discount based on amount of transactions in history
	@Override
	public double getAIDHistoryDiscount (int transactions) {
		if (transactions > 25) {
			return 0.20;
		} else if (transactions > 15) {
			return 0.10;
		} else if (transactions > 8) {
			return 0.05;
		} else if (transactions > 3) {
			return 0.025;
		} else if (transactions > 1) {
			return 0.010;
		} else {
			return 0;
		}
	}
}
