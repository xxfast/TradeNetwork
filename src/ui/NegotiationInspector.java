package ui;

import org.jfree.chart.ChartPanel;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import jade.core.AID;
import model.AgentDailyNegotiationThread;
import model.AgentDailyNegotiationThread.Party;
import model.Offer;
import negotiation.NegotiationThread;
import negotiation.Strategy;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class NegotiationInspector extends ApplicationFrame {
	
	private AgentDailyNegotiationThread toDisplay;

	public NegotiationInspector(String applicationTitle, String chartTitle, AgentDailyNegotiationThread toDisplay) {
		super(applicationTitle);
		setToDisplay(toDisplay);
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle, "Iteration", "Price", createDataset(),
				PlotOrientation.VERTICAL, true, true, false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<AID, Map<Party,List<Offer>>> negotiations = getToDisplay().getNegotiationsForHour(0);
		for(AID agent : negotiations.keySet()) {
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