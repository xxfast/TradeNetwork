package negotiation.tactic.timeFunction;

public class ResourceAgentsFunction extends ResourceFunction {
	private Integer agents;
	public ResourceAgentsFunction(double k,Integer activeNegAgents) {
		super(k);
		// TODO Auto-generated constructor stub
		agents=activeNegAgents;
	}
	@Override
	protected double calculateResource(double time) {
		// TODO Auto-generated method stub
		
		return agents.doubleValue();
	}
	public int getActiveAgents() {
		return agents;
	}
	public void setActiveAgents(Integer agents) {
		this.agents = agents;
	}
	
	
	

}
