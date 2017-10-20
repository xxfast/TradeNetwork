package simulation;

import java.awt.Container;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import agent.SchedulingAgent;
import controllers.ApplianceAgentController;
import controllers.HomeAgentController;
import controllers.SchedulingAgentController;
import controllers.TradeAgentController;
import descriptors.ApplianceAgentDescriptor;
import descriptors.HomeAgentDescriptor;
import descriptors.SchedulingAgentDescriptor;
import descriptors.TradeAgentDescriptor;
import interfaces.IOwnable;
import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import model.TradeAgentNode;

public class Simulation implements Serializable {
	
	public static int Time = 1000;
	
	private String name;
	private String description;
	private State state;
	
	private TreeModel agents;
	
	private transient ContainerController container;
	
	public Simulation() {
		agents = new DefaultTreeModel(new TradeAgentNode("Simulation"));
	}
	
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
		if(descriptor instanceof IOwnable) {
			IOwnable ownable = (IOwnable) descriptor;
			GetRootForIn(ownable.getOwner(),(DefaultMutableTreeNode)agents.getRoot()).add(new TradeAgentNode(tradeAgent));
		}else {
			((DefaultMutableTreeNode)agents.getRoot()).add(new TradeAgentNode(tradeAgent));
		}
		return tradeAgent;
	}
	
	private DefaultMutableTreeNode GetRootForIn(AID lookingFor, DefaultMutableTreeNode in) {
		for(int i=0;i<in.getChildCount();i++) {
			if(in.getChildAt(i) instanceof TradeAgentNode) {
				TradeAgentNode node = (TradeAgentNode) in.getChildAt(i);
				if(node.getAgent()!=null) {
					String name = lookingFor.getLocalName().split("@")[0];
					if(node.getAgent().getDescriptor().getName().equals(name)) {
						return node;
					}
				}
				DefaultMutableTreeNode inChildren =  GetRootForIn(lookingFor,node);
				if(inChildren== null) continue;
				else return inChildren;
			}
		}
		return null;
	}

	public void Start() throws StaleProxyException{
		StartNode((TradeAgentNode) agents.getRoot());
	}
	
	private void StartNode(TradeAgentNode toStart) throws StaleProxyException {
		toStart.getAgent().start();
		for(int i=0;i<toStart.getChildCount();i++) {
			StartNode((TradeAgentNode) toStart.getChildAt(i));
		}
	}
	
	public void Stop() throws StaleProxyException {
		KillNode((TradeAgentNode) agents.getRoot());
	}
	
	private void KillNode(TradeAgentNode toStart) throws StaleProxyException {
		toStart.getAgent().kill();
		for(int i=0;i<toStart.getChildCount();i++) {
			KillNode((TradeAgentNode) toStart.getChildAt(i));
		}
	}
	
	public TreeModel getAgents() {
		return agents;
	}

	public void setAgents(TreeModel agents) {
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
