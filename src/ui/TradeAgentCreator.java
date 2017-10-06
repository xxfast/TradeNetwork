package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import model.AgentType;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import javax.swing.BoxLayout;

public class TradeAgentCreator extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtAgentname;


	/**
	 * Create the dialog.
	 */
	public TradeAgentCreator() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Create a TradeAgent");
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setModal(true);
		setBounds(100, 100, 500, 100);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		{
			JLabel lblAgentName = new JLabel("Agent Name");
			contentPanel.add(lblAgentName);
		}
		{
			txtAgentname = new JTextField();
			txtAgentname.setText("agentName");
			contentPanel.add(txtAgentname);
			txtAgentname.setColumns(10);
		}
		{
			JLabel lblType = new JLabel("Type");
			contentPanel.add(lblType);
		}
		{
			JComboBox comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(AgentType.values()));
			contentPanel.add(comboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("CREATE");
				okButton.setActionCommand("Create");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
