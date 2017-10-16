package negotiation.baserate;

import jade.core.AID;
import negotiation.Transaction;

public class HomeBaseRate extends BaseRate {
	public HomeBaseRate(String baseRateDir, int lookBackLength) {
		super(baseRateDir, lookBackLength);
	}
	
	public HomeBaseRate() {
		super();
	}

	@Override
	public double calc(AID id, int units, int time) {
		double initial = super.getBaseRate()[time];
		int pastUnitTransactions = 0;
		int pastTransactions = 0;
		
		// Search history for previous transactions with Agent
		for(Transaction t: getTransactionHistory()) {
			if (t.getClient().getName() == id.getName()) {
				pastTransactions += 1;
				pastUnitTransactions += t.getUnits();
			}
		}
		
		// To make the home initially ask less than the standard.
		// TODO Think of appropriate value here
		initial *= 0.9;			
		
		// Home reduces the initial asking price based on the amount of units
		// Home probably wouldn't calculate this
		//initial *= (1-getUnitAmountDiscount(units));
		
		// Home reduces the initial asking price based on amount of units traded in history
		// Home probably wouldn't record this
		//initial *= (1-getUnitHistoryDiscount(pastUnitTransactions));
		
		// Home reduces the initial asking price based on amount of transactions with agent in history
		initial *= (1-getAIDHistoryDiscount(pastTransactions));
		
		// Remove the last most transaction from memory
		if (getTransactionHistory().size() > getLookBackLength()) {
			getTransactionHistory().poll();
		}
		
		// Add the transaction to memory
		getTransactionHistory().add(new Transaction(id, units));
		
		return initial;
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
