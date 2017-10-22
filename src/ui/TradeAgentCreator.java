package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.eclipse.jdt.annotation.Nullable;

import agent.ApplianceAgent;
import annotations.Adjustable;
import descriptors.TradeAgentDescriptor;
import jade.core.AID;
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
	
	private List<JComponent> gFields = new ArrayList<JComponent>();
	
	private static Map<Class<?>, Class<?>> primitiveRegistry = RegisterPremitives();
	
	private static HashMap<Class<?>, Class<?>> RegisterPremitives()
    {
		HashMap<Class<?>, Class<?>> toReturn = new HashMap<Class<?>, Class<?>> ();
		toReturn.put(int.class, Integer.class);
		toReturn.put(double.class, Double.class);
		toReturn.put(boolean.class, Boolean.class);
		toReturn.put(short.class, Short.class);
		toReturn.put(long.class, Long.class);
        return toReturn;
    }

	public TradeAgentCreator(Class<?> type) {
		this.type = type;
	}
	
	public void Build() throws InstantiationException, IllegalAccessException{
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Create a "+ UIUtilities.ProcessVariableName(type.getSimpleName()));
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
		setBounds(0, 0, 350, 50 + (adjustableFields * 50));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton("CREATE");
		okButton.setActionCommand("Create");
		okButton.addActionListener(this);

		/* Disabled by default because not all fields are filled in  */
		AllowSave(false);
		
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPane.add(cancelButton);
	}
	
	private JPanel CreateInspector(Class<?> type, Object instance, JPanel container) throws InstantiationException, IllegalAccessException {
		
		/* Create holders for later*/ 
		List<JLabel> labels = new ArrayList<JLabel>();
		List<String> inspectorLabel = new ArrayList<String>();
		List<JComponent> inputs = new ArrayList<JComponent>();
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
					inspectorLabel.add(UIUtilities.ProcessVariableName(f.getName()));
					JPanel inspector = CreateInspector(innerType,innerInstance, newPanel);
					inpectors.add(inspector);
					try {
						PropertyDescriptor pd = new PropertyDescriptor(f.getName(), type);
						Method setter = pd.getWriteMethod();
						Object fd = setter.invoke(instance,innerInstance);
					}catch (InvocationTargetException |  IntrospectionException | IllegalAccessException e) {
						inspector.setBackground(Color.ORANGE);
					} 
				}else {
					/* if type of adjustable is not adjustable, then it assumed to be primitive*/
					Adjustable c = f.getAnnotation(Adjustable.class);
					String labelText = ((c.label().equals("")) ? UIUtilities.ProcessVariableName(f.getName()) : c.label());
					JLabel label = new JLabel(labelText, JLabel.RIGHT);
					labels.add(label);
					JComponent input;
					/* Checks if the type refers to another Agent */
					if(f.getType()!=AID.class) {
						input = new JTextField();
						if(!f.isAnnotationPresent(Nullable.class)) {
							try {
								PropertyDescriptor pd = new PropertyDescriptor(f.getName(), type);
								Method getter = pd.getReadMethod();
								Object fd = getter.invoke(instance);
								if(fd!=null) ((JTextField)input).setText(fd.toString());
							}catch (InvocationTargetException |  IntrospectionException | IllegalAccessException e) {
								input.setBackground(Color.ORANGE);
							} 
						}
						/*  all inputs will have the same length for now */
						((JTextField)input).setColumns(10);
						inputs.add(input);
					}else {
						/* if the type refers to another Agent, create a list of other agents */
						input = new TrageAgentSelector(simulation.getAgents());
						if(!f.isAnnotationPresent(Nullable.class)) {
							try {
								PropertyDescriptor pd = new PropertyDescriptor(f.getName(), type);
								Method getter = pd.getReadMethod();
								Object fd = getter.invoke(instance);
							}catch (InvocationTargetException |  IntrospectionException | IllegalAccessException e) {
								input.setBackground(Color.ORANGE);
							} 
						}
						inputs.add(input);
					}
					/* add a focus listener to validate on focus lost */
					input.addFocusListener(new FocusListener() {
						@Override
						public void focusGained(FocusEvent e) { }
						@Override
						public void focusLost(FocusEvent e) {
							Validate(instance, type, f, input);
							AllowSave(AllValid(gFields));
						}
					});
					if(!gFields.contains(input)) gFields.add(input);
					

					
				}
			}
		}
		
		/* Create the UI Elements */
		container.setLayout(new FlowLayout(FlowLayout.LEFT));
		
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
	
	public boolean AllValid(List<JComponent> inputs) {
		boolean isValid = true;
		for(JComponent field : inputs) {
			if(field instanceof JTextField) {
				if(!field.getBackground().equals(Color.GREEN)){
					isValid = false;
				}
			}
		}
		return isValid;
	}
	
	/* Callback for save */
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
	
	private void Validate(Object instance, Class<?> whatToValidate, Field toValidate, JComponent toShow) {
		String content = "";
		try {
			Class<?> desiredType = toValidate.getType();
			if(primitiveRegistry.containsKey(desiredType)) desiredType = primitiveRegistry.get(desiredType);
			PropertyDescriptor pd = new PropertyDescriptor(toValidate.getName(), whatToValidate);
			Method setter = pd.getWriteMethod();
			if(toShow instanceof JTextField) {
				content = ((JTextField)toShow).getText();
			}else if(toShow instanceof TrageAgentSelector) {
				AID value =  ((TrageAgentSelector)toShow).getSelectedAgent();
				if(!value.equals(null)) {
					Object fd = setter.invoke(instance, value);
					return;
				}
			}
			if((content.equals("") || content.equals(null)) && !toValidate.isAnnotationPresent(Nullable.class)) {
				toShow.setToolTipText("This field is not nullable");
				throw new NullPointerException(toValidate.getName() + " is not nullable");
			}
			Object fd = setter.invoke(instance, (desiredType!=String.class)?valueOf(desiredType, content ) : content);
			toShow.setBackground(Color.GREEN);
			toShow.setToolTipText(null);
		}catch (InvocationTargetException |  IntrospectionException | IllegalAccessException e) {
			toShow.setToolTipText(e.getCause().getMessage());
			toShow.setBackground(Color.ORANGE);
		} catch (IllegalArgumentException e) {
			toShow.setToolTipText(content+ " is not a " + toValidate.getType().getSimpleName());
			toShow.setBackground(Color.RED);
		} catch (NullPointerException e) {
			toShow.setToolTipText(e.getMessage());
			toShow.setBackground(Color.RED);
			if(toShow instanceof TrageAgentSelector) {
				((TrageAgentSelector)toShow).setRenderer(new DefaultListCellRenderer() {
				    @Override
				    public void paint(Graphics g) {
				        setBackground(Color.RED);
				        super.paint(g);
				    }
				});
			}
		} 
	}
	
	private void AllowSave(boolean should) {
		okButton.setEnabled(should);
	}
	
	static <T> T valueOf(Class<T> c, String arg) {
        Exception cause = null;
        T ret = null;
        try {
            ret = c.cast(c.getDeclaredMethod("valueOf", String.class).invoke(null, arg)
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
