package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import FIPA.DateTime;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.proto.ContractNetInitiator;
import jade.util.Logger;
import jade.util.leap.Iterator;

import java.util.Map;
import model.Demand;

public class HomeAgent extends TradeAgent {

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private Random rand;

	private ArrayList<Demand> messages = new ArrayList<>();
	private AID myscheduler;
	private List<AID> retailers;
	protected void setup() {
		// Registration with the DF
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("HomeAgent");
		sd.setName(getName());
		sd.setOwnership("TradeNetwork");
		dfd.setName(getAID());
		dfd.addServices(sd);
		rand = new Random();
		Object[] args = getArguments();
		if(args.length!=1)
		{
			say("Need to specify schedule agent in arguments");
			doDelete();
		}
		myscheduler = new AID((String) args[0],AID.ISLOCALNAME);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
			doDelete();
		}
		retailers= new ArrayList<>();
		
		
		addBehaviour(new TickerBehaviour(this,5000) {
			
			@Override
			public void onTick() {
				// TODO Auto-generated method stub
				// get scheduler agent from AMS

				//get agents with retailer service
				DFAgentDescription[] agents = getServiceAgents("RetailerAgent");
				for(DFAgentDescription agent : agents)
				{
					retailers.add(agent.getName());
				}
				
					
				if(myscheduler!=null)
				{
//					System.out.println("intiated behaviour");
					AchieveREInitiator req = new RequestDemand(myAgent, new ACLMessage(ACLMessage.REQUEST), myscheduler);
					if(retailers.size()>0)
					{
						req.registerHandleInform(new RequestQuote(myAgent, null, retailers));
						addBehaviour(req);
					}
						
				}
										
					
				}
				
			
		});
		
	}
	public static void printMap(Map mp) {
	   
	}
	
	public Random getRandomizer()
	{
		return rand;
	}
	
	
	
	public class RequestDemand extends AchieveREInitiator {
		private AID mySchedulerAgent;

		public RequestDemand(Agent a, ACLMessage msg, AID scheduler) {
			super(a, msg);
			mySchedulerAgent = scheduler;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Vector prepareRequests(ACLMessage request) {
			// construct request to be sent to scheduler
			System.out.println("Sending message");
			DateTime time = new DateTime();
			time.hour=0;
			Demand demand= new Demand(time);
			ACLMessage demReq=demand.createACLMessage(ACLMessage.REQUEST);
			demReq.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			demReq.addReceiver(mySchedulerAgent);
			demReq.setSender(myAgent.getAID());
//			request.setPerformative(ACLMessage.REQUEST);
//			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//			
//			request.setSender(myAgent.getAID());
//			request.addReceiver(mySchedulerAgent);
//			String content = "demand for (18:00,1)";
//			request.setContent(content);
			// TODO Auto-generated method stub
			return super.prepareRequests(demReq);
		}

		


		
	}
	
	public class RequestQuote extends ContractNetInitiator
	{
		private List<AID> retailAgents;
		private int myPrice;
		private Demand schedDemand=null;
		public RequestQuote(Agent a, ACLMessage msg, List<AID> retailers) {
			super(a, msg);
			retailAgents=retailers;
			// TODO Auto-generated constructor stub
		}
		@Override
		protected Vector prepareCfps(ACLMessage msg)
		{
			System.out.println("Gonna make call for proposal");
			//retrieve demand msg from datastore
			String demandMsgKey = (String) ((AchieveREInitiator) getParent()).REPLY_KEY;
			ACLMessage demandMsg=(ACLMessage) getDataStore().get(demandMsgKey);		
			
			ACLMessage quoteRequest = null;
			if(demandMsg!=null)
			{
				
				//construct quote request to send to retailers
				schedDemand =new Demand(demandMsg);
				quoteRequest= new ACLMessage(ACLMessage.CFP);
				quoteRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
				//simply attaching demand- TODO make a meaningful request
				quoteRequest.setContent(schedDemand.getContent());
				for(AID agent : retailAgents)
				{
					quoteRequest.addReceiver(agent);
				}		
				
			}
			else
				myLogger.log(Logger.SEVERE,"NULL Message sent for Quote");
			
			return super.prepareCfps(quoteRequest);
		}
		/*get all PROPOSE msgs and compare proposals
		send back accept and rejects*/
		@Override
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			// TODO Auto-generated method stub
			//get all proposals
			System.out.println("Proposals Recieved");
			ArrayList<ACLMessage> proposals = new ArrayList<>();
			for(Object o : responses)
			{
				ACLMessage msg = (ACLMessage) o;
				if(msg.getPerformative()==ACLMessage.PROPOSE)
				{
					proposals.add(msg);
				}
			}
			if(shouldNegotiate())
			{
				//Negotiation here
				//TODO function to go through all proposals and make new CFP
				// call for better proposals happens here
				//evaluate proposals and find which we can negotiate
				say("Negotiating");
				Map<ACLMessage,Boolean> evalProposals=evaluateProposals(proposals);
				
				Vector<ACLMessage> nextMessages= new Vector<>();
				for(Map.Entry<ACLMessage,Boolean> entry:evalProposals.entrySet())
				{
					ACLMessage nextMsg=entry.getKey().createReply();;
					if(entry.getValue())
					{
						//negotiate with the retailer of proposal
						nextMsg.setPerformative(ACLMessage.CFP);
						nextMsg.setContent(schedDemand.getContent());
					}
					else
					{
						nextMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);
					}
					nextMessages.addElement(nextMsg);					
				}
				newIteration(nextMessages);
				
			}
			else
			{
				System.out.println("Selecting best proposal");
				//select best proposal and accept; reject others
				//send replies
				ACLMessage bestProposal = getBestProposal(proposals);
				for(ACLMessage prop : proposals)
				{
					ACLMessage reply=prop.createReply();
					if(prop.getSender().equals(bestProposal.getSender()))
					{
						System.out.println("Sending acceptence for "+prop.getSender());
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						reply.setContent("All good bois");
					}
					else
					{
						System.out.println("Sending Rejection for "+prop.getSender());
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						reply.setContent("Too much man");
					}
					acceptances.addElement(reply);
				}
			}		
		
		}
		@Override
		protected void handleAllResultNotifications(Vector resultNotifications)
		{
			for(Object notification: resultNotifications)
			{
				ACLMessage msg =(ACLMessage) notification;
				System.out.println("Quote recieved from "+msg.getSender().getLocalName());
				System.out.println("Quote is "+msg.getContent());
			}
		}
		 protected boolean shouldNegotiate()
		 {
			 return true;
		 }
		 
		 protected ACLMessage getBestProposal(List<ACLMessage> proposals)
		 {
			 //randomly select and approve a proposal
			 Random rand = getRandomizer();
			 int choice=rand.nextInt(proposals.size());
			 return proposals.get(choice);
		 }
		 
		 protected HashMap<ACLMessage,Boolean> evaluateProposals(ArrayList<ACLMessage> proposals)
		 {
			 HashMap<ACLMessage, Boolean> eval= new HashMap<>();
			 for(ACLMessage msg : proposals)
			 {
				 //randomly accept/reject
				 boolean choice = rand.nextBoolean();
				 eval.put(msg, choice);
			 }
			 
			 return eval;
		 }
		
	}
	

}
