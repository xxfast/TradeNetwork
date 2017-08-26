package behavior;

import jade.core.behaviours.CyclicBehaviour;

public abstract class InteligentBehavior extends CyclicBehaviour {

	/*
	 * Do behavior initialization here;
	 */
	public abstract void init();
	
	/*
	 * determines whether the intelligent behavior should be reactive
	 *	 @returns true - by default 
	 */
	public boolean shouldReact(){
		return true;
	}
	
	/*
	 * behavioral response to the change in agent's environment 
	 */
	public abstract void react();

	/*
	 * actions needed to be taken after reactive phase, but before proactive phase
	 */
	public abstract void afterReact();
	
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
		if (shouldReact()){
			react();
			afterReact();
		}
		if (shouldProact()){
			proact();
			afterProact();
		}
		if (shouldSocial()){
			social();
			afterSocial();
		}
	}

}
