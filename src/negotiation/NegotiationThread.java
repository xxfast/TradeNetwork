package negotiation;

import java.util.ArrayList;
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
