package model;

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
	
	public History() {
		
	}
	
	public void addTransaction (AID client, int units, double rate) {
		
		if (transactionHistory.containsKey(client)) {
			transactionHistory.get(client).getTransactions().add(new Transaction(units, rate));
		} else {
			TransactionList TL = new TransactionList();
			TL.getTransactions().add(new Transaction(units, rate));
			transactionHistory.put(client, TL);
		}
		
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