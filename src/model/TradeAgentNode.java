package model;

import javax.swing.tree.DefaultMutableTreeNode;

import controllers.TradeAgentController;

public class TradeAgentNode extends DefaultMutableTreeNode{
	
	private TradeAgentController agent;
	
	public TradeAgentNode(String name) {
		super(name);
	}
	
	public TradeAgentNode(TradeAgentController agent) {
		super(agent.getDescriptor().getName());
		setAgent(agent);
	}
	
	public TradeAgentController getAgent() {
		return agent;
	}

	public void setAgent(TradeAgentController agent) {
		this.agent = agent;
	}
}
