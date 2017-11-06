package interfaces;

import model.AgentDailyNegotiationThread;

public interface Object2HomeAgentInterface extends Object2TradeAgentInterface {
	AgentDailyNegotiationThread getDailyThread();
	void discoverRetailers();
}
