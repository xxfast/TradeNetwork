package ui;

import org.jfree.chart.ChartPanel;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import jade.core.AID;
import model.AgentDailyNegotiationThread;
import model.AgentDailyNegotiationThread.Party;
import model.Offer;
import negotiation.NegotiationThread;
import negotiation.Strategy;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class NegotiationInspector extends JFrame {
	
	private AgentDailyNegotiationThread toDisplay;
	
	private int selected = 0;

	public NegotiationInspector(String applicationTitle, String chartTitle, AgentDailyNegotiationThread toDisplay) {
		super(applicationTitle);
		setToDisplay(toDisplay);
		JFreeChart lineChart = ChartFactory.createLineChart(null, "Iteration", "Price", createDataset(0),
				PlotOrientation.VERTICAL, true, true, false);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTabbedPane tabs = new JTabbedPane();
		for(AID agent : toDisplay.getAgents()) {
	        JPanel jp = new JPanel();
	        JLabel label = new JLabel();
	        label.setText(chartTitle + " with " + agent.getLocalName());
	        jp.add(label);
	        tabs.addTab(agent.getLocalName(), jp);
		}
		this.getContentPane().add(tabs, BorderLayout.NORTH);
		
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		//setContentPane(chartPanel);
		add(chartPanel);
		JLabel label = new JLabel("0");
		final JSlider slider = new JSlider(0, 23);
		slider.setValue(0);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            		lineChart.getCategoryPlot().setDataset(createDataset(slider.getValue()));
            		label.setText(slider.getValue()+"");
            		NegotiationInspector.this.validate();
            }
        });

		tabs.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        		selected = tabs.getSelectedIndex();
            		lineChart.getCategoryPlot().setDataset(createDataset(slider.getValue()));
	        }
	    });
        Box p = new Box(BoxLayout.X_AXIS);
        p.add(new JLabel("Time:"));
        p.add(slider);
        p.add(label);
        this.getContentPane().add(p, BorderLayout.SOUTH);
	}

	private DefaultCategoryDataset createDataset(int hour) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<AID, Map<Party,List<Offer>>> negotiations = getToDisplay().getNegotiationsForHour(hour);
		if(!negotiations.keySet().isEmpty()) {
			AID agent = (AID) negotiations.keySet().toArray()[selected];
			for(Party p : negotiations.get(agent).keySet()) {
				for(Offer o : negotiations.get(agent).get(p)) {
					dataset.addValue(o.getOfferValue(Strategy.Item.PRICE), (p==Party.SELF)?"Me":agent.getLocalName(), negotiations.get(agent).get(p).indexOf(o)+"");
				}
			}
		}

		return dataset;
	}

	public AgentDailyNegotiationThread getToDisplay() {
		return toDisplay;
	}

	public void setToDisplay(AgentDailyNegotiationThread agentDailyNegotiationThread) {
		this.toDisplay = agentDailyNegotiationThread;
	}
}