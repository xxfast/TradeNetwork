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

import negotiation.baserate.BoundCalc;
import negotiation.baserate.Transaction;
import negotiation.baserate.TransactionList;

public class History {

	private Map<String, TransactionList> transactionHistory = new HashMap<String, TransactionList>();
	private String id;

	public History(String id) {
		this.id = id;
		loadTransactionHistory();
	}

	public void addTransaction(String client, int units, double rate, int rounds) {

		if (transactionHistory.containsKey(client)) {
			transactionHistory.get(client).getTransactions().add(new Transaction(units, rate, rounds));

		} else {
			TransactionList TL = new TransactionList();
			TL.getTransactions().add(new Transaction(units, rate, rounds));
			transactionHistory.put(client, TL);

		}
	}

	public void addTransaction(String client, Transaction transaction) {
		this.addTransaction(client, transaction.getUnits(), transaction.getRate(), transaction.getRounds());
	}

	private void loadTransactionHistory() {
		String dir = BoundCalc.DEFAULT_LOAD_LOCATION + "history/";
		dir += this.id + ".txt";

		String line = null;

		try {
			FileReader reader = new FileReader(dir);
			BufferedReader bufferedReader = new BufferedReader(reader);

			int clientNum = Integer.parseInt(bufferedReader.readLine());
			// System.out.println("CN: " + clientNum);

			for (int i = 0; i < clientNum; i++) {

				String client = bufferedReader.readLine();
				int transactionNum = Integer.parseInt(bufferedReader.readLine());

				for (int j = 0; j < transactionNum; j++) {
					String[] transaction = bufferedReader.readLine().split(",");
					this.addTransaction(client, Integer.parseInt(transaction[0]), Double.parseDouble(transaction[1]),
							Integer.parseInt(transaction[2]));
				}
			}

			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println(String.format("Unable to open file \"%s\"", dir));
		} catch (IOException ex) {
			System.out.println(String.format("Error reading file \"%s\"", dir));
		}

	}

	public void saveTransactionHistory() {
		String dir = BoundCalc.DEFAULT_LOAD_LOCATION + "history/";
		dir += this.id + ".txt";
		try {
			FileWriter fileWriter = new FileWriter(dir);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			String size = Integer.toString(transactionHistory.size());

			bufferedWriter.write(size);
			bufferedWriter.newLine();

			for (Map.Entry<String, TransactionList> entry : transactionHistory.entrySet()) {

				bufferedWriter.write(entry.getKey());
				bufferedWriter.newLine();

				size = Integer.toString(entry.getValue().getTransactions().size());
				bufferedWriter.write(size);
				bufferedWriter.newLine();

				for (Transaction t : entry.getValue().getTransactions()) {

					bufferedWriter.write(String.format("%d,%f,%d", t.getUnits(), t.getRate(), t.getRounds()));
					bufferedWriter.newLine();
				}
			}
			bufferedWriter.close();
		} catch (IOException ex) {
			System.out.println(String.format("Error writing to file \"%s\"", dir));
		}
	}

	public double getTotalMoneyTradedForClient(String id) {
		if (transactionHistory.containsKey(id)) {
			return transactionHistory.get(id).getTotalMoneyPayed();
		} else {
			return 0;
		}
	}

	public int getTotalUnitsTradedForClient(String id) {
		if (transactionHistory.containsKey(id)) {
			return transactionHistory.get(id).getTotalUnitsTraded();
		} else {
			return 0;
		}
	}

	public int getTotalTransactionsForClient(String id) {
		if (transactionHistory.containsKey(id)) {
			return transactionHistory.get(id).getTotalTransactions();
		} else {
			return 0;
		}
	}

	public Map<String, TransactionList> getTransactionHistory() {
		return transactionHistory;
	}
}