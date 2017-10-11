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

//Used for the energy schedule
import java.util.Vector;


public class RetailerAgent extends TradeAgent {

	private class EnergyUnit {
		private int time;
		private int units;
		private int cost;
		
		public EnergyUnit(int units, int time) {
			this.time = time;
			this.units = units;
			this.cost = 0;
		}
		
		public int getTime() {
			return time;
		}
		
		public int getUnits() {
			return units;
		}
		
		public int getCost() {
			return cost;
		}
		
		public void determineCost(int rate) {
			cost = rate*units;
		}
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
		// Set up the agent
		
		// Sets the agent's properties (energy rate & threshold) to passed or default values
		setAgentProperties();
		
		//Describes the agent as a retail agent
		setupServiceProviderComponent();
		
		say("EnergyRate = $" + energyRate + "/unit, EnergyThreshold = " + energyThreshold + " units.");
		
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
	
	private class RetailerCNRBehaviour extends SSIteratedContractNetResponder{
		private EnergyUnit currentUnitRequest = null;
		
		RetailerCNRBehaviour(Agent a, ACLMessage initialMessage) {
			super(a, initialMessage);
			say("Creating new SSICNR behaviour");
		}
		
		@Override
		protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
			
			String[] strContent = cfp.getContent().split(":");
			currentUnitRequest = new EnergyUnit(convStrToInt(strContent[0]), convStrToInt(strContent[1]));
			
			say(String.format("CFP received from %s. Units required: %d units", cfp.getSender().getLocalName(), currentUnitRequest.getUnits()));
			
			
			if (evaluateAction(currentUnitRequest.getUnits())) {
				currentUnitRequest.determineCost(energyRate);
				say(String.format("Proposing. Cost: $%d.00. Delivering at time %d.", currentUnitRequest.getCost(), currentUnitRequest.getTime()));
				
				ACLMessage propose = cfp.createReply();
				propose.setPerformative(ACLMessage.PROPOSE);
				propose.setContent(String.valueOf(currentUnitRequest.getCost()));
				return propose;
			}
			else {
				// We refuse to provide a proposal
				say(String.format("Refuse. Cannot provide %d units. Threshold is %d units.", currentUnitRequest.getUnits(), currentUnitRequest.getUnits()));
				throw new RefuseException("evaluation-failed");	
			}
		}
		
		@Override
		protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
			say(String.format("Proposal accepted by %s", accept.getSender().getLocalName()));
			
			if (performAction()) {
				say(String.format("%d units delivered to %s", currentUnitRequest.getUnits(), accept.getSender().getLocalName()));
				ACLMessage inform = accept.createReply();
				inform.setContent(currentUnitRequest.getCost() + ":" + currentUnitRequest.getTime());
				inform.setPerformative(ACLMessage.INFORM);
				
				return inform;
			}
			else {
				say("Action execution failed");
				throw new FailureException("unexpected-error");
			}
		}

		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
			say("Proposal rejected");
		}
	}
	
	private boolean performAction() {
		// For later, actually reduce energy stores for this agent and base price off that
		return true;
	}
	
	private boolean evaluateAction(int energyRequestAmount) {
		// If the amount required is too much deny the request
		if ( (energyRequestAmount > energyThreshold) && (energyThreshold >= 0) ) {
			return false;
		} else {
			return true;
		}
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

	public int getEnergyStored() {
		return energyStored;
	}

	public void setEnergyStored(int energyStored) {
		this.energyStored = energyStored;
	}
}
