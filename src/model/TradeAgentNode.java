package model;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import controllers.TradeAgentController;
import simulation.Simulation;

public class TradeAgentNode extends DefaultMutableTreeNode {

	private String iconResource;
	private TradeAgentController agent;
	private Simulation owner;

	public TradeAgentNode(String name, Simulation ownr) {
		super(name);
		setOwner(ownr);
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

	public String getIconResource() {
		return iconResource;
	}

	public void setIconResource(String icon) {
		this.iconResource = icon;
	}

	public Simulation getOwner() {
		return owner;
	}

	public void setOwner(Simulation owner) {
		this.owner = owner;
	}

	
}
