package negotiation.baserate;

import jade.core.AID;
import negotiation.Transaction;

public class RetailerBaseRate extends BaseRate {
	public RetailerBaseRate(String baseRateDir, int lookBackLength) {
		super(baseRateDir, lookBackLength);
	}
	
	public RetailerBaseRate() {
		super();
	}
	
	// Assuming retailer
	@Override
	public double calc (AID id, int units, int time) {
		double initial = getBaseRate()[time];
		int pastUnitTransactions = 0;
		int pastTransactions = 0;
		
		// Search history for previous transactions with Agent
		for(Transaction t: getTransactionHistory()) {
			if (t.getClient().getName() == id.getName()) {
				pastTransactions += 1;
				pastUnitTransactions += t.getUnits();
			}
		}
		
		// To make the retailer initially ask for more than the standard.
		// TODO Think of appropriate value here
		initial *= 1.1;			
		
		// Retailer reduces the initial asking price based on the amount of units
		initial *= (1-getUnitAmountDiscount(units));
		
		// Retailer reduces the initial asking price based on amount of units traded in history
		initial *= (1-getUnitHistoryDiscount(pastUnitTransactions));
		
		// Retailer reduces the initial asking price based on amount of transactions with agent in history
		//initial *= (1-getAIDHistoryDiscount(pastTransactions));
			
		// Remove the last most transaction from memory
		if (getTransactionHistory().size() > getLookBackLength()) {
			getTransactionHistory().poll();
		}
		
		// Add the transaction to memory
		getTransactionHistory().add(new Transaction(id, units));
		
		return initial;
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
