package model;

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import negotiation.Strategy;

public class Offer {
	//factory for managing offers
	private final String DELIMETER=";";
	private final String SEPERATOR="-";
	private Map<Strategy.Item,Double> offerValues;
	private String owner="";
	
	
	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public ACLMessage createACLMessage(int performative){
		ACLMessage toReturn = new ACLMessage(performative);
		toReturn.setContent(getContent());
		if(!owner.equals(""))
			toReturn.setSender(new AID(owner,AID.ISLOCALNAME));
		return toReturn;
	}
	
	
	public Offer()
	{
		this.offerValues= new HashMap<>();
		
	}
	public Offer(Map<Strategy.Item,Double> offervals){
		this.offerValues=offervals;
	}
	
	
	public Offer(ACLMessage message){
		offerValues= new HashMap<Strategy.Item, Double>();
		setContent(message.getContent());
		owner=message.getSender().getLocalName();
	}
	
	public String getContent(){
		StringBuilder build= new StringBuilder();
		for(Map.Entry<Strategy.Item, Double> entry : offerValues.entrySet())	
		{
			build.append(entry.getKey()).append(SEPERATOR).append(String.format("%.2f", entry.getValue())).append(DELIMETER);
		}
		return build.toString();
	}
	
	public void setContent(String content){
		String[] values = content.split(DELIMETER);
		for(String val:values)
		{
			String[] pairs=val.split(SEPERATOR);
			String k=pairs[0];
			String v=pairs[1];
			offerValues.put(Strategy.Item.valueOf(k), Double.valueOf(v));
		}
		
	}

	public Map<Strategy.Item, Double> getOfferValues() {
		return offerValues;
	}
	
	public double getOfferValue(Strategy.Item offerItem)
	{
		return offerValues.get(offerItem);
	}
	

}
