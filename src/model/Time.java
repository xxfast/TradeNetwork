package model;

import jade.lang.acl.ACLMessage;

public class Time {
	private long time;
	
	public Time(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return String.valueOf(time);
	}
	
	public Time(ACLMessage toParse) {
		time = Long.valueOf(toParse.getContent());
	}
	
	public ACLMessage toACL(int performative) {
		ACLMessage toReturn = new ACLMessage(performative);
		toReturn.setContent(String.valueOf(time));
		return toReturn;
	}
}