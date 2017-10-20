package negotiation.baserate;

import java.util.*;
import negotiation.baserate.Transaction;

public class TransactionList {
	private List<Transaction> transactions = new ArrayList<Transaction>();
	
	public TransactionList() {
		
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	public int getTotalTransactions () {
		return transactions.size();
	}
	
	public int getTotalUnitsTraded() {
		int total = 0;
		for(Transaction t: transactions) {
			total += t.getUnits();
		}
		
		return total;
	}
	
	public double getTotalMoneyPayed() {
		double total = 0;
		for(Transaction t: transactions) {
			total += t.getUnits()*t.getRate();
		}
		
		return total;
	}
}
