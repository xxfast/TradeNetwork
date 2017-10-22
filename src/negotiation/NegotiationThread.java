package negotiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Offer;
//object which mantains a list of all offers and counter offers with an agent
//the order is- receivedOff,CounterOff,receivedOff,CounterOff......
public class NegotiationThread {
	private List<Offer> history;

	/**
	 * 
	 */
	public NegotiationThread() {
		history = new ArrayList<>();
	}
	
	public void addOffer(Offer offer)
	{
		history.add(offer.clone());
	}
	
	public int size()
	{
		return history.size();
	}
	
	public Offer get(int index)
	{
		return history.get(index);
	}
	
	//return list of opponent offers
	public List<Offer> getOpponentOffers()
	{
		List<Offer> opponent= new ArrayList<>();
		//add all even entries
		for(int i=0;i<history.size();i++)
		{
			if(i%2==0)
				opponent.add(history.get(i));				
		}
		return opponent;
	}
	//return list of self offers
	public List<Offer> getSelfOffers()
	{
		List<Offer> self= new ArrayList<>();
		//add all even entries
		for(int i=0;i<history.size();i++)
		{
			if(i%2==1)
				self.add(history.get(i));				
		}
		return self;
	}

	@Override
	public String toString() {
		String str="Neg Thread\n";
		for(Offer off:history)
		{
			str+=off.toString()+"\n";
		}
		return str;
	}
	
	
	

}
