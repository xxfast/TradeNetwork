package negotiation.baserate;

import jade.core.AID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Exception;
import negotiation.baserate.Transaction;
import negotiation.baserate.TransactionList;

public class History {
	
	private Map<AID,TransactionList> transactionHistory = new HashMap<AID,TransactionList>();
	private Transaction currentTransaction = new Transaction();
	private AID currentClient;
	private boolean finalRateAdded = true;
	
	public History() {
		
	}
	
	public void newTransaction (AID client, int units) {
		
		if (!finalRateAdded){
			System.out.println("The previous transaction hasn't been updated with the final rate decided in the negotiation. Call updateRate() to add the rate");
		}

		finalRateAdded = false;
		
		this.currentTransaction = new Transaction();
		this.currentClient = client;
		
		currentTransaction.setUnits(units);
	}
	
	public void saveTransactionHistory (String dir) {
        try {
            FileWriter fileWriter = new FileWriter(dir);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            bufferedWriter.write(transactionHistory.size());
            bufferedWriter.newLine();
            
            for (Map.Entry<AID,TransactionList> entry : transactionHistory.entrySet()) {
            	
                bufferedWriter.write(entry.getKey().getName());
                bufferedWriter.newLine();

                for (Transaction t: entry.getValue().getTransactions()) {
                    bufferedWriter.write(String.format("%d,%f", t.getUnits(), t.getRate()));
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(String.format("Error writing to file \"%s\"", dir));                
        }
	}
	
	public void updateFinalRate (double rate) {		
		
		if (!finalRateAdded) {
			currentTransaction.setRate(rate);
			finalRateAdded = true;

			if (transactionHistory.containsKey(currentClient)) {
				transactionHistory.get(currentClient).getTransactions().add(currentTransaction);
			} else {
				TransactionList TL = new TransactionList();
				TL.getTransactions().add(currentTransaction);
				transactionHistory.put(this.currentClient, TL);
			}
		}
		
		
	}
	
	public double getTotalMoneyTradedForClient(AID id) {
		if (transactionHistory.containsKey(id)) {
			return transactionHistory.get(id).getTotalMoneyPayed();
		} else {
			return 0;
		}
	}
	
	public int getTotalUnitsTradedForClient(AID id) {
		if (transactionHistory.containsKey(id)) {
			return transactionHistory.get(id).getTotalUnitsTraded();
		} else {
			return 0;
		}
	}
	
	public int getTotalTransactionsForClient (AID id) {
		if (transactionHistory.containsKey(id)) {
			return transactionHistory.get(id).getTotalTransactions();
		} else {
			return 0;
		}
	}
}