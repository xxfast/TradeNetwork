package interfaces;

import jade.core.AID;

public interface Object2ApplianceAgentInterface extends Object2TradeAgentInterface {
	public AID getSchedulerAgent();
	public void setSchedulerAgen(AID schedulerAgen);
}
