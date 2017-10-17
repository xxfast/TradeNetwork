package descriptors;

import java.io.Serializable;

import annotations.Adjustable;

@Adjustable(label = "An agent representing a genric trade agent")
public abstract class TradeAgentDescriptor implements Serializable {
	
	@Adjustable(label = "Name of the Agent")  private String name;
	
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
