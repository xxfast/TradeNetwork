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

	public static class TradeAgentNodeRenderer implements TreeCellRenderer {

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			JLabel label;
			label = new JLabel();
			Object o = ((DefaultMutableTreeNode) value);
			if (o instanceof TradeAgentNode) {
				TradeAgentNode agent = (TradeAgentNode) o;
				if (agent.getAgent() != null) {
					label.setText(agent.getAgent().getDescriptor().getName());
					URL imageUrl = null;
					try {
						imageUrl = new URL(
								ProcessNameToURL(agent.getAgent().getDescriptor().getClass().getSimpleName()));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (imageUrl != null) {
						label.setIcon(new ImageIcon(imageUrl));
					}
				} else {
					label.setText("Simulation");
					URL imageUrl = null;
					try {
						imageUrl = new URL(ProcessNameToURL("SimulationAgent"));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (imageUrl != null) {
						label.setIcon(new ImageIcon(imageUrl));
					}
				}

			} else {
				label.setIcon(null);
				label.setText("" + value);
			}
			return label;
		}

		public static String ProcessNameToURL(String toProcess) throws MalformedURLException {
			URL toLocate = new File(
					System.getProperty("user.dir") + "/resources/icons/" + toProcess.split("Agent")[0] + ".png").toURI()
							.toURL();
			return toLocate.toString();
		}
	}
}
