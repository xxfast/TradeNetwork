package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import agent.ApplianceAgent;
import annotations.Adjustable;
import descriptors.TradeAgentDescriptor;
import jade.wrapper.StaleProxyException;
import simulation.Simulation;

import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class TradeAgentCreator extends JDialog implements ActionListener {

	private Simulation simulation;
	
	private final JPanel contentPanel = new JPanel(new BorderLayout());
	
	private Object instance;
	
	private Class<?> type;
	
	public JButton okButton ;

	public int adjustableFields = 0;
	
	public TradeAgentCreator(Class<?> type) {
		this.type = type;
	}
	
	public void Build() throws InstantiationException, IllegalAccessException{
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Create a "+ type.getSimpleName());
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setModal(true);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		/* Create the instance */
		instance = type.newInstance();
		JPanel form = new JPanel();
		/* Traverse through the hierarchy to check for any inheriting members */
		Stack<Class<?>> parents = new Stack<Class<?>>();
		do {
			parents.push(type);
			type = type.getSuperclass();
		}while(type.isAnnotationPresent(Adjustable.class));
		
		Class<?> parent;
		do {
			parent = parents.pop();
			CreateInspector(parent, instance, form);
		}while(!parents.isEmpty());

		contentPanel.add(form, BorderLayout.CENTER);
		
		/* Create Layouts for labels and fields  */
		setBounds(0, 0, 400, 50 + (adjustableFields * 50));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton("CREATE");
		okButton.setActionCommand("Create");
		okButton.addActionListener(this);
		AllowSave(true);
		
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	private JPanel CreateInspector(Class<?> type, Object instance, JPanel container) throws InstantiationException, IllegalAccessException {
		
		/* Create holders for later*/ 
		List<JLabel> labels = new ArrayList<JLabel>();
		List<JTextField> inputs = new ArrayList<JTextField>();
		List<String> inspectorLabel = new ArrayList<String>();
		List<JPanel> inpectors = new ArrayList<JPanel>();
		
		for(Field f : type.getDeclaredFields()) {
			/* Only get the adjustable fields */
			if(f.isAnnotationPresent(Adjustable.class)) {
				/* Add to the field buffer for later*/
				adjustableFields++;
				/* Get the type of the adjustable*/
				Class<?> innerType = f.getType();
				/* check if type of adjustable is not adjustable*/
				if(innerType.isAnnotationPresent(Adjustable.class)) {
					JPanel newPanel = new JPanel(new BorderLayout());
					Object innerInstance = innerType.newInstance();
					inspectorLabel.add(ProcessVariableName(f.getName()));
					inpectors.add(CreateInspector(innerType,innerInstance, newPanel));
					try {
						PropertyDescriptor pd = new PropertyDescriptor(f.getName(), type);
						Method setter = pd.getWriteMethod();
						Object fd = setter.invoke(instance,innerInstance);
					}catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IntrospectionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}else {

					/* if type of adjustable is not adjustable, then it assumed to be primitive*/
					Adjustable c = f.getAnnotation(Adjustable.class);
					JLabel label = new JLabel(c.label(), JLabel.RIGHT);
					labels.add(label);
					JTextField input= new JTextField();
					
					input.addFocusListener(new FocusListener() {
						@Override
						public void focusGained(FocusEvent e) {}

						@Override
						public void focusLost(FocusEvent e) {
							try {
								Class<?> desiredType = f.getType();
								PropertyDescriptor pd = new PropertyDescriptor(f.getName(), type);
								Method setter = pd.getWriteMethod();
								Object fd = setter.invoke(instance, (desiredType!=String.class)?valueOf(desiredType, input.getText()):input.getText());
								input.setBackground(Color.GREEN);
							}catch (InvocationTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IntrospectionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalArgumentException e1) {
								input.setBackground(Color.RED);
								e1.printStackTrace();
							} 
						}

						
					});
					input.setColumns(10);
					inputs.add(input);
				}
			}
		}
		
		/* Create the UI Elements */
		container.setLayout(new FlowLayout());
		
		JPanel labelPanel = new JPanel(new GridLayout(labels.size(), 1));
	    JPanel fieldPanel = new JPanel(new GridLayout(inputs.size(), 1));
	    for(int i=0;i< labels.size(); i++) {
    			labelPanel.add(labels.get(i));
    			fieldPanel.add(inputs.get(i));
	    }
	    JPanel form = new JPanel(new BorderLayout());
	    form.add(labelPanel, BorderLayout.WEST);
	    form.add(fieldPanel, BorderLayout.CENTER);
	    container.add(form);
	    for(int i=0; i<inpectors.size();i++) {
	    		inpectors.get(i).setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), inspectorLabel.get(i)));
		    container.add(inpectors.get(i));
	    }
	    return container;
	}
	
	public boolean AllValid() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private String ProcessVariableName(String name) {
		String newName = ""; 
		for(int i=0; i<name.length(); i++) {
	        if(Character.isUpperCase(name.charAt(i)) && i!=0) {
		        newName+=' ';
	        }
	        newName+=(i==0)?Character.toUpperCase(name.charAt(i)):name.charAt(i);
	    }
		return newName;
	}

	/**
	 *  On Completion of the form
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println(instance.toString());
		try {
			simulation.CreateTradeAgent((TradeAgentDescriptor) instance);
			dispose(); 
		} catch (StaleProxyException e1) {
			e1.printStackTrace();
		}
	}
	
	public void AllowSave(boolean should) {
		okButton.setEnabled(should);
	}
	
	static <T> T valueOf(Class<T> c, String arg) {
        Exception cause = null;
        T ret = null;
        try {
            ret = c.cast(
                c.getDeclaredMethod("valueOf", String.class)
                .invoke(null, arg)
            );
        } catch (NoSuchMethodException e) {
            cause = e;
        } catch (IllegalAccessException e) {
            cause = e;
        } catch (InvocationTargetException e) {
            cause = e;
        }
        if (cause == null) {
            return ret;
        } else {
            throw new IllegalArgumentException(cause);
        }
    }

	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation toInspect) {
		this.simulation = toInspect;
	}

}
