package agent;

import java.util.ArrayList;
import java.util.List;

import FIPA.DateTime;
import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import model.Demand;
import model.Schedule;
import jade.domain.FIPAException;
import jade.domain.FIPANames;

public class SchedulingAgent extends Agent {
	
	private Schedule schedule = new Schedule(7); 
	
	protected void setup() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType("SUPPLY");
		sd.setName("SCHEDULER");
		register(sd);
		
		MessageTemplate demandTemplate = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
	
		//To respond to Appliance Agent
		
		addBehaviour(new AchieveREResponder(this, demandTemplate) {
			
			private Demand recievedDemand; 
			
			protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException{
					recievedDemand = new Demand(request);
//					System.out.println(getLocalName() + ": DEMAND received demand from " + request.getSender().getName() + ". Demand is " + recievedDemand.getUnits() + " unit(s) from " + recievedDemand.getTime().hour + "h for " + recievedDemand.getDuration() + " Hrs");
//					System.out.println(getLocalName() + ": OK ");
					ACLMessage agree = request.createReply();
					agree.setPerformative(ACLMessage.AGREE);
					return agree;
			}

			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException{
				if(SchedulingAgent.this.schedule(recievedDemand.getUnits(),recievedDemand.getTime(),recievedDemand.getDuration())){	
//					System.out.println(getLocalName() + ": YES Scehduled the demand succesfully");
					ACLMessage inform = request.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}else{
//					System.out.println(getLocalName() + ": Action failed, informing initiator");
					throw new FailureException("unexpected-error");
				}
			}
		});
		
		MessageTemplate requestTemplate = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		
		//To respond to Home Agent
		
		addBehaviour(new AchieveREResponder(this, requestTemplate) {
			
			private Demand recievedRequest; 
			
			protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException{
				recievedRequest = new Demand(request);
					System.out.println(getLocalName() + ": REQUEST received request from " + request.getSender().getName() + ". For the time slot of " + recievedRequest.getTime().hour + "h");
					System.out.println(getLocalName() + ": OK ");
					ACLMessage agree = request.createReply();
					agree.setPerformative(ACLMessage.AGREE);
					return agree;
			}

			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException{
				
				Demand inform = new Demand(recievedRequest.getTime());
				List<Integer> demands = SchedulingAgent.this.getSchedule().getTime().get(recievedRequest.getTime().hour);
				int total = 0;
				for(Integer demand : demands){
					total+= demand;
				}
				inform.setUnits(total);
				System.out.println(getLocalName() + ": YES Send the total demand for "+ recievedRequest.getTime().hour+"h which was " +total + " unit, successfully");
				return inform.createACLMessage(ACLMessage.INFORM);
			}
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
	
	public Schedule getSchedule(){
		return schedule;
	}
	
	public boolean schedule(int amount, DateTime time, int duration){
		schedule.getTime().get(time.hour).add(amount);
		return true;
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

}
