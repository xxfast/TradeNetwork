import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import agent.ApplianceAgent;
import agent.HomeAgent;
import controllers.TradeAgentController;
import descriptors.ApplianceAgentDescriptor;
import descriptors.HeaterAgentDescriptor;
import descriptors.HomeAgentDescriptor;
import descriptors.RefrigeratorAgentDescriptor;
import descriptors.RetailerAgentDescriptor;
import descriptors.SchedulingAgentDescriptor;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import model.Demand;
import negotiation.tactic.Tactic;
import simulation.Simulation;
import ui.SimulationInspecter;

/**
 * The main program of TradeNetwork
 * This starts up the UI instantiates the SimulationAdapter
 * @author Isuru
 */
public class MainProgram {
	
	public static final String DEFAULT_SAVE_LOCATION = "/data/test.tns";	
	
	public static String consoleOutput = "";
	
	public static AgentContainer container;
	
	public static Simulation toRun;
	
	public static void main(String[] args)  {
		// Get a hold to the JADE runtime
		Runtime rt = Runtime.instance();
		
		// Launch the Main Container (with the administration GUI on top) listening on port 8888
		say(": Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null); 
		pMain.setParameter(Profile.GUI, "false");
		
		// Get the Agent container
		container = rt.createMainContainer(pMain);
		
		try {
		// Set a default simulation
		toRun = CreateDefaultSimulation();
		}catch(StaleProxyException e) {
			
		}
		
		// Start the main UI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationInspecter window = new SimulationInspecter();
					window.setToInspect(toRun);
					window.UpdateModel();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static Simulation CreateDefaultSimulation() throws StaleProxyException {
		Simulation test = new Simulation();
		test.setName("Test Simulation");
		test.setDescription("This is a test save");
		test.setContainer(container);
		
		// Create a agent of class HomeAgent 
		say("Starting up a HomeAgent...");
		HomeAgentDescriptor myHome1Agent = new HomeAgentDescriptor();
		myHome1Agent.setName("Home1");
		myHome1Agent.setMaxNegotiationTime(6);
		myHome1Agent.setParamK(0.01);
		myHome1Agent.setParamBeta(0.5);
		myHome1Agent.setTacticType(Tactic.Type.TIMEDEPENDENT);
		test.CreateTradeAgent(myHome1Agent);
		
//		// Create a agent of class HomeAgent 
//		say("Starting up a Home2Agent...");
//		HomeAgentDescriptor myHome2Agent = new HomeAgentDescriptor();
//		myHome2Agent.setName("Home2");
//		myHome2Agent.setMaxNegotiationTime(6);
//		myHome2Agent.setParamK(0.01);
//		myHome2Agent.setParamBeta(0.9);
//		myHome2Agent.setTacticType(Tactic.Type.TIMEDEPENDENT);
//		test.CreateTradeAgent(myHome2Agent);
		
		//Create a agent of class ApplianceAgent 
		say("Starting up a FridgeApplianceAgent...");
		RefrigeratorAgentDescriptor myFridge1Agent = new RefrigeratorAgentDescriptor();
		myFridge1Agent.setName("Fridge1");
		myFridge1Agent.setOwner(new AID(myHome1Agent.getName(), AID.ISLOCALNAME));
		myFridge1Agent.setStartingDemand(new Demand(1));
		myFridge1Agent.setEnergyUsage(1);
		test.CreateTradeAgent(myFridge1Agent);
		
//		//Create a agent of class ApplianceAgent 
//		say("Starting up a FridgeApplianceAgent...");
//		RefrigeratorAgentDescriptor myFridge2Agent = new RefrigeratorAgentDescriptor();
//		myFridge2Agent.setName("Fridge2");
//		myFridge2Agent.setOwner(new AID(myHome2Agent.getName(), AID.ISLOCALNAME));
//		myFridge2Agent.setStartingDemand(new Demand(1));
//		myFridge2Agent.setEnergyUsage(1);
//		test.CreateTradeAgent(myFridge2Agent);
		
		// Create a agent of class RetailerAgent 
		say("Starting up a RetailerAgent...");
		RetailerAgentDescriptor myRetailerAgent = new RetailerAgentDescriptor();
		myRetailerAgent.setName("SimpleEnergy");

		myRetailerAgent.setMaxNegotiationTime(10);
		myRetailerAgent.setParamK(0.01);
		myRetailerAgent.setParamBeta(0.5);
		myRetailerAgent.setTacticType(Tactic.Type.COMBINATION);
		test.CreateTradeAgent(myRetailerAgent);
		

		// Create a agent of class RetailerAgent 
		say("Starting up a Retailer2Agent...");
		RetailerAgentDescriptor myRetailer2Agent = new RetailerAgentDescriptor();
		myRetailer2Agent.setName("AGL");

		myRetailer2Agent.setMaxNegotiationTime(9);
		myRetailer2Agent.setParamK(0.6);
		myRetailer2Agent.setParamBeta(0.1);
		myRetailer2Agent.setTacticType(Tactic.Type.COMBINATION);
		test.CreateTradeAgent(myRetailer2Agent);
		
		return test;
	}
	
	public static void Save() {
		say("Saving simulation to " + DEFAULT_SAVE_LOCATION);
		 try {
			 File savedFile = new File(DEFAULT_SAVE_LOCATION);
			 savedFile.createNewFile();  
	         FileOutputStream fileOut = new FileOutputStream(savedFile,true);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(toRun);
	         out.close();
	         fileOut.close();
	         say("Simulation saved in "+ DEFAULT_SAVE_LOCATION);
	      }catch(IOException i) {
	         i.printStackTrace();
	      }
	}
	
	public static void load() {
		say("Loading simulation from " + DEFAULT_SAVE_LOCATION);
	      try {
	         FileInputStream fileIn = new FileInputStream(DEFAULT_SAVE_LOCATION);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         toRun = ((Simulation) in.readObject());
	         in.close();
	         fileIn.close();
	      }catch(IOException i) {
	         i.printStackTrace();
	         return;
	      }catch(ClassNotFoundException c) {
	         say("File not found");
	         c.printStackTrace();
	         return;
	      }
	      //say("Name: " + toAdapt.getName() + ", Description: "+ toAdapt.getDescription());
	      //for(TradeAgentController tac : toAdapt.getAgents()) {
	    	  //	say(tac.getDescriptor().getDescription());
	      //}
	}
	
	public static void say(String toSay) {
		consoleOutput+= "\n" + toSay;
		System.out.println("MAIN PROGRAM: "+ toSay);
	}
}
