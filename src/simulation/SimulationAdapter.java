package simulation;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import ui.SimulationInspecter;
import jade.core.Runtime;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import agent.ApplianceAgent;
import agent.HomeAgent;
import agent.RetailerAgent;
import agent.SchedulingAgent;
import controllers.TradeAgentController;
import descriptors.HomeAgentDescriptor;
import descriptors.SchedulingAgentDescriptor;
import descriptors.TradeAgentDescriptor;
import interfaces.Object2ApplianceAgentInterface;
import jade.core.AID;
import jade.core.Profile; 
import jade.core.ProfileImpl;

public class SimulationAdapter {
	
	private static Simulation toAdapt;
	
	public void Start() throws StaleProxyException {
		toAdapt.Start();
	}

	public static Simulation getToAdapt() {
		return toAdapt;
	}

	public static void setToAdapt(Simulation toAdapt) {
		SimulationAdapter.toAdapt = toAdapt;
	}
	
}
