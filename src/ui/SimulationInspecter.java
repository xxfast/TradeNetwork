package ui;
import java.awt.BorderLayout;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import controllers.TradeAgentController;
import descriptors.ApplianceAgentDescriptor;
import descriptors.HomeAgentDescriptor;
import simulation.Simulation;


public class SimulationInspecter {

	private Simulation toInspect; 
	
	private JFrame frame;
	private JTree tree;

	/**
	 * Create the application.
	 * @param simulationController 
	 */
	public SimulationInspecter() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setFrame(new JFrame());
		getFrame().setBounds(100, 100, 500, 350);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		getFrame().setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As..");
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenu mnCreate = new JMenu("Create");
		mnEdit.add(mnCreate);
		
		JMenuItem mntmApplianceAgent = new JMenuItem("Appliance Agent");
		mntmApplianceAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = null;
				try {
					dialog = new TradeAgentCreator(ApplianceAgentDescriptor.class);
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});
		mnCreate.add(mntmApplianceAgent);
		
		JMenuItem mntmHomeAgent = new JMenuItem("Home Agent");
		mntmHomeAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(HomeAgentDescriptor.class);
					dialog.setSimulation(toInspect);
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});
		mnCreate.add(mntmHomeAgent);
		
		JMenuItem mntmRetailerAgent = new JMenuItem("Retailer Agent");
		mnCreate.add(mntmRetailerAgent);
		
		JMenuItem mntmRemove = new JMenuItem("Remove Selected");
		mnEdit.add(mntmRemove);
		
		JMenu mnControl = new JMenu("Control");
		menuBar.add(mnControl);
		
		JRadioButtonMenuItem rdbtnmntmStop = new JRadioButtonMenuItem("Stop");
		mnControl.add(rdbtnmntmStop);
		
		JRadioButtonMenuItem rdbtnmntmPause = new JRadioButtonMenuItem("Pause");
		mnControl.add(rdbtnmntmPause);
		
		JRadioButtonMenuItem rdbtnmntmPlay = new JRadioButtonMenuItem("Play");
		mnControl.add(rdbtnmntmPlay);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		menuBar.add(mntmHelp);
		getFrame().getContentPane().setLayout(new BoxLayout(getFrame().getContentPane(), BoxLayout.X_AXIS));
		
		JPanel view = new JPanel();
		getFrame().getContentPane().add(view);
		view.setLayout(new BorderLayout(0, 0));
		
		JTextPane console = new JTextPane();
		console.setEditable(false);
		console.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		console.setBackground(Color.LIGHT_GRAY);
		console.setText("Console");
		view.add(console, BorderLayout.SOUTH);
		
		tree = new JTree();
		UpdateModel();
		view.add(tree, BorderLayout.CENTER);
		
		Panel sidebar = new Panel();
		sidebar.setPreferredSize(new Dimension(250, 0));
		view.add(sidebar, BorderLayout.EAST);
		
		sidebar.add(new SimulationController());
		sidebar.add(new TradeAgentInspector());
		
		
	}
	
	public void UpdateModel() {
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Simulation") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("Agents");
						node_1.add(new DefaultMutableTreeNode("blue"));
						node_1.add(new DefaultMutableTreeNode("violet"));
						node_1.add(new DefaultMutableTreeNode("red"));
						node_1.add(new DefaultMutableTreeNode("yellow"));
					add(node_1);
				}
			}
		));
	}
	
	public class TradeAgentHolder extends DefaultMutableTreeNode{
		private TradeAgentController agent;

		public TradeAgentController getAgent() {
			return agent;
		}

		public void setAgent(TradeAgentController agent) {
			this.agent = agent;
		}
	}
	
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public Simulation getToInspect() {
		return toInspect;
	}

	public void setToInspect(Simulation toInspect) {
		this.toInspect = toInspect;
	}

}
