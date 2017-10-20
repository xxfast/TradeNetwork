package ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.tree.TreeModel;

import model.TradeAgentNode;
import simulation.Simulation;

public class TrageAgentSelector extends JComboBox<String>{
	
	public TrageAgentSelector(TreeModel agents) {
		super(CreateModel(agents));
	}
	
	private static DefaultComboBoxModel<String> CreateModel(TreeModel toModel) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		List<TradeAgentNode> agents = ExpandNode ((TradeAgentNode) toModel.getRoot());
		List<String> agentNames = new ArrayList<String>();
		for(TradeAgentNode node : agents) {
			agentNames.add(node.getAgent().getDescriptor().getName());
		}
		model = new DefaultComboBoxModel<String>(agentNames.toArray(new String[0]));
		return model;
	}
	
	private static List<TradeAgentNode> ExpandNode(TradeAgentNode toExpand){
		List<TradeAgentNode> toReturn = new ArrayList<TradeAgentNode>();
		for(int i=0;i<toExpand.getChildCount();i++) {
			TradeAgentNode child = (TradeAgentNode) toExpand.getChildAt(i);
			List<TradeAgentNode> grandChildren = null;
			if(child.getChildCount()>0) grandChildren = ExpandNode(child); 
			if(grandChildren!= null )toReturn.addAll(grandChildren);
		}
		return toReturn;
	}
	
	public boolean isEmpty() {
		return this.getModel().getSize() > 0;
	}

}
