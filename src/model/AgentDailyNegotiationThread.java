package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jade.core.AID;
import model.AgentDailyNegotiationThread.Party;
import negotiation.NegotiationThread;
import negotiation.Strategy;

public class AgentDailyNegotiationThread {
	public enum Party{
		SELF,
		OTHER
	}
	
	private Map<Integer,Map<AID,NegotiationThread>> dailyThreads;
	
	public AgentDailyNegotiationThread()
	{
		dailyThreads= new HashMap<>();
		//set 24 time slots for 24 hours
		for(int i=0;i<24;i++)
		{
			dailyThreads.put(i, new HashMap<>());
		}
	}
	private boolean validateHour(int hour)
	{
		return hour>=0 && hour<=23;
	}
	public void addHourThread(int hour,AID agent,NegotiationThread thread)
	{
		if(validateHour(hour))
		{
			dailyThreads.get(hour).put(agent, thread);
		}
		
	}
	public Map<AID,Map<Party,List<Offer>>> getNegotiationsForHour(int hour)
	{
		if(validateHour(hour))
		{
			Map<AID,NegotiationThread> hourThreads = dailyThreads.get(hour);
			
			Map<AID,Map<Party,List<Offer>>> map = new HashMap<>();
			for(Map.Entry<AID, NegotiationThread> entry:hourThreads.entrySet())
			{
				Map<Party,List<Offer>> transactions = new HashMap<>();
				//get list of offers for party OTHER
				List<Offer> otherOffers =entry.getValue().getOpponentOffers();
				//get list of offers for party SELF
				List<Offer> selfOffers =entry.getValue().getSelfOffers();
				
				transactions.put(Party.SELF, selfOffers);
				transactions.put(Party.OTHER, otherOffers);
				
				map.put(entry.getKey(), transactions);
			}
			
			return map;
			
			
		}
		else return null;
	}
	
	public List<AID> getAgents(){
		List<AID> toReturn = new ArrayList<AID>();
		for(Map.Entry<Integer, Map<AID,NegotiationThread>> hourly: dailyThreads.entrySet()) {
			for(AID id : hourly.getValue().keySet()) {
				if(!toReturn.contains(id)) {
					toReturn.add(id);
				}
			}
		}
		return toReturn;
	}
	
	public double[] getMaxMinPrice(){
		double max = 0;
		double min = Double.MAX_VALUE;
		for(Map.Entry<Integer, Map<AID,NegotiationThread>> hour: dailyThreads.entrySet()) {
			Map<AID, Map<Party,List<Offer>>> negotiations = getNegotiationsForHour(hour.getKey());
			for(AID agent : negotiations.keySet()) {
				for(Party p : negotiations.get(agent).keySet()) {
					for(Offer o : negotiations.get(agent).get(p)) {
						if(max < o.getOfferValue(Strategy.Item.PRICE)) {
							max =  o.getOfferValue(Strategy.Item.PRICE);
						}
						if(min > o.getOfferValue(Strategy.Item.PRICE)) {
							min =  o.getOfferValue(Strategy.Item.PRICE);
						}
					}
				}
			}
		}
		return new double[]{min,max};
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		for(Map.Entry<Integer, Map<AID,NegotiationThread>> hourly:dailyThreads.entrySet())
		{
			
			int hour =hourly.getKey();
			
			Map<AID,Map<Party,List<Offer>>> hourThread=this.getNegotiationsForHour(hour);
			if(hourThread.size()>0)
			{
				build.append("hour "+ hour+"\n");
				for(Map.Entry<AID, Map<Party,List<Offer>>> entry:hourThread.entrySet())
				{
					build.append("Agent Negotiating with "+entry.getKey().getLocalName()+"\n");
					List<Offer> self=entry.getValue().get(Party.SELF);
					List<Offer> other=entry.getValue().get(Party.OTHER);
					for(Offer off:other)
					{
						build.append("Other "+off.getContent()+", ");
					}
					build.append("\n");
					for(Offer off:self)
					{
						build.append("Self "+off.getContent()+", ");
					}
					build.append("\n");
						
				}
			}
		
		}
		return build.toString();
		
		 
	}
}
