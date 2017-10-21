package descriptors;

import annotations.Adjustable;
import jade.core.AID;

@Adjustable 
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
