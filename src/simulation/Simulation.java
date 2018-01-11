package simulation;

import java.awt.Container;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import agent.ApplianceAgent;
import agent.HomeAgent;
import agent.RetailerAgent;
import agent.SchedulingAgent;
import agent.appliances.HeaterAgent;
import agent.appliances.RefrigeratorAgent;
import agent.appliances.TelevisionAgent;
import controllers.ApplianceAgentController;
import controllers.HeaterAgentController;
import controllers.HomeAgentController;
import controllers.RefrigeratorAgentController;
import controllers.RetailerAgentController;
import controllers.SchedulingAgentController;
import controllers.TelevisionAgentController;
import controllers.TradeAgentController;
import descriptors.ApplianceAgentDescriptor;
import descriptors.HeaterAgentDescriptor;
import descriptors.HomeAgentDescriptor;
import descriptors.RefrigeratorAgentDescriptor;
import descriptors.RetailerAgentDescriptor;
import descriptors.SchedulingAgentDescriptor;
import descriptors.TelevisionAgentDescriptor;
import descriptors.TradeAgentDescriptor;
import interfaces.IOwnable;
import interfaces.ISavable;
import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import model.TradeAgentNode;

public class Simulation implements Serializable, ISavable{
	
	public static int Time = 1000;
	
	private String name = "default";
	private String description;
	private State state;
	
	private transient TreeModel agentTree;
	private List<TradeAgentController> agents;
	
	private transient ContainerController container;
	
	private String output;
	
	public Simulation() {
		agentTree = new DefaultTreeModel(new TradeAgentNode("Simulation", this));
		agents = new ArrayList<TradeAgentController>();
	}
	
	public Simulation(String name) {
		this.name = name;
		agentTree = new DefaultTreeModel(new TradeAgentNode(name, this));
		agents = new ArrayList<TradeAgentController>();
	}
	
	public void ExpandTree(){
		agentTree = new DefaultTreeModel(new TradeAgentNode(name, this));
		if(agents!=null && !agents.isEmpty()) {
			for(TradeAgentController nd : agents) {
				try {
					CreateTradeAgent(nd.getDescriptor());
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void FlattenTree() throws StaleProxyException {
		agents = this.getAgentsAsList((TradeAgentNode) agentTree.getRoot());
	}
	
	public TradeAgentController CreateTradeAgent(TradeAgentDescriptor descriptor) throws StaleProxyException {
		TradeAgentController tradeAgent = null;
		Class<?> toCreate = null;
		AgentController createdAgent = null;
		if(descriptor instanceof SchedulingAgentDescriptor) {
			tradeAgent = new SchedulingAgentController();
			toCreate = SchedulingAgent.class;
		}else if(descriptor instanceof HeaterAgentDescriptor) {
			tradeAgent = new HeaterAgentController();
			toCreate = HeaterAgent.class;
		}else if(descriptor instanceof RefrigeratorAgentDescriptor) {
			tradeAgent = new RefrigeratorAgentController();
			toCreate = RefrigeratorAgent.class;
		}else if(descriptor instanceof TelevisionAgentDescriptor) {
			tradeAgent = new TelevisionAgentController();
			toCreate = TelevisionAgent.class;
		}else if(descriptor instanceof ApplianceAgentDescriptor) {
			tradeAgent = new ApplianceAgentController();
			toCreate = ApplianceAgent.class;
		}else if(descriptor instanceof HomeAgentDescriptor) {
			tradeAgent = new HomeAgentController();
			toCreate = HomeAgent.class;
		}else if(descriptor instanceof RetailerAgentDescriptor) {
			tradeAgent = new RetailerAgentController();
			toCreate = RetailerAgent.class;
		}else if(descriptor instanceof HeaterAgentDescriptor) {
			tradeAgent = new HeaterAgentController();
			toCreate = HeaterAgent.class;
		}
		tradeAgent.setDescriptor(descriptor);
		createdAgent = container.createNewAgent(descriptor.getName(), toCreate.getName(), descriptor.toArray());
		tradeAgent.setInnerController(createdAgent);
		if(descriptor instanceof IOwnable) {
			IOwnable ownable = (IOwnable) descriptor;
			GetRootForIn(ownable.getOwner(),(DefaultMutableTreeNode)agentTree.getRoot()).add(new TradeAgentNode(tradeAgent));
		}else {
			((DefaultMutableTreeNode)agentTree.getRoot()).add(new TradeAgentNode(tradeAgent));
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
		List<TradeAgentController> controllers = getAgentsAsList((TradeAgentNode) agentTree.getRoot());
		for(TradeAgentController ta  : controllers) {
			if(!(ta instanceof HomeAgentController)) {
				ta.start();
			}
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(TradeAgentController ta  : controllers) {
			if(ta instanceof HomeAgentController) {
				ta.start();
			}
		}
	}
	
	private void StartNode(TradeAgentNode toStart) throws StaleProxyException {
		if(toStart.getAgent()!=null) 
			toStart.getAgent().start();
		for(int i=0;i<toStart.getChildCount();i++) {
			StartNode((TradeAgentNode) toStart.getChildAt(i));
		}
	}
	
	public void Stop() throws StaleProxyException {
		KillNode((TradeAgentNode) agentTree.getRoot());
	}
	
	private void KillNode(TradeAgentNode toKill) throws StaleProxyException {
		if(toKill.getAgent()!=null && toKill.getAgent().getInnerController()!=null) 
			toKill.getAgent().kill();
		for(int i=0;i<toKill.getChildCount();i++) {
			KillNode((TradeAgentNode) toKill.getChildAt(i));
		}
	}
	
	public List<TradeAgentController> getAgentsAsList(TradeAgentNode toStart){
		List<TradeAgentController> agents = new ArrayList<TradeAgentController>();
		if(toStart.getAgent()!=null) agents.add(toStart.getAgent());
		for(int i=0;i<toStart.getChildCount();i++) {
			agents.addAll(getAgentsAsList((TradeAgentNode) toStart.getChildAt(i)));
		}
		return agents;
	}
	
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
	

	public void Remove(TradeAgentNode tn) {
		// TODO Auto-generated method stub
	}
	
	public TreeModel getAgentTree() {
		return agentTree;
	}

	public void setAgentTree(TreeModel agents) {
		this.agentTree = agents;
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
	
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public enum State{ Running, Paused, Stopped }

	public void say(String toSay) {
		System.out.println(this.getName() +":" + toSay);
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
}
