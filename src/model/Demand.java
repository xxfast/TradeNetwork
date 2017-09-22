package model;

import java.util.Date;

import FIPA.DateTime;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Demand {
	
	private int units;
	private DateTime time;
	private int duration; 
	
	public ACLMessage createACLMessage(int performative){
		ACLMessage toReturn = new ACLMessage(performative);
		toReturn.setContent(getContent());
		return toReturn;
	}
	
	public static MessageTemplate Template  = MessageTemplate.and(
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
			MessageTemplate.MatchPerformative(ACLMessage.INFORM));
	
	public Demand(DateTime time){
		init(0, time, 1);
	}
	
	public Demand(int units, DateTime time){
		init(units, time, 1);
	}
	
	public Demand(int units, DateTime time, int duration){
		init(units, time, duration);
	}
	
	private void init(int units, DateTime time, int duration){
		this.setUnits(units);
		this.setTime(time);
		this.setDuration(duration);
	}
	
	public Demand(ACLMessage message){
		init(0, time, 1);
		setContent(message.getContent());
	}
	
	public String getContent(){
		return getUnits() + ":" + getTime().hour + ":" + getDuration();
		
	}
	
	public void setContent(String content){
		String[] messageBits = content.split(":");
		setUnits( Integer.valueOf(messageBits[0]));
		setDuration( Integer.valueOf(messageBits[1]));
		DateTime toSet = new DateTime();
		toSet.hour = Short.valueOf(messageBits[1]);
		setTime(toSet);
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

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime on) {
		this.time = on;
	}
	
}
