package controllers;

import annotations.Callable;
import interfaces.Object2ApplianceAgentInterface;
import jade.wrapper.StaleProxyException;

public class ApplianceAgentController extends TradeAgentController {
	
	@Callable
	public void Mute() {
		try {
			Object2ApplianceAgentInterface O2AInterface = this.getInnerController().getO2AInterface(Object2ApplianceAgentInterface.class);
			O2AInterface.setMuted(!O2AInterface.isMuted());
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
