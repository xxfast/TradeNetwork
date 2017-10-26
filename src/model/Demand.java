package model;

import java.io.Serializable;

import FIPA.DateTime;
import annotations.Adjustable;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@Adjustable(label="Demand Unit")
public class Demand implements Serializable{
	
	@Adjustable(label="Required number of units") private int units = 0;
	@Adjustable(label="When is it required?")  private short time = 0 ;
	@Adjustable(label="For how long is it required")  private int duration = 1; 
	
	public ACLMessage createACLMessage(int performative){
		ACLMessage toReturn = new ACLMessage(performative);
		toReturn.setContent(getContent());
		return toReturn;
	}
	
	public static MessageTemplate Template  = MessageTemplate.and(
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
			MessageTemplate.MatchPerformative(ACLMessage.INFORM));
	
	public Demand(){
	}
	
	public Demand(Short time){
		init(0, time, 1);
	}
	
	public Demand(int units){
		init(units, (short) 0, 1);
	}
	
	public Demand(int units, Short time){
		init(units, time, 1);
	}
	
	public Demand(int units, Short time, int duration){
		init(units, time, duration);
	}
	
	private void init(int units, Short time, int duration){
		this.setUnits(units);
		this.setTime(time);
		this.setDuration(duration);
	}
	
	public Demand(ACLMessage message){
		init(0, time, 1);
		setContent(message.getContent());
	}
	
	public String getContent(){
		return getUnits() + ":" + getTime() + ":" + getDuration();
		
	}
	
	public void setContent(String content){
		String[] messageBits = content.split(":");
		setUnits( Integer.valueOf(messageBits[0]));

		setDuration( Integer.valueOf(messageBits[2]));

		setTime( Short.valueOf(messageBits[1]));
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int hours) {
		this.duration = hours;
	}

	public Short getTime() {
		return time;
	}

	public void setTime(Short on) {
		this.time = on;
	}

	
}
