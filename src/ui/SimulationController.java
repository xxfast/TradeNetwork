package ui;

import java.awt.Button;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import jade.wrapper.StaleProxyException;
import simulation.Simulation;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class SimulationController extends JPanel {
	
	private Simulation toControl;
	public Button playBtn;
	
	public SimulationController(Simulation toControl) {
		this.toControl = toControl;
		setBorder(new TitledBorder(null, "Simulation Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		playBtn = new Button("Play");
		add(playBtn);
		
		Button stopBtn = new Button("Stop");
		add(stopBtn);
	}
	
}
