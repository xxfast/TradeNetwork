package agent;

import java.util.Iterator;

import descriptors.TradeAgentDescriptor;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import model.History;
import model.Offer;
import negotiation.Strategy.Item;
import negotiation.baserate.Transaction;
import negotiation.negotiator.AgentNegotiator;

public class TradeAgent extends Agent {
	
	private TradeAgentDescriptor descriptor;
	protected History myHistory;
	
	private boolean muted;
	
	protected void setup() {
		say("Initialising!");
		myHistory= new History(this.getLocalName());
	}
	
	public String getDescription(){
		return getLocalName();
	}
	
	public AID getAgentFromAMS(String localname)
	{
		AID agent=null;
		SearchConstraints c = new SearchConstraints();
		c.setMaxResults((long) -1);
		AMSAgentDescription[] agents=null;
		try {
			 agents= AMSService.search(this, new AMSAgentDescription(), c);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < agents.length; i++) {
			if(agents[i].getName().getLocalName().equals(localname))
			{
				agent=agents[i].getName();
			}
			
		}
		return agent;
	}

	public DFAgentDescription[] getServiceAgents( String service ) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType( service );
		dfd.addServices(sd);
		try
		{
			DFAgentDescription[] result = DFService.search(this, dfd);
			return result;
		}
		catch (Exception fe) {}
		return null;

	}
	public void addToHistory(AgentNegotiator neg,ACLMessage result,boolean success,AID client)
	{
		Transaction trans=neg.getNegotiationThread().getAsTransaction();
		if(!success)
		{
			trans.setRate(AgentNegotiator.REJECTRATE);
		}
		else
		{
//			System.out.println("reply "+result.toString());
			Offer off =new Offer(result);
			double finalPrice=off.getOfferValue(Item.PRICE);
			trans.setRate(finalPrice);
		}
		
		myHistory.addTransaction(client.getLocalName(), trans);
	}
	public AID getfirstReciever(ACLMessage msg)
	{
		Iterator it=msg.getAllReceiver();
		AID rec=(AID) it.next();
		return rec;
	}
	
	public void say(String message){
		if(!muted) System.out.println(this.getLocalName() +": "+ message);
	}

	public TradeAgentDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(TradeAgentDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

}
