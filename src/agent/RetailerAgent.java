package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//Used for the energy schedule
import java.util.Vector;

// Used to make the agent a ContractNetResponder Agent
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
// Used to make the agent a Service Provider Agent
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSIteratedContractNetResponder;
import jade.proto.SSResponderDispatcher;
// Used to log exceptions
import jade.util.Logger;
import model.Demand;
import model.Offer;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.negotiator.AgentNegotiator.OfferStatus;
import negotiation.negotiator.RetailerAgentNegotiator;
import negotiation.tactic.BehaviourDependentTactic;
import negotiation.tactic.ResourceDependentTactic;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.behaviour.AverageTitForTat;
import negotiation.tactic.timeFunction.ResourceAgentsFunction;
import negotiation.tactic.timeFunction.ResourceTimeFunction;
import negotiation.tactic.timeFunction.TimeWeightedFunction;
import negotiation.tactic.timeFunction.TimeWeightedPolynomial;


public class RetailerAgent extends TradeAgent {
    
	
	private final boolean INC=true;// supplier mentality
	private RetailerAgentNegotiator negotiator;

	private class EnergyUnit {
		private int time;
		private int units;
		private int cost;
		
	
	}	
	
	//params needed to setup negotiators
	//coming from args
	private double maxNegotiationTime=10;
	private double ParamK=0.01;
	private double ParamBeta=0.5;
	private double tacticTimeWeight=0.4;
	private double tacticResourceWeight=0.6;
	private double tacticBehaviourWeight=0.3;
	private int behaviourRange=2;

	private Vector<EnergyUnit> energyUnitSchedule = new Vector<EnergyUnit>();
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	// The agent's cost per unit requested by the home agent
	private int energyRate;
	
	// Max units the agent can provide in one transaction
	private int energyThreshold;
	
	// [install later] Amount of energy the retailer agent has, determines rate and threshold
	private int energyStored = 0;
	
	protected void setup() {
		
		// Sets the agent's properties (energy rate & threshold) to passed or default values
		setAgentProperties();
		
		//Describes the agent as a retail agent
		setupServiceProviderComponent();
		

		say("Retailer "+this.getName());


		
		// Template to filter messages as to only receive CFP messages for the CNR Behaviour
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );
		
		
		this.addBehaviour(new SSResponderDispatcher(this,template) {
			@Override
			protected Behaviour createResponder(ACLMessage initiationMsg) {
				// TODO Auto-generated method stub
				return new RetailerCNRBehaviour(myAgent, initiationMsg);
			}
		});
	}
	
	private void setAgentProperties() {
		Object[] args = this.getArguments();
		//set negotiation time from arguments
		this.maxNegotiationTime=Double.parseDouble((String) args[0]);
	  	
	}
	public void setupNegotiator()
	{
				
		
		//create TWfunction
		TimeWeightedFunction poly = new TimeWeightedPolynomial(this.ParamK, this.ParamBeta, this.maxNegotiationTime);
		//create RAFunction- for resource dep tactic
		ResourceTimeFunction rsrcFunc= new ResourceTimeFunction(this.ParamK, this.maxNegotiationTime);
		
		//create behTFT- for behaviour dep tactic
		AverageTitForTat tft = new AverageTitForTat(Item.PRICE);
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		ResourceDependentTactic tactic2= new ResourceDependentTactic(rsrcFunc, this.INC);
		BehaviourDependentTactic tactic3= new BehaviourDependentTactic(tft, this.behaviourRange);
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(tacticTimeWeight));
		tactics.put(tactic2, new Double(tacticResourceWeight));
//		tactics.put(tactic3, new Double(tacticBehaviourWeight));
		try {
			priceStrat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR "+e.getMessage());
			
		}
		
		//add Strategy to negotiator's strategies
		ArrayList<Strategy> strats=new ArrayList<>();
		strats.add(priceStrat);
		
		//create score weights for negotiating items
		//ATM only price is considered so given full weight
		Map<Strategy.Item,Double>scoreWeights= new HashMap<>();
		//add only price item
		scoreWeights.put(Item.PRICE, new Double(1));
		
		
		
		
		//create negotiator with params
		this.negotiator= new RetailerAgentNegotiator( this.maxNegotiationTime, strats, scoreWeights);
	}
	

	
	private class RetailerCNRBehaviour extends SSIteratedContractNetResponder{
		private EnergyUnit currentUnitRequest = null;		

		
		
		RetailerCNRBehaviour(Agent a, ACLMessage initialMessage) {
			super(a, initialMessage);
			
			setupNegotiator();
			//get demand from initial Message
			Offer off = new Offer(initialMessage);
			Demand demand=off.getDemand();
			System.out.println("demand "+demand.getContent());
			//setup initial issue 
			negotiator.setInitialIssue(demand);
			say("Creating new SSICNR behaviour");
		}
		
		@Override
		protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
			
			
			//get offer received
			Offer offer= new Offer(cfp);
			//interprete offer
			OfferStatus stat=negotiator.interpretOffer(offer);
			if(stat.equals(OfferStatus.REJECT))
			{
				//reject proposal by throwing a refuse
				throw new RefuseException("Times up ");
			}
			ACLMessage reply = cfp.createReply();
			reply.setPerformative(ACLMessage.PROPOSE);
			if(stat.equals(OfferStatus.ACCEPT))
			{
				//responder cant accept cfps so sending same offer received from cfp as proposal	
				reply.setContent(cfp.getContent());
			}			
			if(stat.equals(OfferStatus.COUNTER))
			{
				
				//send reply with counter offer						
				reply.setContent(negotiator.getLastOffer().getContent());
				say("counter offer "+reply.getContent());
			}				
			return reply;
			
		}
		
		@Override
		protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
			say("yay my offer accepted");
			ACLMessage resp=accept.createReply();
			say("Accepted"+propose.getContent());
			resp.setContent("Accepted"+propose.getContent());
			resp.setPerformative(ACLMessage.INFORM);
			return resp;
			
			
		}

		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
			say("Proposal rejected "+reject.getContent());
		}
	}
	
	private boolean performAction() {
		// For later, actually reduce energy stores for this agent and base price off that
		return true;
	}
	
	
	// Temporary method until the method of setting rate is determined

		
	private void setupServiceProviderComponent () {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription(); 
		
		// Maybe also add another service description of electricity buyer for when we implement agents buying power
		
		// Create service description
		sd.setType("RetailerAgent"); 
		sd.setName(getName());
		sd.setOwnership("TradeNetwork");
		
		// Add service to agent description
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		// Try to register the agent with its description
		try {
			DFService.register(this,dfd);	
		} catch (FIPAException e) {
			myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
			doDelete();
		}
	}
	
	
	private int convStrToInt (String str) {
		int value = 0;
		
		try {
			value = Integer.parseInt(str);	
		} catch (NumberFormatException e) {
			myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot convert \"" + str  + "\" into an integer", e);
			doDelete();
		}
		
		return value;
	}

	public int getEnergyStored() {
		return energyStored;
	}

	public void setEnergyStored(int energyStored) {
		this.energyStored = energyStored;
	}
}
