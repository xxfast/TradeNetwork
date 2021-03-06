package negotiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import negotiation.tactic.Tactic;

public class Strategy {
	//the value which this strategy produces
	public enum Item{
		PRICE,
		DURATION
	}
	
	//tactics and their weights
	private Map<Tactic,Double> tactics;	
	private ArrayList<Issue> issues;
	private double currentVal;
	private Item value;
	/**
	 * @param tactics
	 */
	public Strategy(Item value) {
		this.tactics = null;
		this.issues=new ArrayList<>();
		currentVal=0;	
		this.value=value;
	}
	
	private void computeNextOfferVal()
	{
		double finalval=0;
		double distWeight=0;
		double temp=0;
		Map<Tactic,Double> tacticsNextValues = new HashMap<>();
		Issue next=issues.get(issues.size()-1);
		//get each tactic to compute next value
		for(Map.Entry<Tactic, Double> entry: tactics.entrySet())
		{
			//calculate next value
			temp=entry.getKey().nextValue(next);
			//store next values in map 
			if(temp!=0)
				tacticsNextValues.put(entry.getKey(), temp);
			else
				//keep track of their weights
				distWeight+=entry.getValue();
		
		}

		//check if any of the new values are 0, if so they shouldnt be taken into the final value
		//the weight for that tactic should be evenly distributed to other tactics
		
		Map<Tactic,Double> weights;
		//if need redistribution of weights, create map with new weights 
		if(distWeight!=0)
		{
			
			Map<Tactic,Double> newWeights = new HashMap<>();
			for(Map.Entry<Tactic, Double> entry:tactics.entrySet())
			{
				newWeights.put(entry.getKey(), entry.getValue()+distWeight/(double)tacticsNextValues.size());
			}
			weights=newWeights;
		}
		else
		{
			//use default tactics with weights
			weights=tactics;
		}
		
		//compute final value
		for(Map.Entry<Tactic, Double> entry:tacticsNextValues.entrySet())
		{
//			System.out.println("tactic "+entry.getKey()+" next val"+entry.getValue());
			finalval+=entry.getValue()*weights.get(entry.getKey());
		}
		
		currentVal=finalval;
	}
	
	private void addIssue(Issue issue)
	{
		//add a cloned issue
		this.issues.add(new Issue(issue.getIteration(),issue.getMaxVal(),issue.getMinVal()));
	}
	
	public boolean generateOfferValue(Issue issue)
	{		
		if(issues.size()==0 && issue.getIteration()==0)
		{
			this.addIssue(issue);
			//compute offer val for new issue
			computeNextOfferVal();
			return true;
		}
		else
		{
			Issue current=issues.get(issues.size()-1);
			//check if new issue is preceding previous issue
			if(current.getIteration()+1==issue.getIteration())
			{
				this.addIssue(issue);
				//compute offer val for new issue
				computeNextOfferVal();
				return true;
			}
			else
				return false;
		}
				
	}
	
	public void setTactics(Map<Tactic,Double> tactics) throws Exception
	{
		double weightTotal=0;
		//validate tactics before setting
		for(Map.Entry<Tactic, Double> entry: tactics.entrySet())
		{
			weightTotal+=entry.getValue();
		}
		if(weightTotal!=1)
		{
			throw new Exception("Weights should add up to 1");
		}
		else
			this.tactics=tactics;
	}

	public Item getItem() {
		return value;
	}

	public double getCurrentVal() {
		return currentVal;
	}

	public ArrayList<Issue> getIssues() {
		return issues;
	}

	public Map<Tactic, Double> getTactics() {
		return tactics;
	}
	
	public Strategy clone()
	{
		Strategy clone = new Strategy(this.value);
		//clone issues
		clone.tactics=this.tactics;
		clone.issues= new ArrayList<>();
		for(Issue issue:this.issues)
		{
			clone.issues.add(issue);
		}
		clone.currentVal=this.currentVal;
		return clone;
	}



	
	
	
	
	
	
}
