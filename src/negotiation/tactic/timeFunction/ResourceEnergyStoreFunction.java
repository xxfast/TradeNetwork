package negotiation.tactic.timeFunction;

public class ResourceEnergyStoreFunction extends ResourceFunction{

	public final double K=4.00;
	private Double currentEnergy;
	private double max_energy;
	
	public ResourceEnergyStoreFunction(double k, Double energy) {
		super(k);
		this.currentEnergy=energy;
		max_energy=energy.doubleValue();
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double calculateResource(double time) {
		// TODO Auto-generated method stub
		//returning normalized value btw 0-4
		return currentEnergy*(K/max_energy);
	}

}
