package descriptors;

import java.io.Serializable;

public abstract class TradeAgentDescriptor implements Serializable {
	
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return String.format("[TradeAgent: "+ getName()+ "]");
	}
	
	public Object[] toArray() {
		Object[] toReturn = new Object[]{};
		return toReturn;
	}
	
}
