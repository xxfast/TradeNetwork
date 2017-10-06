import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import ui.MainProgram;
import jade.core.Runtime;

import java.awt.EventQueue;

import agent.ApplianceAgent;
import agent.HomeAgent;
import agent.RetailerAgent;
import agent.SchedulingAgent;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Profile; 
import jade.core.ProfileImpl;

public class SimulationController {
	
	public static void main(String[] args) throws StaleProxyException, InterruptedException {
		// Get a hold to the JADE runtime
		Runtime rt = Runtime.instance();
		// Launch the Main Container (with the administration GUI on top) listening on port 8888
		System.out.println(SimulationController.class.getName() + ": Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null); 
		pMain.setParameter(Profile.GUI, "true");
		ContainerController mainCtrl = rt.createMainContainer(pMain); // Wait for some time
		Thread.sleep(200);
		
		// Create a agent of class SchedulingAgent 
		System.out.println(SimulationController.class.getName() + ": Starting up a SchedulingAgent...");
		AgentController schedulingAgentCtrl = mainCtrl.createNewAgent("SA", SchedulingAgent.class.getName(), new Object[0]);
		schedulingAgentCtrl.start();

		Thread.sleep(100); 
		
		String schedulerName = schedulingAgentCtrl.getName().split("@")[0];
		
		System.out.println(schedulerName);
		// Create a agent of class HomeAgent 
		System.out.println(SimulationController.class.getName() + ": Starting up a HomeAgent...");
		AgentController homeAgentCtrl = mainCtrl.createNewAgent("HA", HomeAgent.class.getName(),new Object[] {schedulerName});
		homeAgentCtrl.start();
		
		// Create a agent of class ApplianceAgent 
		System.out.println(SimulationController.class.getName() + ": Starting up a ApplianceAgent...");
		AgentController ApplianceAgentCtrl = mainCtrl.createNewAgent("AA", ApplianceAgent.class.getName(),new Object[] {schedulerName});
		ApplianceAgentCtrl.start();
		
		// Create a agent of class RetailerAgent 
		System.out.println(SimulationController.class.getName() + ": Starting up a ApplianceAgent...");
		AgentController RetailerAgentCtrl = mainCtrl.createNewAgent("RA", RetailerAgent.class.getName(),new Object[0]);
		RetailerAgentCtrl.start();

		
		// Start the main UI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainProgram window = new MainProgram();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
