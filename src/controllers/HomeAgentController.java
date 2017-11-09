package controllers;

import javax.swing.JOptionPane;

import org.jfree.ui.RefineryUtilities;

import annotations.Callable;
import interfaces.Object2ApplianceAgentInterface;
import interfaces.Object2HomeAgentInterface;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import model.AgentDailyNegotiationThread;
import ui.NegotiationInspector;

public class HomeAgentController extends TradeAgentController {

	@Callable
	public void ShowNegotiation() {
		try {
			AgentController ac = this.getInnerController();
			Object2HomeAgentInterface O2AInterface = ac.getO2AInterface(Object2HomeAgentInterface.class);
			AgentDailyNegotiationThread dnt = O2AInterface.getDailyThread();
			if(dnt!=null) {
				String title = this.getDescriptor().getName() + "'s Negotiations";
				NegotiationInspector chart = new NegotiationInspector(title,  title,dnt);
				chart.pack();
				RefineryUtilities.centerFrameOnScreen(chart);
				chart.setVisible(true);
			}else {
				JOptionPane.showMessageDialog(null, "There not seemed to be any negotiation going on here");
			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DiscoverRetailers() {
		try {
			AgentController ac = this.getInnerController();
			Object2HomeAgentInterface O2AInterface = ac.getO2AInterface(Object2HomeAgentInterface.class);
			O2AInterface.discoverRetailers();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
