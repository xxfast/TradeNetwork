package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import model.Demand;
import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.negotiator.AgentNegotiator.OfferStatus;
import negotiation.negotiator.HomeAgentNegotiator;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.TimeWeightedFunction;
import negotiation.tactic.TimeWeightedPolynomial;

public class HomeAgent extends TradeAgent {
	private final boolean INC=false;//customer mentality
	private double maxNegotiationTime=10;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private Random rand;
	
	private HomeAgentNegotiator negotiator;

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
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
			doDelete();
		}
		
		
		rand = new Random();
		//retrieve scheduler form arguments
		Object[] args = getArguments();
		if(args.length!=1)
		{
			say("Need to specify schedule agent in arguments");
			doDelete();
		}
		myscheduler = new AID((String) args[0],AID.ISLOCALNAME);
		
		retailers= new ArrayList<>();	
		//get agents with retailer service
		DFAgentDescription[] agents = getServiceAgents("RetailerAgent");
		for(DFAgentDescription agent : agents)
		{
			retailers.add(agent.getName());
		}
		
		addBehaviour(new TickerBehaviour(this,5000) {
			
			@Override
			public void onTick() {
				// TODO Auto-generated method stub
				// get scheduler agent from AMS

				
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
		
	
	protected Random getRandomizer()
	{
		return rand;
	}
	
	
	protected void setupHomeNegotiator()
	{
		say("Setting up negotiator");
		//setup strategies
		//obtain from source params
		//simply intializing params
		double WFParamK=0.3;
		double WFParamBeta=0.5;  //Beta <1 competitive Beta >1 passive		
		
		//create TWfunction
		TimeWeightedFunction poly = new TimeWeightedPolynomial(WFParamK, WFParamBeta, this.maxNegotiationTime);
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		double timeTWeight=1;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(timeTWeight));
		try {
			priceStrat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			say("ERROR "+e.getMessage());
			
		}
		
		//add Strategy to negotiator's strategies
		ArrayList<Strategy> strats=new ArrayList<>();
		strats.add(priceStrat);
		
		//create score weights for negotiating items
		//ATM only price is considered so given full weight
		Map<Strategy.Item,Double>scoreWeights= new HashMap<>();
		//add only price item
		scoreWeights.put(Item.PRICE, new Double(1));
		
		//create price range for each offer item-obtain from source
		Map<Strategy.Item,Issue> itemissue = new HashMap<>();
		//only add price issue since we are only focusing on price
		itemissue.put(Strategy.Item.PRICE, new Issue(40, 20));
		
		//create negotiator with params
		this.negotiator= new HomeAgentNegotiator( this.maxNegotiationTime, itemissue, strats, scoreWeights);
		
		
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
			//setup home negotiator
			setupHomeNegotiator();
			// TODO Auto-generated constructor stub
		}
		@Override
		protected Vector prepareCfps(ACLMessage msg)
		{
			say("Gonna make call for proposal");
			//retrieve demand msg from datastore
			String demandMsgKey = (String) ((AchieveREInitiator) getParent()).REPLY_KEY;
			ACLMessage demandMsg=(ACLMessage) getDataStore().get(demandMsgKey);		
			
			ACLMessage quoteRequest = null;
			if(demandMsg!=null)
			{
				
				//construct quote request to send to retailers
				schedDemand =new Demand(demandMsg);
				//TODO- based on demand determine a min and max price
				//create initial offer from Negotiator
				Offer offer=negotiator.generateOffer();
				
				quoteRequest= offer.createACLMessage(ACLMessage.CFP);
				quoteRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
				
				say("intial offer sent "+offer.getContent());	
				for(AID agent : retailAgents)
				{
					quoteRequest.addReceiver(agent);
				}		
				
			}
			else
				myLogger.log(Logger.SEVERE,"NULL Message sent for Quote");
			
			//set negotiator to next iteration
			negotiator.nextIteration();
			
			return super.prepareCfps(quoteRequest);
		}
		/*get all PROPOSE msgs and compare proposals
		send back accept and rejects*/
		@Override
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			// TODO Auto-generated method stub
			//get all proposals
//			say("Proposals Recieved");
			ArrayList<ACLMessage> proposals = new ArrayList<>();
			for(Object o : responses)
			{
				ACLMessage msg = (ACLMessage) o;
				if(msg.getPerformative()==ACLMessage.PROPOSE)
				{
					proposals.add(msg);
				}
			}
			//convert all proposals to a list of offer objects
			Map<ACLMessage,Offer> msgtooffer = new HashMap<>();
			List<Offer> offers = new ArrayList<>();
			for (Object obj: responses)
			{
				ACLMessage msg=(ACLMessage)obj;
				Offer off = new Offer(msg);
				offers.add(off);
				msgtooffer.put(msg, off);
			}
			//ask negotiator to interpret proposals
			Map<Offer,HomeAgentNegotiator.OfferStatus> offerstatus=negotiator.interpretOffers(offers);
			//handle response based on offer status
			//vector for counter offers
			Vector<ACLMessage> nextMessages= new Vector<>();
			//go prepare every acl message reply based on offer status
//			say("messages in recieved for prop "+responses.size());
			for(Map.Entry<ACLMessage, Offer> entry:msgtooffer.entrySet())
			{
				OfferStatus status=offerstatus.get(entry.getValue());
				//handle REJECT
				if(status.equals(OfferStatus.REJECT))
				{
//					say("Reject "+entry.getKey().getSender().getLocalName());
					acceptances.addElement(createReply(ACLMessage.REJECT_PROPOSAL, entry.getKey()));
				}
					
				
				//handle ACCEPT
				if(status.equals(OfferStatus.ACCEPT))
				{
//					say("Accept "+entry.getKey().getSender().getLocalName());
					acceptances.addElement(createReply(ACLMessage.ACCEPT_PROPOSAL, entry.getKey()));
				}
				
				//handle COUNTER
				if(status.equals(OfferStatus.COUNTER))
				{

					//construct reply with counter offer
					Offer counter=negotiator.getLastOffer();
					say("made counter offers "+counter.getContent());
					nextMessages.addElement(createReply(ACLMessage.CFP, entry.getKey(), counter.getContent()));
				}
				
			}
			
			//send next set of offers if negotiating
			if(nextMessages.size()>0)
			{
				newIteration(nextMessages);
			}
			
			
		
		}
		@Override
		protected void handleAllResultNotifications(Vector resultNotifications)
		{
			for(Object notification: resultNotifications)
			{
				ACLMessage msg =(ACLMessage) notification;
				System.out.println("Inform recieved from "+msg.getSender().getLocalName());
				System.out.println("Msg is "+msg.getContent());
			}
		}
		 protected ACLMessage createReply(int perfomative,ACLMessage msg,String content)
		 {
			ACLMessage reply=msg.createReply();
			reply.setPerformative(perfomative);
			
			reply.setContent(content);
			return reply;
		 }
		 protected ACLMessage createReply(int perfomative,ACLMessage msg)
		 {
			ACLMessage reply=msg.createReply();
			reply.setPerformative(perfomative);		
			return reply;
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
