package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import agent.ApplianceAgent;
import agent.RetailerAgent;
import agent.TradeAgent;
import annotations.Creatable;
import annotations.Customizable;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import model.AgentType;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import javax.swing.BoxLayout;
import java.awt.Rectangle;

public class TradeAgentCreator extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	public TradeAgentCreator(Class<?> type) {
		setBounds(new Rectangle(37, 23, 300, 400));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Create a "+ type.getSimpleName());
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(3,1));
		
		type = ApplianceAgent.class;
		
		/* Fields will be instantiated only for customizable agents */
		if(type.isAnnotationPresent(Creatable.class)) {
			for(Field f : type.getDeclaredFields()) {
				if(f.getAnnotationsByType(Customizable.class)!=null) {
					Customizable c = f.getAnnotation(Customizable.class);
					JLabel label = new JLabel(c.label());
					contentPanel.add(label);
					JTextField input= new JTextField();
					contentPanel.add(input);
					input.setColumns(10);
				}
			}
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
