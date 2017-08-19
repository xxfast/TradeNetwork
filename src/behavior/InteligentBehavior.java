package behavior;

import jade.core.behaviours.CyclicBehaviour;

public abstract class InteligentBehavior extends CyclicBehaviour {

	/*
	 * Do behavior initialization here;
	 */
	public abstract void init();
	
	/*
	 * determines whether the intelligent behavior should be preactive
	 *	 @returns true - by default 
	 */
	public boolean shouldPreact(){
		return true;
	}
	
	/*
	 * behavioral response to the change in agent's environment 
	 */
	public abstract void preact();

	/*
	 * actions needed to be taken after preactive phase, but before proactive phase
	 */
	public abstract void afterPreact();
	
	/*
	 * determines whether the intelligent behavior should be proactive
	 *	 @returns true - by default 
	 */
	public boolean shouldProact(){
		return true;
	}

	/*
	 * behavioral response actively completed by the agent themselves,
	 */
	public abstract void proact();
	
	/*
	 * actions needed to be taken after proactive phase, but before social phase
	 */
	public abstract void afterProact();
	
	/*
	 * determines whether the intelligent behavior should be social
	 *	 @returns true - by default 
	 */
	public boolean shouldSocial(){
		return true;
	}

	/*
	 * behavioral responses for communicating information with other agents,
	 */
	public abstract void social();
	
	/*
	 * actions needed to be taken after social phase */
	public abstract void afterSocial();
	
	@Override
	public void action() {
		if (shouldPreact()){
			preact();
			afterPreact();
		}
		if (shouldPreact()){
			proact();
			afterProact();
		}
		if (shouldPreact()){
			social();
			afterSocial();
		}
	}

}
