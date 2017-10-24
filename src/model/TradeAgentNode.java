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

public class TradeAgentNode extends DefaultMutableTreeNode {

	private String iconResource;
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

	public String getIconResource() {
		return iconResource;
	}

	public void setIconResource(String icon) {
		this.iconResource = icon;
	}

	
}
