package ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class TradeAgentInspector extends JPanel {

	public TradeAgentInspector() {
		setBorder(new TitledBorder(null, "Agent Inspector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		setPreferredSize(new Dimension(250, 100));
		JLabel lblEditorconsole = new JLabel("Please select an agent to edit it's properties");
		lblEditorconsole.setPreferredSize(new Dimension(250, 100));
		add(lblEditorconsole);

	}

}
