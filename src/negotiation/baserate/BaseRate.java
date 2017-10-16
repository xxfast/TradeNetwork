package negotiation.baserate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import jade.core.AID;
import java.util.*;

import negotiation.Transaction;

public abstract class BaseRate {
		
	// How for the object is willing to look back in the transaction history
	private int lookBackLength;
	private double[] baseRate = new double[24]; 
	private Queue<Transaction> transactionHistory = new LinkedList<Transaction>();
	
	// Constructor
	public BaseRate(String baseRateDir, int lookBackLength) {
		loadBaseRate(baseRateDir);	
		this.lookBackLength = lookBackLength;
	}
	
	// Default constructor
	public BaseRate() {
		this("baserate.txt", 30);
	}
	
	// Prints the base rate array in easily readable config
	public void printBaseRate () {
		for (int i = 0; i < 24; i++) {
			System.out.println(String.format("Rate at %d:00 = $%f", i, baseRate[i]));
		}
	}
	
	// Load the base rate array from the passed directory
	public void loadBaseRate(String dir) {
        String line = null;

        try {
            FileReader reader = new FileReader(dir);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int i = 0;
                        
            while((line = bufferedReader.readLine()) != null) {
            	try {
            		baseRate[i] = Double.parseDouble(line);
            	}
            	catch(NumberFormatException ex) {
                    System.out.println(String.format("Unable to convert value \"%s\"", line));                
            	}
            	i++;
            }   

            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(String.format("Unable to open file \"%s\"", dir));                
        }
        catch(IOException ex) {
            System.out.println(String.format("Error reading file \"%s\"", dir));                
        }
	}
	
	public int getLookBackLength() {
		return this.lookBackLength;
	}
	
	public double[] getBaseRate() {
		return this.baseRate;
	}
	
	public Queue<Transaction> getTransactionHistory() {
		return this.transactionHistory;
	}
	
	// Calculate inital rate for the initiator/responder to use
	public abstract double calc (AID id, int units, int time);
	
	// Discount based on amount of transactions in history
	public abstract double getAIDHistoryDiscount (int transactions);
	
	// Discount based on amount of units traded in history
	public abstract double getUnitHistoryDiscount (int units);
	
	// Discount based on  amount of units traded in this trade
	public abstract double getUnitAmountDiscount (int units);

}