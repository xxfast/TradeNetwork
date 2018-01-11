package ui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import model.TradeAgentNode;

public class TradeAgentNodeRenderer implements TreeCellRenderer {

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
				label.setText(agent.getOwner().getName());
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

		if(selected) {
			label.setBackground(Color.gray);
			label.setForeground(Color.gray);
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