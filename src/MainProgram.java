import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import controllers.TradeAgentController;
import descriptors.SchedulingAgentDescriptor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import simulation.Simulation;
import simulation.SimulationAdapter;
import ui.SimulationInspecter;

/**
 * The main program of TradeNetwork
 * This starts up the UI instantiates the SimulationAdapter
 * @author Isuru
 */
public class MainProgram {
	
	public static final String DEFAULT_SAVE_LOCATION = "./data/test.tns";	
	
	public static String consoleOutput = "";
	
	public static AgentContainer container;
	
	public static void main(String[] args) throws StaleProxyException, InterruptedException {
		// Get a hold to the JADE runtime
		Runtime rt = Runtime.instance();
		
		// Launch the Main Container (with the administration GUI on top) listening on port 8888
		say(": Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null); 
		pMain.setParameter(Profile.GUI, "true");
		
		// Get the Agent container
		container = rt.createMainContainer(pMain);
		
		// Set the adapter with a default simulation
		SimulationAdapter.setToAdapt(CreateDefaultSimulation());
		
		// Start the main UI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationInspecter window = new SimulationInspecter();
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
		// Create a agent of class SchedulingAgent 
		SchedulingAgentDescriptor mySchedulingAgent = new SchedulingAgentDescriptor();
		mySchedulingAgent.setName("SA");
		test.CreateTradeAgent(mySchedulingAgent);
		
		return test;
		//Starting the Simulation
		
//		// Create a agent of class HomeAgent 
//		say("Starting up a HomeAgent...");
//		HomeAgentDescriptor myHomeAgent = new HomeAgentDescriptor();
//		myHomeAgent.setName("HA");
//		myHomeAgent.setSchedulerAgent(schedulerAgent);
//		AgentController homeAgentCtrl = mainCtrl.createNewAgent("HA", HomeAgent.class.getName(),new Object[] {schedulerName});
//		currentSimulation.Add(homeAgentCtrl);
//		
//		// Create a agent of class ApplianceAgent 
//		say("Starting up a ApplianceAgent...");
//		AgentController ApplianceAgentCtrl = mainCtrl.createNewAgent("AA", ApplianceAgent.class.getName(),new Object[] {schedulerName});
//		currentSimulation.Add(ApplianceAgentCtrl);
//		
//		// Create a agent of class RetailerAgent 
//		say("Starting up a RetailerAgent...");
//		AgentController RetailerAgentCtrl = mainCtrl.createNewAgent("RA", RetailerAgent.class.getName(),new Object[0]);
//		
//		currentSimulation.Add(RetailerAgentCtrl);
	}
	
	public static void Save() {
		say("Saving simulation to " + DEFAULT_SAVE_LOCATION);
		 try {
			 File savedFile = new File(DEFAULT_SAVE_LOCATION);
			 savedFile.createNewFile();  
	         FileOutputStream fileOut = new FileOutputStream(savedFile,true);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(SimulationAdapter.getToAdapt());
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
	         SimulationAdapter.setToAdapt((Simulation) in.readObject());
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
