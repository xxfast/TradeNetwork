package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import FIPA.DateTime;
import annotations.Adjustable;
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
import model.AgentDailyNegotiationThread;
import model.Demand;
import model.History;
import model.Offer;
import model.AgentDailyNegotiationThread.Party;
import negotiation.NegotiationThread;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.baserate.HomeBound;
import negotiation.negotiator.AgentNegotiator;
import negotiation.negotiator.AgentNegotiator.OfferStatus;
import negotiation.negotiator.HomeAgentNegotiator;
import negotiation.tactic.BehaviourDependentTactic;
import negotiation.tactic.ResourceDependentTactic;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.behaviour.AverageTitForTat;
import negotiation.tactic.timeFunction.ResourceAgentsFunction;
import negotiation.tactic.timeFunction.TimeWeightedFunction;
import negotiation.tactic.timeFunction.TimeWeightedPolynomial;
import negotiation.baserate.BoundCalc;
import negotiation.baserate.HomeBound;
import simulation.Simulation;
import model.History;

public class HomeAgent extends TradeAgent {
	private final boolean INC=false;//customer mentality
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private Random rand;
	
	
	private Map<AID,HomeAgentNegotiator> negotiators;
	private AgentDailyNegotiationThread dailyThread;

	private ArrayList<Demand> messages = new ArrayList<>();
	private AID myscheduler;
	private List<AID> retailers;
	
	private int agentHour;
	//params needed to setup negotiators
	//coming from args
	@Adjustable(label="Max Iterations")
	private double maxNegotiationTime=8;
	@Adjustable(label="Parameter K")
	private double ParamK=0.01;
	@Adjustable(label="Parameter Beta")
	private double ParamBeta=0.5;
	
	private double tacticTimeWeight=0.6;
	private double tacticResourceWeight=0.2;
	private double tacticBehaviourWeight=0.2;
	private int behaviourRange=2;
	//private structure
	
