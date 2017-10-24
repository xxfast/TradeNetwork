package ui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;

import model.TradeAgentNode;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class TradeAgentInspector extends JPanel {
	
	private TradeAgentNode selectedAgent;
	private JTextArea inspectText;
	
	public TradeAgentInspector() {
		setBorder(new TitledBorder(null, "Agent Inspector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		setPreferredSize(new Dimension(250, 100));
		Clear();
		add(inspectText);
	}
	
	public void Clear() {
		inspectText = new JTextArea("Please select an agent to inspect it's properties");
		inspectText.setBackground(this.getBackground());
		inspectText.setBounds(0, 0, 230, 500);
		inspectText.setColumns(5);
		inspectText.setWrapStyleWord(true);
		inspectText.setLineWrap(true);
	}
	
	public void Update() {
		inspectText.setBounds(0, 0, 230, inspectText.getPreferredSize().height);
		inspectText.setText(selectedAgent.getAgent().getDescriptor().getDescription());
	}

	public TradeAgentNode getSelectedAgent() {
		return selectedAgent;
	}

	public void setSelectedAgent(TradeAgentNode selectedAgent) {
		this.selectedAgent = selectedAgent;
	}

	public JTextArea getInspectText() {
		return inspectText;
	}

	public void setInspectText(JTextArea inspectText) {
		this.inspectText = inspectText;
	}

}
