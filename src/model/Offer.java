package model;

import java.util.HashMap;
import java.util.Map;

import FIPA.DateTime;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import negotiation.Strategy;

public class Offer {
	//factory for managing offers
	private final String SPLITTER="/";
	private final String DELIMETER=";";
	private final String SEPERATOR="-";
	private Map<Strategy.Item,Double> offerValues;
	private Demand demand;
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
		demand=new Demand(new DateTime());
		
	}
	public Offer(Map<Strategy.Item,Double> offervals){
		this.offerValues=offervals;
		this.demand= new Demand(1,new DateTime());
	}
	
	public Offer(Map<Strategy.Item,Double> offervals,Demand demand){
		this.offerValues=offervals;
		this.demand= demand;
	}
	
	
	public Offer(ACLMessage message){
		offerValues= new HashMap<Strategy.Item, Double>();
		demand= new Demand(new DateTime());
		setContent(message.getContent());
		owner=message.getSender().getLocalName();
	}
	
	public String getContent(){
		StringBuilder build= new StringBuilder();
		for(Map.Entry<Strategy.Item, Double> entry : offerValues.entrySet())	
		{
			build.append(entry.getKey()).append(SEPERATOR).append(String.format("%.2f", entry.getValue())).append(DELIMETER);
		}
		build.append(SPLITTER);
		build.append(demand.getContent());
		return build.toString();
	}
	
	public void setContent(String content){
		String[] split = content.split(SPLITTER);
		String offers=split[0];
		String demand=split[1];
		//set offer values
		String[] values = offers.split(DELIMETER);
		for(String val:values)
		{
			String[] pairs=val.split(SEPERATOR);
			String k=pairs[0];
			String v=pairs[1];
			offerValues.put(Strategy.Item.valueOf(k), Double.valueOf(v));
		}
		//set demand
		this.demand.setContent(demand);
		
	}
	
	public Offer clone()
	{
		Map<Strategy.Item,Double> itemVals= new HashMap<>();
		for(Map.Entry<Strategy.Item, Double> entry : offerValues.entrySet())
		{
			itemVals.put(entry.getKey(), entry.getValue());
		}
		Offer clone = new Offer(itemVals);
		clone.setOwner(this.owner);
		clone.demand=new Demand(this.demand.getUnits(), this.demand.getTime(), this.demand.getDuration());
		return clone;
	}

	public Demand getDemand() {
		return demand;
	}


	public void setDemand(Demand demand) {
		this.demand = demand;
	}


	public Map<Strategy.Item, Double> getOfferValues() {
		return offerValues;
	}
	
	public double getOfferValue(Strategy.Item offerItem)
	{
		return offerValues.get(offerItem);
	}


	@Override
	public String toString() {
		return "Offer [offerValues=" + offerValues + ", owner=" + owner + "]";
	}
	
	
	

}
