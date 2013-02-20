package open.CooperativeAuctions.AvEdges;

import java.util.LinkedList;
import java.util.Vector;

import open.CooperativeAuctions.PathLength.Minisum_Agent;
import util.graph.Node;

public class Minisum3_Agent extends Minisum_Agent {
	
	double utility_without_0 = -1, utility_without_1 = -1, utility_without_both = -1;
	
	Vector<String[]> quitting_node_group;
	
	
	public Minisum3_Agent(String id, int number_of_agents,
			LinkedList<String> nodes, double idleness_rate_for_path) {
		super(id, number_of_agents, nodes, idleness_rate_for_path);
	}
	
	public Minisum3_Agent(String id, double entering_time, double quitting_time, 
			int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, idleness_rate_for_path, society_id);
	}

	@Override
	protected double CalculateUtility(LinkedList<String> nodes) {
		if(nodes.size() == 0 || nodes.size() == 1)
			return 0;
		double sum = 0;
		int paths = 0;
		for(int i = 0; i < nodes.size(); i++){
			Node n1 = this.graph.getNode(nodes.get(i));
			for(int j = i+1; j < nodes.size(); j++){
				Node n2 = this.graph.getNode(nodes.get(j));
				double current_length = this.graph.getDistance(n1, n2);
				if(current_length != Double.MAX_VALUE){
					sum += current_length;
					paths++;
				}
			}
		}
		
		return sum/paths;
	}

}