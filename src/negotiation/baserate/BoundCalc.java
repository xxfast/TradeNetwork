package negotiation.baserate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import jade.core.AID;
import java.util.*;

public abstract class BoundCalc {
		
	private double[] stdRate = new double[24]; 
	private History history = new History();
	
	// Constructor
	public BoundCalc(String baseRateDir) {
		loadBaseRate(baseRateDir);
	}
	
	// Default constructor
	public BoundCalc() {
		this("src\\negotiation\\baserate\\baserate.txt");
	}
	
	// Prints the base rate array in easily readable config
	public void printStdRate () {
		for (int i = 0; i < 24; i++) {
			System.out.println(String.format("Rate at %d:00 = $%f", i, stdRate[i]));
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
            		stdRate[i] = Double.parseDouble(line);
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
	
	public void saveHistory(String dir) {
		history.saveTransactionHistory(dir);
	}
	
	public void updateRate(double rate) {
		history.updateFinalRate(rate);
	}
			
	public double[] getStdRate() {
		return this.stdRate;
	}
	
	public History getHistory() {
		return this.history;
	}
	
	// Calculate inital rate for the initiator/responder to use
	public abstract double[] calcBounds (AID id, int units, int time);
	
	// Discount based on amount of transactions in history
	public abstract double getAIDHistoryDiscount (int transactions);
	
	// Discount based on amount of units traded in history
	public abstract double getUnitHistoryDiscount (int units);
	
	// Discount based on  amount of units traded in this trade
	public abstract double getUnitAmountDiscount (int units);

}