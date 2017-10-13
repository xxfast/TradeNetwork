package controllers;

import java.io.Serializable;

import descriptors.TradeAgentDescriptor;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class TradeAgentController implements Serializable{
	
	private TradeAgentDescriptor descriptor;
	private transient AgentController innerController;
	
	public void start() throws StaleProxyException{
		innerController.start();
	}
	
	public void kill() throws StaleProxyException{
		innerController.kill();
	}
	
	public TradeAgentDescriptor getDescriptor() {
		return descriptor;
	}
	public void setDescriptor(TradeAgentDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	public AgentController getInnerController() {
		return innerController;
	}
	public void setInnerController(AgentController innerController) {
		this.innerController = innerController;
	}
	
}
