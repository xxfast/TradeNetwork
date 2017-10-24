package negotiation.baserate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import jade.core.AID;
import model.History;

import java.util.*;

public abstract class BoundCalc {
	
	public final static String DEFAULT_LOAD_LOCATION = System.getProperty("user.dir") + "/resources/data/" ; 
		
	private double[] stdRate = new double[24]; 
	protected History hist;
	// Constructor
	public BoundCalc(String baseRateDir,History hist) {
		loadBaseRate(baseRateDir );
		this.hist=hist;
	}
	
	// Default constructor
	public BoundCalc(History history) {
		this(DEFAULT_LOAD_LOCATION + "baserate.txt", history);
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
			
	public double[] getStdRate() {
		return this.stdRate;
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