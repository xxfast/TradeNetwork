package ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.tree.TreeModel;

import simulation.Simulation;

public class TrageAgentSelector<T> extends JComboBox{
	
	Class<?> selectedType;
	DefaultComboBoxModel<String> model;
	List<Simulation.TradeAgentNode> agents;
	
	public TrageAgentSelector(Class<?> toSelect, TreeModel agents) {
		this.selectedType = toSelect;
		CreateModel(agents);
	}
	
	private void CreateModel(TreeModel toModel) {
		this.model = new DefaultComboBoxModel();
		agents = ExpandNode ((Simulation.TradeAgentNode) toModel.getRoot());
		List<String> agentNames = new ArrayList<String>();
		for(Simulation.TradeAgentNode node : agents) {
			agentNames.add(node.getAgent().getDescriptor().getName());
		}
		model = new DefaultComboBoxModel(agentNames.toArray());
	}
	
	private List<Simulation.TradeAgentNode> ExpandNode(Simulation.TradeAgentNode toExpand){
		List<Simulation.TradeAgentNode> toReturn = new ArrayList<Simulation.TradeAgentNode>();
		for(int i=0;i<toExpand.getChildCount();i++) {
			Simulation.TradeAgentNode child = (Simulation.TradeAgentNode) toExpand.getChildAt(i);
			List<Simulation.TradeAgentNode> grandChildren = null;
			if(child.getChildCount()>0) grandChildren = ExpandNode(child); 
			toReturn.addAll(grandChildren);
		}
		return toReturn;
	}

}
