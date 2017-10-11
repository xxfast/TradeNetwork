package simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import agent.SchedulingAgent;
import controllers.ApplianceAgentController;
import controllers.HomeAgentController;
import controllers.SchedulingAgentController;
import controllers.TradeAgentController;
import descriptors.ApplianceAgentDescriptor;
import descriptors.HomeAgentDescriptor;
import descriptors.SchedulingAgentDescriptor;
import descriptors.TradeAgentDescriptor;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Simulation implements Serializable {
	
	private String name;
	private String description;
	private State state;
	private List<TradeAgentController> agents = new ArrayList<TradeAgentController>();
	
	private transient ContainerController container;
	
	public TradeAgentController CreateTradeAgent(TradeAgentDescriptor descriptor) throws StaleProxyException {
		TradeAgentController tradeAgent = null;
		AgentController createdAgent = null;
		if(descriptor instanceof SchedulingAgentDescriptor) {
			tradeAgent = new SchedulingAgentController();
		}else if(descriptor instanceof ApplianceAgentDescriptor) {
			tradeAgent = new ApplianceAgentController();}
		else if(descriptor instanceof HomeAgentDescriptor) {
			tradeAgent = new HomeAgentController();
		}
		tradeAgent.setDescriptor(descriptor);
		createdAgent = container.createNewAgent(descriptor.getName(), SchedulingAgent.class.getName(), descriptor.toArray());
		tradeAgent.setInnerController(createdAgent);
		agents.add(tradeAgent);
		return tradeAgent;
	}
	
	public void Start() throws StaleProxyException{
		for(TradeAgentController ctrl : agents) {
			ctrl.start();
		}
	}
	
	public void KillAll() throws StaleProxyException {
		for(TradeAgentController ctrl : agents) {
			ctrl.kill();
		}
	}
	
	public List<TradeAgentController> getAgents() {
		return agents;
	}

	public void setAgents(List<TradeAgentController> agents) {
		this.agents = agents;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ContainerController getContainer() {
		return container;
	}

	public void setContainer(ContainerController container) {
		this.container = container;
	}
	
	public enum State{ Running, Paused, Stopped }
}