	protected void setup() {

		
		agentHour=0;
		rand = new Random();
		setAgentProperties();
		dailyThread= new AgentDailyNegotiationThread();
		retailers= new ArrayList<>();	
		//get agents with retailer service
		DFAgentDescription[] agents = getServiceAgents("RetailerAgent");
		for(DFAgentDescription agent : agents)
		{
			retailers.add(agent.getName());
		}
		
		negotiators= new HashMap<>();
		
		addBehaviour(new TickerBehaviour(this,Simulation.Time) {
			
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
		
	protected void setAgentProperties()
	{
		//retrieve scheduler form arguments
		Object[] args = getArguments();
		if(args.length<1)
		{
			say("Need to specify schedule agent in arguments");
			doDelete();
		}
		myscheduler = new AID((String) args[0],AID.ISLOCALNAME);
		
		//retrieve max time from args
		this.maxNegotiationTime=Double.valueOf((String)args[1]);
		//retrieve K and Beta from args
		this.ParamK=Double.valueOf((String)args[2]);
		this.ParamBeta=Double.valueOf((String)args[3]);
	}
	protected Random getRandomizer()
	{
		return rand;
	}
	
	public void goNextHour()
	{
		++agentHour;
	}
	
	protected void setupHomeNegotiators(Integer activeAgents)
	{
		say("Setting up negotiators");
		//setup strategies
		
		//tactic setup
		//create TWfunction- for time dependent tactic
		TimeWeightedFunction poly = new TimeWeightedPolynomial(this.ParamK, this.ParamBeta, this.maxNegotiationTime);
		
		//create RAFunction- for resource dep tactic
		ResourceAgentsFunction rsrcFunc= new ResourceAgentsFunction(this.ParamK, activeAgents);
		
		//create behTFT- for behaviour dep tactic
		AverageTitForTat tft = new AverageTitForTat(Item.PRICE);
		
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		ResourceDependentTactic tactic2= new ResourceDependentTactic(rsrcFunc, this.INC); 
		BehaviourDependentTactic tactic3= new BehaviourDependentTactic(tft, this.behaviourRange);
		
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(this.tacticTimeWeight));
		tactics.put(tactic2, new Double(this.tacticResourceWeight));
		tactics.put(tactic3,new Double(this.tacticBehaviourWeight));
		try {
			priceStrat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			say("ERROR "+e.getMessage());
			doDelete();
			
		}
		
		//add Strategy to negotiator's strategies
		ArrayList<Strategy> strats=new ArrayList<>();
		strats.add(priceStrat);
		
		//create score weights for negotiating items
		//ATM only price is considered so given full weight
		Map<Strategy.Item,Double>scoreWeights= new HashMap<>();
		//add only price item
		scoreWeights.put(Item.PRICE, new Double(1));
		
		//get my history object-simply creating new history, TODO object shud handle loading agent history
		History history = new History(this.getLocalName());
		//create bound calc for price
		HomeBound homecacl= new HomeBound(history);
	
		
		//create negotiators with params for each retailer
		for(AID agent:retailers)
		{
			this.negotiators.put(agent, new HomeAgentNegotiator( this.maxNegotiationTime, strats, scoreWeights,homecacl));
		}
		 
		
		
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

			short agenthr=(short)agentHour;
			Demand demand= new Demand(new Short(agenthr));
			

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
		private Integer activeAgents;
		private Map<ACLMessage,Double> acceptedScores;
		private Map<ACLMessage,Double> proposalScores;
		
		public RequestQuote(Agent a, ACLMessage msg, List<AID> retailers) {
			super(a, msg);
			retailAgents=retailers;
			activeAgents=new Integer(retailers.size());
			acceptedScores= new HashMap<>();
			proposalScores= new HashMap<>();
			
			
			//setup home negotiators
			setupHomeNegotiators(activeAgents);
			//add negotiations to dailyNegotiaition threads
			for(Map.Entry<AID, HomeAgentNegotiator> entry:negotiators.entrySet())
			{
				
				dailyThread.addHourThread(agentHour, entry.getKey(), entry.getValue().getNegotiationThread());
			}
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
			Vector<ACLMessage> cfps= new Vector<>();
			if(demandMsg!=null)
			{
				
				
				//construct quote request to send to retailers
				schedDemand =new Demand(demandMsg);
				//TODO- ask negotiators to set intital issues
				for(Map.Entry<AID, HomeAgentNegotiator> entry:negotiators.entrySet())
				{
					//create offer objects to set initial issue
					Offer off = new Offer();
					off.setOwner(entry.getKey().getLocalName());
					off.setDemand(schedDemand);
					entry.getValue().setInitialIssue(off);
					System.out.println("intial issue for "+entry.getKey().getLocalName()+" issue "+entry.getValue().getItemIssue().get(Item.PRICE));
				}				 
				 
				//create initial offer for each retailer from negotiators
				Offer offer;
				for(Map.Entry<AID,HomeAgentNegotiator> entry :negotiators.entrySet())
				{
					offer=entry.getValue().generateOffer();
					//attach demand to offer
					offer.setDemand(schedDemand);
					//send msg
					quoteRequest= offer.createACLMessage(ACLMessage.CFP);
					quoteRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
					say("intial offer sent to agent"+entry.getKey().getLocalName()+" val"+offer.getContent());
					quoteRequest.addReceiver(entry.getKey());
					cfps.addElement(quoteRequest);
					//set negotiator to next iteration
					entry.getValue().nextIteration();
				}			
				
			}
			else
				myLogger.log(Logger.SEVERE,"NULL Message sent for Quote");
			
			
			
			return cfps;
		}
		
		@Override
		protected void handlePropose(ACLMessage propose, Vector acceptances) {
			// TODO Auto-generated method stub
			//evaluate proposal for retailer
			
			//get retailer
			AID ret=propose.getSender();
			Offer off = new Offer(propose);
			OfferStatus status=negotiators.get(ret).interpretOffer(off);
			//store the scores for each proposal evaluated
			proposalScores.put(propose, negotiators.get(ret).evalScore(off));
			ACLMessage reply = propose.createReply();
			if(status.equals(OfferStatus.REJECT))
			{
				//reject proposal 
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				reply.setContent("Times up");
				say("Times up -Rejected "+propose.getSender().getLocalName()+" offer "+propose.getContent());
				//decrement activeagents
				--activeAgents;
				//remove negotiator object for retailer
			}
			
			
			if(status.equals(OfferStatus.ACCEPT))
			{
				
				//handle accept proposal in handleResponse
				acceptedScores.put(propose, negotiators.get(ret).evalScore(off));
				return;
			}
			
			if(status.equals(OfferStatus.COUNTER))
			{
				reply.setPerformative(ACLMessage.CFP);
				//set reply with counter offer						
				reply.setContent(negotiators.get(ret).getLastOffer().getContent());
				say("counter offer to "+propose.getSender().getLocalName()+" val "+reply.getContent());
			}				
			acceptances.addElement(reply);
			
		}
		@Override
		protected void handleRefuse(ACLMessage refuse) {
			// TODO add logic to refuse, decrement activeagents 
			activeAgents--;
			say("Rejected from "+refuse.getSender().getLocalName());
			super.handleRefuse(refuse);
		}
		/*get all PROPOSE msgs and compare proposals
		send back accept and rejects*/
		@Override
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			// TODO Auto-generated method stub
			//handle accepted offers
			if(acceptedScores.size()>0)
			{
				//select one with best score
				double bestscore=0;
				ACLMessage bestproposal=null;
				for(Map.Entry<ACLMessage, Double> entry:acceptedScores.entrySet())
				{
					if(entry.getValue()>bestscore)
					{
						bestscore=entry.getValue();
						bestproposal=entry.getKey();
					}
				}
				//create rejections for any counter offers since no point negotiating further
				//maybe can later capitalize on opponents conceding mentality by holding the price for other negotiations
				for(Object obj :acceptances)
				{
					ACLMessage msg=(ACLMessage)obj;
					if(msg.getPerformative()!=ACLMessage.REJECT_PROPOSAL)
					{
						//set it to reject
						msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
					}
				}
				//create rejections for accepted
				for(Map.Entry<ACLMessage, Double> entry:acceptedScores.entrySet())
				{
					ACLMessage reply=entry.getKey().createReply();
					reply.setContent(entry.getKey().getContent());
					if(entry.getKey().getSender().equals(bestproposal.getSender()))
					{
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					}
					else
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					
					acceptances.addElement(reply);
				}			
				
			}
			//check if all rejected- we select best offer we cn get
			boolean allRej=true;
			for(Object obj :acceptances)
			{
				ACLMessage msg = (ACLMessage) obj;
				//check if all rejected
				if(msg.getPerformative()!=ACLMessage.REJECT_PROPOSAL)
				{
					allRej=false;
					break;
				}				
				
			}
			if(allRej)
			{
				//select and accept best possible offer
				handleAllRejections(acceptances);
			}
			//iterate through acceptances and determine if a new iteration of CFPs 
			boolean isNextIter=false;
			for(Object obj:acceptances)
			{
				ACLMessage msg=(ACLMessage) obj;
				if(msg.getPerformative()==ACLMessage.CFP)
				{
					isNextIter=true;
					break;
				}
				
					
			}
			if(isNextIter)
				newIteration(acceptances);
	
			//clear proposalscores for next iteration
			proposalScores.clear();
			
			
		
		}
		@Override
		protected void handleAllResultNotifications(Vector resultNotifications)
		{
			for(Object notification: resultNotifications)
			{
				ACLMessage msg =(ACLMessage) notification;
				System.out.println("Inform recieved from "+msg.getSender().getLocalName());
				System.out.println("Msg is "+msg.getContent());
//				NegotiationThread t = negotiators.get(msg.getSender()).getNegotiationThread();
//				say(t.toString());
			}
			
			
			
		}
		
		public void handleAllRejections(Vector acceptances)
		{
			//select one with best score
			double bestscore=0;
			ACLMessage bestproposal=null;
			for(Map.Entry<ACLMessage, Double> entry:proposalScores.entrySet())
			{
				if(entry.getValue()>bestscore)
				{
					bestscore=entry.getValue();
					bestproposal=entry.getKey();
				}
				
			} 
			System.out.println("Accepting "+bestproposal.getSender().getLocalName()+" since best of all offers");
			//accept best proposal and reject rest
			//clear acceptances
			acceptances.clear();
			for(Map.Entry<ACLMessage, Double> entry:proposalScores.entrySet())
			{
				ACLMessage reply=entry.getKey().createReply();
				reply.setContent(entry.getKey().getContent());
				if(entry.getKey().getSender().equals(bestproposal.getSender()))
				{
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				}
				else
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				
				acceptances.addElement(reply);
			}
		}
		
		 @Override
		public int onEnd() {
			// TODO Auto-generated method stub
//			 System.out.println(dailyThread.toString());
				goNextHour();
			return super.onEnd();
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
		 
		
	}
	

}
