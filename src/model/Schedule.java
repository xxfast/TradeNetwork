package model;

import jade.core.AID;
import java.util.List;
import java.util.ArrayList;

public class Schedule {
	private List<ArrayList<Slot>> time = new ArrayList<ArrayList<Slot>>();
	
	public Schedule(int days){
		for(int i=0;i<days;i++){
			time.add(new ArrayList<Slot>());
		}
	}
	
	public List<ArrayList<Slot>> getTime() {
		return time;
	}
	
	public static class Slot{
		private AID agent; 
		private int amount;
		
		public Slot(AID agent, int amount){
			this.agent = agent;
			this.amount = amount;
		}
		
		public AID getAgent() {
			return agent;
		}
		public void setAgent(AID agent) {
			this.agent = agent;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
	}
}
