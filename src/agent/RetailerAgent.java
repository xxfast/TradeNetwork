package agent;

// Used to make the agent a Service Provider Agent
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;

// Used to make the agent a ContractNetResponder Agent
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

// Used to log exceptions
import jade.util.Logger;

public class RetailerAgent extends TradeAgent {

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private int energyRate;
	private int energyThreshold;
	
	// [install later] Amount of energy the retailer agent has, determines rate and threshold
	private int energyStored = 0;
	
	protected void setup() {
		// Sets the agent's properties (energy rate & threshold) to passed or default values
		setAgentProperties();
		
		// Describes the agent as a retail agent
		setupServiceProviderComponent();
		
		System.out.println("Agent "+getLocalName()+": EnergyRate = $" + energyRate + "/unit, EnergyThreshold = " + energyThreshold + " units.");
		System.out.println("Agent "+getLocalName()+": waiting for CFP...");
		
		// Template to filter messages as to only receive CFP messages
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );
				
		// Adds the CNR behaviour to the agent
		addBehaviour(new ContractNetResponder(this, template) {
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Energy required is "+cfp.getContent()+" units.");
				int energyRequestAmount = convObjToInt(cfp.getContent());
				
				if (evaluateAction(energyRequestAmount)) {
					// Calculate energy cost based on amount needed and energyRate property
					int energyCost = calculateEnergyCost(cfp.getContent());
					
					System.out.println("Agent "+getLocalName()+": Proposing. Cost: $"+energyCost+ ".00");
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(String.valueOf(energyCost));
					return propose;
				}
				else {
					// We refuse to provide a proposal
					System.out.println("Agent "+getLocalName()+": Refuse. Cannot provide " + energyRequestAmount + " units. Threshold is " + energyThreshold + " units.");
					throw new RefuseException("evaluation-failed");
				}
			}
			
			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
				System.out.println("Agent "+getLocalName()+": Proposal accepted by " + accept.getSender().getName());
				if (performAction()) {
					System.out.println("Agent "+getLocalName()+ ": Energy delivered to " + accept.getSender().getName());
					ACLMessage inform = accept.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}
				else {
					System.out.println("Agent "+getLocalName()+": Action execution failed");
					throw new FailureException("unexpected-error");
				}	
			}

			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		} );
	}
	
	// Temporary method until the method of setting rate is determined
	private void setAgentProperties() {
		Object[] args = this.getArguments();

		switch (args.length) {
			case 1:
				// One argument, assumed to be rate
				// Implies there will be no energy threshold
				energyRate = convObjToInt(args[0]);
				energyThreshold = -1;
				break;
			case 2:
				// Two arguments, assumed to be rate and threshold
				// If threshold is less than zero it is ignored in calculations
				energyRate = convObjToInt(args[0]);
				energyThreshold = convObjToInt(args[1]);
				break;
			default:
				// Too few (<1) argument or too many (>2) arguments
				// Maybe throw an error
				energyRate = (int) (Math.random() * 10);
				energyThreshold = -1;
				break;
		}	
	}
	
	private int convObjToInt (Object arg) {
		int value = 0;
		
		try {
			value = Integer.parseInt((String) arg);	
		} catch (NumberFormatException e) {
			myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot convert \"" + (String)arg  + "\" into an integer", e);
			doDelete();
		}
		
		return value;
	}

	private boolean evaluateAction(int energyRequestAmount) {
		// If the amount required is too much deny the request
		if ( (energyRequestAmount > energyThreshold) && (energyThreshold >= 0) ) {
			return false;
		} else {
			return true;
		}
	}
	
	private int calculateEnergyCost(String cfpContent) {
		// assume that the passed content is integer only
		int energyRequestAmount = 0;
		energyRequestAmount = convObjToInt(cfpContent);
		return energyRate * energyRequestAmount;
	}

	private boolean performAction() {
		// For later, actually reduce energy stores for this agent and base price off that
		return true;
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
	
	protected void takeDown(){
       try {
    	   DFService.deregister(this); 
       } catch (Exception e) {
    	   myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot de-register with DF", e);
       }
	}
	
	
	
}
