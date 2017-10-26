package ui;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;

import annotations.Adjustable;
import descriptors.TradeAgentDescriptor;
import model.TradeAgentNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class TradeAgentInspector extends JPanel {
	
	private TradeAgentNode selectedAgent;
	private JComponent shown;

	Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	
	public TradeAgentInspector() {
		setBorder(new TitledBorder(null, "Agent Inspector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		Clear();
		add(shown);
	}
	
	public void Build(TradeAgentDescriptor instance) {
		Class<?> type = instance.getClass();
		/* Traverse through the hierarchy to check for any inheriting members */
		Stack<Class<?>> parents = new Stack<Class<?>>();
		do {
			parents.push(type);
			type = type.getSuperclass();
		}while(type.isAnnotationPresent(Adjustable.class));
		
		/*  Process the parent at a time */
		Class<?> parent;
		do {
			parent = parents.pop();
			CreateTable(parent, instance);
		}while(!parents.isEmpty());

		Vector<Object> colomns = new Vector<Object>();
		colomns.add("key");
		colomns.add("value");
		remove(shown);
		shown = new JTable(data,colomns);
		shown.setBackground(this.getBackground());
		add(shown);
		shown.setPreferredSize(shown.getPreferredSize());
		this.revalidate();
		this.repaint();
	}
	
	private void CreateTable(Class<?> type, Object instance) {
		for(Field f : type.getDeclaredFields()) {
			/* Create the Array*/
			Vector<Object> keyValues = new Vector<Object>();
			if(f.isAnnotationPresent(Adjustable.class)) {
				data.addElement(keyValues);
				/* Get the type of the adjustable*/
				Class<?> innerType = f.getType();
				keyValues.add(f.getName());
				/* check if type of adjustable is not adjustable*/
				Object innerInstance = null;
				PropertyDescriptor pd;
				if(innerType.isAnnotationPresent(Adjustable.class)) {
					try {
						pd = new PropertyDescriptor(f.getName(), type);
						Method getter = pd.getReadMethod();
						innerInstance = getter.invoke(instance);
					} catch (IntrospectionException e) {
						e.printStackTrace();
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
					if(innerInstance!=null) CreateTable(innerType, innerInstance);
				}else {
					try {
						pd = new PropertyDescriptor(f.getName(), type);
						Method getter = pd.getReadMethod();
						Object fd = getter.invoke(instance);
						keyValues.add(fd);
					}catch (InvocationTargetException |  IntrospectionException | IllegalAccessException e) {
						e.printStackTrace();
					} 
				}
			}
		}
	}
	
	public void Clear() {
		removeAll();
		data = new Vector<Vector<Object>>();
		JTextArea inspectText = new JTextArea("No Agents selected");
		inspectText.setBackground(this.getBackground());
		inspectText.setPreferredSize(inspectText.getPreferredSize());
		inspectText.setColumns(5);
		inspectText.setWrapStyleWord(true);
		inspectText.setLineWrap(true);
		shown = inspectText;
	}
	
	public void Update() {
		data = new Vector<Vector<Object>>();
		Build(getSelectedAgent().getAgent().getDescriptor());
	}

	public TradeAgentNode getSelectedAgent() {
		return selectedAgent;
	}

	public void setSelectedAgent(TradeAgentNode selectedAgent) {
		this.selectedAgent = selectedAgent;
	}

}
