package controllers;

import javax.swing.JOptionPane;

import org.jfree.ui.RefineryUtilities;

import agent.HomeAgent.NegotiatingBehaviour;
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
			NegotiatingBehaviour nb = O2AInterface.getNegotiation();
			if(nb!=null) {
				String title = nb.getAgent().getLocalName() + "'s Negotiations";
				NegotiationInspector chart = new NegotiationInspector(title,  title,nb.getDailyThread());
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
}
