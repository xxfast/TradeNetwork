package model;

import java.util.Date;

import FIPA.DateTime;
import jade.lang.acl.ACLMessage;

public class NotificationMessage extends ACLMessage {
	
	private int units;
	private int hours; 
	private DateTime on;
	
	public NotificationMessage(int units,int hours, DateTime on){
		super(ACLMessage.INFORM);
		this.setUnits(units);
		this.setHours(hours);
		this.setOn(on);
	}
	
	public NotificationMessage(ACLMessage message){
		super(ACLMessage.INFORM);
		setContent(message.getContent());
	}
	
	@Override
	public String getContent(){
		return getUnits() + ":" + getHours() + ":" + getOn().hour;
		
	}
	
	@Override
	public void setContent(String content){
		super.setContent(content);
		String[] messageBits = content.split(":");
		setUnits( Integer.valueOf(messageBits[0]));
		setHours( Integer.valueOf(messageBits[1]));
		DateTime toSet = new DateTime();
		toSet.hour = Short.valueOf(messageBits[1]);
		setOn(toSet);
	}

	private int getUnits() {
		return units;
	}

	private void setUnits(int units) {
		this.units = units;
	}

	private int getHours() {
		return hours;
	}

	private void setHours(int hours) {
		this.hours = hours;
	}

	private DateTime getOn() {
		return on;
	}

	private void setOn(DateTime on) {
		this.on = on;
	}
	
}
