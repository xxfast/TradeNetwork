package descriptors;

import jade.core.AID;

public class HomeAgentDescriptor extends TradeAgentDescriptor {
	
	public String getDescription() {
		return String.format("[HomeAgent:" +  getName());
	}

	@Override
	public String toString() {
		return getDescription();
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{};
		return toReturn;
	}
	
}
