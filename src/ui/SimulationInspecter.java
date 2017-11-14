package ui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import controllers.TradeAgentController;
import descriptors.ApplianceAgentDescriptor;
import descriptors.HeaterAgentDescriptor;
import descriptors.HomeAgentDescriptor;
import descriptors.RefrigeratorAgentDescriptor;
import descriptors.RetailerAgentDescriptor;
import descriptors.TelevisionAgentDescriptor;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;
import model.TradeAgentNode;
import simulation.Simulation;

public class SimulationInspecter {

	private Simulation toInspect;

	private Runtime jadeRuntime;

	private JFrame frame;
	private JTree tree;

	/**
	 * Create the application.
	 * 
	 * @param simulationController
	 */
	public SimulationInspecter(Runtime rt) {
		this.jadeRuntime = rt;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setFrame(new JFrame("TradeNetwork"));
		getFrame().setBounds(100, 100, 500, 350);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		getFrame().setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
				TradeNetworkFileFilter filter = new TradeNetworkFileFilter();

				fc.setFileFilter(filter);
				int returnVal = fc.showDialog(SimulationInspecter.this.getFrame(), "Open");

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						System.out.println("Inspector: Clearing container.");
						if(toInspect.getState()!=Simulation.State.Running) toInspect.Start();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						toInspect.Stop();
					} catch (StaleProxyException e1) {
						e1.printStackTrace();
					}
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					System.out.println("Opening: " + file.getAbsolutePath() + ".");
					ContainerController container = toInspect.getContainer();
					toInspect = (Simulation) UIUtilities.Load(file.getAbsolutePath());
					toInspect.setContainer(container);
					toInspect.ExpandTree();
					System.out.println(toInspect.toString());
					UpdateModel();
				} else {
					System.out.println("Open command cancelled by user.");
				}
			}

		});
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
				fc.setSelectedFile(new File(UIUtilities.processFileName(toInspect.getName())));
				// Set an extension filter, so the user sees other XML files
				fc.setFileFilter(new FileNameExtensionFilter("tns file", "tns"));
				int returnVal = fc.showSaveDialog(SimulationInspecter.this.getFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					toInspect.setName(file.getName().substring(0,file.getName().indexOf('.')));
					System.out.println("Saving " + toInspect.toString());
					try {
						toInspect.FlattenTree();
					} catch (StaleProxyException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					UIUtilities.Save(toInspect, file);
					System.out.println("Saved: " + file.getAbsolutePath() + ".");
				} else {
					System.out.println("Save cancelled by user.");
				}
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save As..");
		mnFile.add(mntmSaveAs);

		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenu mnCreate = new JMenu("Create");
		mnEdit.add(mnCreate);

		JMenu mnAppliances = new JMenu("Appliances");
		mnCreate.add(mnAppliances);

		JMenuItem mntmApplianceAgent = new JMenuItem("Generic Appliance Agent");
		mnAppliances.add(mntmApplianceAgent);
		mntmApplianceAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(ApplianceAgentDescriptor.class);
					dialog.setSimulation(toInspect);
					dialog.Build();
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							UpdateModel();
						}
					});
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});

		JMenuItem mntmHeaterApplianceAgent = new JMenuItem("Heater Appliance Agent");
		mnAppliances.add(mntmHeaterApplianceAgent);
		mntmHeaterApplianceAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(HeaterAgentDescriptor.class);
					dialog.setSimulation(toInspect);
					dialog.Build();
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							UpdateModel();
						}
					});
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});

		JMenuItem mntmTelevisionApplianceAgent = new JMenuItem("Television Appliance Agent");
		mnAppliances.add(mntmTelevisionApplianceAgent);
		mntmTelevisionApplianceAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(TelevisionAgentDescriptor.class);
					dialog.setSimulation(toInspect);
					dialog.Build();
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							UpdateModel();
						}
					});
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});

		JMenuItem mntmRefrigeratorApplianceAgent = new JMenuItem("Refrigerator Appliance Agent");
		mnAppliances.add(mntmRefrigeratorApplianceAgent);
		mntmRefrigeratorApplianceAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(RefrigeratorAgentDescriptor.class);
					dialog.setSimulation(toInspect);
					dialog.Build();
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							UpdateModel();
						}
					});
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});

		JMenuItem mntmHomeAgent = new JMenuItem("Home Agent");
		mntmHomeAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(HomeAgentDescriptor.class);
					dialog.setSimulation(toInspect);
					dialog.Build();
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							UpdateModel();
						}
					});
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
		mntmRetailerAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentCreator dialog = null;
				try {
					dialog = new TradeAgentCreator(RetailerAgentDescriptor.class);
					dialog.setSimulation(toInspect);
					dialog.Build();
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							UpdateModel();
						}
					});
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				dialog.setVisible(true);
			}
		});
		mnCreate.add(mntmRetailerAgent);

		JMenuItem mntmRemove = new JMenuItem("Remove Selected");
		mnEdit.add(mntmRemove);
		mntmRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TradeAgentNode tn = (TradeAgentNode) tree.getSelectionPath().getLastPathComponent();
				toInspect.Remove(tn);
			}
		});

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
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		view.add(tree, BorderLayout.CENTER);

		Panel sidebar = new Panel();
		sidebar.setPreferredSize(new Dimension(250, 0));
		view.add(sidebar, BorderLayout.EAST);
		SimulationController simulationCtrl = new SimulationController(toInspect);

		simulationCtrl.playBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					toInspect.setState(Simulation.State.Running);
					toInspect.Start();
					simulationCtrl.playBtn.setEnabled(toInspect.getState()!=Simulation.State.Running);
					simulationCtrl.stopBtn.setEnabled(toInspect.getState()==Simulation.State.Running);
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		simulationCtrl.stopBtn.setEnabled(false);
		
		simulationCtrl.stopBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					toInspect.setState(Simulation.State.Stopped);
					toInspect.Stop();
					simulationCtrl.playBtn.setEnabled(toInspect.getState()!=Simulation.State.Running);
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		sidebar.add(simulationCtrl);

		TradeAgentInspector inspector = new TradeAgentInspector();
		sidebar.add(inspector);

		TradeAgentControls controller = new TradeAgentControls();
		sidebar.add(controller);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TradeAgentNode node = (TradeAgentNode) tree.getLastSelectedPathComponent();
				if (node == null)
					return;
				if (node.getAgent() != null) {
					inspector.setSelectedAgent(node);
					inspector.Update();
					controller.setToControl(node.getAgent());
					controller.Update();
				} else {
					inspector.Clear();
					controller.Clear();
				}
			}
		});
	}

	public void UpdateModel() {
		tree.setModel(getToInspect().getAgentTree());
		tree.setCellRenderer(new TradeAgentNodeRenderer());
		tree.updateUI();
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
