package test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import model.History;
import negotiation.baserate.TransactionList;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHistory {

	@Test
	public void TestASavingHistory() {
		History hist = new History("AgentX");
		hist.getTransactionHistory().clear();
		hist.addTransaction("opponent 1", 25, 37,6);
		hist.addTransaction("opponent 1", 40, 36,7);
		hist.saveTransactionHistory();
		
	}
	@Test
	public void TestBLoadingHistory() {
		History hist = new History("AgentX");
		Map<String,TransactionList> trans=hist.getTransactionHistory();
		for(Map.Entry<String, TransactionList> entry:trans.entrySet())
		{
			assertEquals("opponent 1",entry.getKey());
			assertEquals(6, entry.getValue().getTransactions().get(0).getRounds());
			assertEquals(7, entry.getValue().getTransactions().get(1).getRounds());
			
		}
	
		
		
	}

}
