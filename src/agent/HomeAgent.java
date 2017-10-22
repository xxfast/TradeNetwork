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
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.util.Logger;
import model.AgentDailyNegotiationThread;
import model.Demand;
import model.History;
import model.Offer;
import model.Schedule;
import negotiation.Issue;
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
	private final boolean INC = false;// customer mentality

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private Random rand;
	private Map<AID, HomeAgentNegotiator> negotiators;
	private AgentDailyNegotiationThread dailyThread;

	private ArrayList<Demand> messages = new ArrayList<>();
	private List<AID> retailers;

	private int agentHour;
	// params needed to setup negotiators
	// coming from args
	@Adjustable(label = "Max Iterations")
	private double maxNegotiationTime = 8;
	@Adjustable(label = "Parameter K")
	private double ParamK = 0.01;
	@Adjustable(label = "Parameter Beta")
	private double ParamBeta = 0.5;

	private double tacticTimeWeight = 0.6;
	private double tacticResourceWeight = 0.2;
	private double tacticBehaviourWeight = 0.2;
	private int behaviourRange = 2;

	private Schedule schedule = new Schedule();

	protected void setup() {
		super.setup();
		agentHour = 0;
		rand = new Random();
		dailyThread = new AgentDailyNegotiationThread();
		
		Object[] args = getArguments();
		// retrieve max time from args
		this.maxNegotiationTime = (double) (args[0]);
		// retrieve K and Beta from args
		this.ParamK = ((Double) args[1]);
		this.ParamBeta = ((Double) args[2]);
		retailers = new ArrayList<>();
		
		// get agents with retailer service
		DFAgentDescription[] agents = getServiceAgents("RetailerAgent");
		for (DFAgentDescription agent : agents) {
			retailers.add(agent.getName());
		}

		negotiators = new HashMap<>();

		/*
		 * Setting up 1) start listening to demands form home's appliances 2) start
		 * negotiating with the retailers
		 */

		addBehaviour(new DemandListeningBehaviour(this));
		addBehaviour(new NegotiatingBehaviour(this));
	}

	private class DemandListeningBehaviour extends AchieveREResponder {

		public DemandListeningBehaviour(Agent a) {
			super(a, MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
		}

		private Demand recievedDemand;

		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException {
			recievedDemand = new Demand(request);
			say("DEMAND received demand from " + request.getSender().getName() + ". Demand is "
					+ recievedDemand.getUnits() + " unit(s) from " + recievedDemand.getTime() + "h for "
					+ recievedDemand.getDuration() + " Hrs");
			say("OK ");
			ACLMessage agree = request.createReply();
			agree.setPerformative(ACLMessage.AGREE);
			return agree;
		}

		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
				throws FailureException {
			if (schedule(recievedDemand.getUnits(), recievedDemand.getTime(), recievedDemand.getDuration())) {
				say("YES Scehduled the demand succesfully");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				say("Action failed, informing initiator");
				throw new FailureException("unexpected-error");
			}
		}

	}

	private class NegotiatingBehaviour extends TickerBehaviour {
		public NegotiatingBehaviour(Agent a) {
			super(a, Simulation.Time);
		}

		@Override
		public void onTick() {
			addBehaviour(new RequestQuote(myAgent, null, retailers));
		}
	}

	protected Random getRandomizer() {
		return rand;
	}

	public void goNextHour() {
		++agentHour;
	}

	protected void setupHomeNegotiators(Integer activeAgents) {
		say("Setting up negotiators");
		// setup strategies

		// tactic setup
		// create TWfunction- for time dependent tactic
		TimeWeightedFunction poly = new TimeWeightedPolynomial(this.ParamK, this.ParamBeta, this.maxNegotiationTime);

		// create RAFunction- for resource dep tactic
		ResourceAgentsFunction rsrcFunc = new ResourceAgentsFunction(this.ParamK, activeAgents);

		// create behTFT- for behaviour dep tactic
		AverageTitForTat tft = new AverageTitForTat(Item.PRICE);

		// create tactics
		TimeDependentTactic tactic1 = new TimeDependentTactic(poly, this.INC);
		ResourceDependentTactic tactic2 = new ResourceDependentTactic(rsrcFunc, this.INC);
		BehaviourDependentTactic tactic3 = new BehaviourDependentTactic(tft, this.behaviourRange);

		// create strategy and add tactics with weights
		Strategy priceStrat = new Strategy(Strategy.Item.PRICE);

		Map<Tactic, Double> tactics = new HashMap<Tactic, Double>();
		tactics.put(tactic1, new Double(this.tacticTimeWeight));
		tactics.put(tactic2, new Double(this.tacticResourceWeight));
		tactics.put(tactic3, new Double(this.tacticBehaviourWeight));
		try {
			priceStrat.setTactics(tactics);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			say("ERROR " + e.getMessage());
			doDelete();

		}

		// add Strategy to negotiator's strategies
		ArrayList<Strategy> strats = new ArrayList<>();
		strats.add(priceStrat);

		// create score weights for negotiating items
		// ATM only price is considered so given full weight
		Map<Strategy.Item, Double> scoreWeights = new HashMap<>();
		// add only price item
		scoreWeights.put(Item.PRICE, new Double(1));

		// get my history object-simply creating new history, TODO object shud handle
		// loading agent history
		History history = new History(this.getLocalName());
		// create bound calc for price
		HomeBound homecacl = new HomeBound(history);

		// create negotiators with params for each retailer
		for (AID agent : retailers) {
			this.negotiators.put(agent,
					new HomeAgentNegotiator(this.maxNegotiationTime, strats, scoreWeights, homecacl));
		}
	}

	public class RequestQuote extends ContractNetInitiator {
		private List<AID> retailAgents;
		private int myPrice;
		private Demand schedDemand = null;
		private Integer activeAgents;
		private Map<ACLMessage, Double> acceptedScores;
		private Map<ACLMessage, Double> proposalScores;

		public RequestQuote(Agent a, ACLMessage msg, List<AID> retailers) {
			super(a, msg);
			retailAgents = retailers;
			activeAgents = new Integer(retailers.size());
			acceptedScores = new HashMap<>();
			proposalScores = new HashMap<>();

			// setup home negotiators
			setupHomeNegotiators(activeAgents);
			// add negotiations to dailyNegotiaition threads
			for (Map.Entry<AID, HomeAgentNegotiator> entry : negotiators.entrySet()) {
				dailyThread.addHourThread(agentHour, entry.getKey(), entry.getValue().getNegotiationThread());
			}
		}

		@Override
		protected Vector prepareCfps(ACLMessage msg) {
			// retrieve demand
			Demand demandMsg = calculateDemandForHour((short) 0);
			say("Gonna make call for proposal for " + demandMsg.getTime() + "h which was " + demandMsg.getUnits()
					+ " units");

			ACLMessage quoteRequest = null;
			Vector<ACLMessage> cfps = new Vector<>();
			if (demandMsg != null) {
				// construct quote request to send to retailers
				schedDemand = demandMsg;
				// TODO- ask negotiators to set intital issues
				for (Map.Entry<AID, HomeAgentNegotiator> entry : negotiators.entrySet()) {
					// create offer objects to set initial issue
					Offer off = new Offer();
					off.setOwner(entry.getKey().getLocalName());
					off.setDemand(schedDemand);
					entry.getValue().setInitialIssue(off);
					System.out.println("intial issue for " + entry.getKey().getLocalName() + " issue "
							+ entry.getValue().getItemIssue().get(Item.PRICE));
				}

				// create initial offer for each retailer from negotiators
				Offer offer;
				for (Map.Entry<AID, HomeAgentNegotiator> entry : negotiators.entrySet()) {
					offer = entry.getValue().generateOffer();
					// attach demand to offer
					offer.setDemand(schedDemand);
					// send msg
					quoteRequest = offer.createACLMessage(ACLMessage.CFP);
					quoteRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
					say("intial offer sent to agent" + entry.getKey().getLocalName() + " val" + offer.getContent());
					quoteRequest.addReceiver(entry.getKey());
					cfps.addElement(quoteRequest);
					// set negotiator to next iteration
					entry.getValue().nextIteration();
				}

			} else
				myLogger.log(Logger.SEVERE, "NULL Message sent for Quote");
			return cfps;
		}

		@Override
		protected void handlePropose(ACLMessage propose, Vector acceptances) {
			// TODO Auto-generated method stub
			// evaluate proposal for retailer

			// get retailer
			AID ret = propose.getSender();
			Offer off = new Offer(propose);
			OfferStatus status = negotiators.get(ret).interpretOffer(off);
			// store the scores for each proposal evaluated
			proposalScores.put(propose, negotiators.get(ret).evalScore(off));
			ACLMessage reply = propose.createReply();
			if (status.equals(OfferStatus.REJECT)) {
				// reject proposal
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				reply.setContent("Times up");
				say("Times up -Rejected " + propose.getSender().getLocalName() + " offer " + propose.getContent());
				// decrement activeagents
				--activeAgents;
				// remove negotiator object for retailer
			}

			if (status.equals(OfferStatus.ACCEPT)) {

				// handle accept proposal in handleResponse
				acceptedScores.put(propose, negotiators.get(ret).evalScore(off));
				return;
			}

			if (status.equals(OfferStatus.COUNTER)) {
				reply.setPerformative(ACLMessage.CFP);
				// set reply with counter offer
				reply.setContent(negotiators.get(ret).getLastOffer().getContent());
				say("counter offer to " + propose.getSender().getLocalName() + " val " + reply.getContent());
			}
			acceptances.addElement(reply);

		}

		@Override
		protected void handleRefuse(ACLMessage refuse) {
			// TODO add logic to refuse, decrement activeagents
			activeAgents--;
			say("Rejected from " + refuse.getSender().getLocalName());
			super.handleRefuse(refuse);
		}

		/*
		 * get all PROPOSE msgs and compare proposals send back accept and rejects
		 */
		@Override
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			// TODO Auto-generated method stub
			// handle accepted offers
			if (acceptedScores.size() > 0) {
				// select one with best score
				double bestscore = 0;
				ACLMessage bestproposal = null;
				for (Map.Entry<ACLMessage, Double> entry : acceptedScores.entrySet()) {
					if (entry.getValue() > bestscore) {
						bestscore = entry.getValue();
						bestproposal = entry.getKey();
					}
				}
				// create rejections for any counter offers since no point negotiating further
				// maybe can later capitalize on opponents conceding mentality by holding the
				// price for other negotiations
				for (Object obj : acceptances) {
					ACLMessage msg = (ACLMessage) obj;
					if (msg.getPerformative() != ACLMessage.REJECT_PROPOSAL) {
						// set it to reject
						msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
					}
				}
				// create rejections for accepted
				for (Map.Entry<ACLMessage, Double> entry : acceptedScores.entrySet()) {
					ACLMessage reply = entry.getKey().createReply();
					reply.setContent(entry.getKey().getContent());
					if (entry.getKey().getSender().equals(bestproposal.getSender())) {
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					} else
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

					acceptances.addElement(reply);
				}

			}
			// check if all rejected- we select best offer we cn get
			boolean allRej = true;
			for (Object obj : acceptances) {
				ACLMessage msg = (ACLMessage) obj;
				// check if all rejected
				if (msg.getPerformative() != ACLMessage.REJECT_PROPOSAL) {
					allRej = false;
					break;
				}

			}
			if (allRej) {
				// select and accept best possible offer
				handleAllRejections(acceptances);
			}
			// iterate through acceptances and determine if a new iteration of CFPs
			boolean isNextIter = false;
			for (Object obj : acceptances) {
				ACLMessage msg = (ACLMessage) obj;
				if (msg.getPerformative() == ACLMessage.CFP) {
					isNextIter = true;
					break;
				}

			}
			if (isNextIter)
				newIteration(acceptances);

			// clear proposalscores for next iteration
			proposalScores.clear();

		}

		@Override
		protected void handleAllResultNotifications(Vector resultNotifications) {
			for (Object notification : resultNotifications) {
				ACLMessage msg = (ACLMessage) notification;
				System.out.println("Inform recieved from " + msg.getSender().getLocalName());
				System.out.println("Msg is " + msg.getContent());
				// NegotiationThread t =
				// negotiators.get(msg.getSender()).getNegotiationThread();
				// say(t.toString());
			}

		}

		public void handleAllRejections(Vector acceptances) {
			// select one with best score
			double bestscore = 0;
			ACLMessage bestproposal = null;
			for (Map.Entry<ACLMessage, Double> entry : proposalScores.entrySet()) {
				if (entry.getValue() > bestscore) {
					bestscore = entry.getValue();
					bestproposal = entry.getKey();
				}

			}
			System.out.println("Accepting " + bestproposal.getSender().getLocalName() + " since best of all offers");
			// accept best proposal and reject rest
			// clear acceptances
			acceptances.clear();
			for (Map.Entry<ACLMessage, Double> entry : proposalScores.entrySet()) {
				ACLMessage reply = entry.getKey().createReply();
				reply.setContent(entry.getKey().getContent());
				if (entry.getKey().getSender().equals(bestproposal.getSender())) {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				} else
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

				acceptances.addElement(reply);
			}
		}

		@Override
		public int onEnd() {
			// TODO Auto-generated method stub
			// System.out.println(dailyThread.toString());
			goNextHour();
			return super.onEnd();
		}

		protected ACLMessage createReply(int perfomative, ACLMessage msg, String content) {
			ACLMessage reply = msg.createReply();
			reply.setPerformative(perfomative);
			reply.setContent(content);
			return reply;
		}

		protected ACLMessage createReply(int perfomative, ACLMessage msg) {
			ACLMessage reply = msg.createReply();
			reply.setPerformative(perfomative);
			return reply;
		}

	}

	public Schedule getSchedule() {
		return schedule;
	}

	public boolean schedule(int amount, Short time, int duration) {
		schedule.getTime().get(time).add(amount);
		return true;
	}

	public Demand calculateDemandForHour(Short time) {
		Demand inform = new Demand(time);
		List<Integer> demands = getSchedule().getTime().get(time);
		int total = 0;
		for (Integer demand : demands) {
			total += demand;
		}
		inform.setUnits(total);
		return inform;
	}
}
