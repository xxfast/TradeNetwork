package interfaces;

import agent.HomeAgent.NegotiatingBehaviour;

public interface Object2HomeAgentInterface extends Object2TradeAgentInterface {
	 NegotiatingBehaviour getNegotiation();
	 void discoverRetailers();
}
