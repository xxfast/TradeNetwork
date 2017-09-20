package agent;

import jade.core.AID;
import jade.core.Agent;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.proto.AchieveREResponder;
import model.Schedule;
import jade.domain.FIPAException;

public class SchedulingAgent extends Agent {
	
	private Schedule schedule = new Schedule(7); 
	
	protected void setup() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType("SUPPLY");
		sd.setName("SCHEDULER");
		register(sd);
		addBehaviour(new AchieveREResponder(){
			
		});
	}

	void register(ServiceDescription sd) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd); 
		
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	void schedule( int when, AID by, int amount){
		schedule.getTime().get(when).add(new Schedule.Slot(by, amount));
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

}
