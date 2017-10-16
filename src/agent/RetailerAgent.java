package agent;

// Used to make the agent a Service Provider Agent
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;

// Used to make the agent a ContractNetResponder Agent
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.proto.SSIteratedContractNetResponder;
import jade.proto.SSResponderDispatcher;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.core.behaviours.Behaviour;

// Used to implement the FSM Behaviour
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

// Used to log exceptions
import jade.util.Logger;
import model.Offer;
import negotiation.Issue;
import negotiation.Strategy;
import negotiation.Strategy.Item;
import negotiation.negotiator.RetailerAgentNegotiator;
import negotiation.negotiator.AgentNegotiator.OfferStatus;
import negotiation.tactic.Tactic;
import negotiation.tactic.TimeDependentTactic;
import negotiation.tactic.TimeWeightedFunction;
import negotiation.tactic.TimeWeightedPolynomial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//Used for the energy schedule
import java.util.Vector;

public class RetailerAgent extends TradeAgent {
	
	private double maxNegotiationTime=10;
	private final boolean INC=true;// supplier mentality
	private RetailerAgentNegotiator negotiator;

	private class EnergyUnit {
		private int time;
		private int units;
		private int cost;
		
	
	}	

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
		
		say(String.format("EnergyRate = $%d/unit, EnergyThreshold = %d units.", energyRate, energyThreshold));
		
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
	
	public void setupNegotiator()
	{
		double WFParamK=0.3;
		double WFParamBeta=0.5;  //Beta <1 competitive Beta >1 passive		
		
		//create TWfunction
		TimeWeightedFunction poly = new TimeWeightedPolynomial(WFParamK, WFParamBeta, this.maxNegotiationTime);
		
		//create tactics
		TimeDependentTactic tactic1= new TimeDependentTactic(poly, this.INC);
		
		//create strategy and add tactics with weights
		Strategy priceStrat= new Strategy(Strategy.Item.PRICE);
		double timeTacticWeight=1;//changes as new tactics added
		
		Map<Tactic,Double> tactics = new HashMap<Tactic,Double>();
		tactics.put(tactic1, new Double(timeTacticWeight));
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
		
		//create price range for each offer item-obtain from source
		Map<Strategy.Item,Issue> itemissue = new HashMap<>();
		//only add price issue since we are only focusing on price
		itemissue.put(Strategy.Item.PRICE, new Issue(40, 20));
		
		//create negotiator with params
		this.negotiator= new RetailerAgentNegotiator( this.maxNegotiationTime, itemissue, strats, scoreWeights);
	}
	

	
	private class RetailerCNRBehaviour extends SSIteratedContractNetResponder{
		private EnergyUnit currentUnitRequest = null;
		
		private Map<Item,Double> myvals;
		private Offer offer;
		int offset=2;
		
		
		RetailerCNRBehaviour(Agent a, ACLMessage initialMessage) {
			super(a, initialMessage);
			
			setupNegotiator();
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
			say("Proposal rejected");
		}
	}
	
	private boolean performAction() {
		// For later, actually reduce energy stores for this agent and base price off that
		return true;
	}
	
	
	// Temporary method until the method of setting rate is determined
	private void setAgentProperties() {
		Object[] args = this.getArguments();
		
	  	if (args != null && args.length > 0) {
			switch (args.length) {
			case 1:
				// One argument, assumed to be rate
				// Implies there will be no energy threshold
				energyRate = convStrToInt((String)args[0]);
				energyThreshold = -1;
				break;
			case 2:
				// Two arguments, assumed to be rate and threshold
				// If threshold is less than zero it is ignored in calculations
				energyRate = convStrToInt((String)args[0]);
				energyThreshold = convStrToInt((String)args[1]);
				break;
			default:
				// Too many (>2) arguments
				// Maybe make exception
				
				energyRate = (int) (Math.random() * 10);
				energyThreshold = -1;
				break;
			}
		} else {
			
			// Too few (<1) argument
			energyRate = (int) (Math.random() * 10);
			energyThreshold = -1;
		}
	}
		
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
}
