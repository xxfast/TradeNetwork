package interfaces;

import jade.core.AID;

public interface Object2ApplianceAgentInterface extends Object2TradeAgentInterface {
	public AID getHome();
	public void setHome(AID home);
}
