package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import annotations.Adjustable;
import annotations.Callable;
import controllers.TradeAgentController;

public class TradeAgentControls extends JPanel {

	private TradeAgentController toControl;
	/**
	 * Create the panel.
	 */
	public TradeAgentControls() {
		setBorder(new TitledBorder(null, "Agent Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		add(new JLabel("No agent selected"));
	}
	
	public void Update() {
		removeAll();
		Class<?> type = toControl.getClass();
		CreateControls(type, toControl);
		revalidate();
		repaint();
		Dimension d = getPreferredSize();
		d.width = 200;
		d.height = 100;
		setPreferredSize(d);
	}
	
	private void CreateControls(Class<?> type, Object instance) {
		for(Method m: type.getMethods()) {
			if(m.isAnnotationPresent(Callable.class)) {
				JButton invokeMethod = new JButton(UIUtilities.ProcessVariableName(m.getName()));
				invokeMethod.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							m.invoke(instance);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
							e1.printStackTrace();
							invokeMethod.setBackground(Color.ORANGE);
						}
					}
				});
				add(invokeMethod);
			}
		}
	}
	
	public void Clear() {
		removeAll();
		add(new JLabel("No agent selected"));
		setPreferredSize(getPreferredSize());
	}
	
	
	public TradeAgentController getToControl() {
		return toControl;
	}
	public void setToControl(TradeAgentController toControl) {
		this.toControl = toControl;
	}
	

}
