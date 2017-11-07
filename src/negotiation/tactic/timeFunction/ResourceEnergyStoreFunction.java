package negotiation.tactic.timeFunction;

public class ResourceEnergyStoreFunction extends ResourceFunction{

	//basic function is- lower the energy remaining the higher the cost, thus want to sell at reservation val
	private Double currentEnergy;
	private Double fixedMaintanenceCost;
	public ResourceEnergyStoreFunction(double k, Double energy) {
		super(k);
		this.currentEnergy=energy;
		this.fixedMaintanenceCost=25.00;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double calculateResource(double time) {
		// TODO Auto-generated method stub
		return currentEnergy/fixedMaintanenceCost;
	}

}
