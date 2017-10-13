package ui;

import java.awt.Button;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class SimulationController extends JPanel {

	/**
	 * Create the panel.
	 */
	public SimulationController() {
		setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		Button playBtn = new Button("Play");
		playBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		add(playBtn);
		
		Button stopBtn = new Button("Stop");
		add(stopBtn);
	}

}
