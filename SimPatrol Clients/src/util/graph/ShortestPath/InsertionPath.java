package util.graph.ShortestPath;

import java.util.LinkedList;

import util.graph.Graph;
import util.graph.Node;

/**
 * This path calculator is very accurate but takes a lot of time when there is a high number of nodes, because it recalculates from scratch the path.
 * See Tovey, C.; Lagoudakis, M.; Jain, S. & Koenig, S. The generation of bidding rules for auction-based robot coordination (2005) for details
 * 
 * @author pouletc
 *
 */
public class InsertionPath implements ShortestPath {

	@Override
	public LinkedList<String> GetShortestPath(Graph graph, LinkedList<String> nodes) {
		LinkedList<String> nodes_copy = new LinkedList<String>();
		for(int i = 0; i < nodes.size(); i++)
			nodes_copy.add(nodes.get(i));
		
		if(graph == null)
			return null;
		if(nodes_copy.size() == 0)
			return new LinkedList<String>();
		if(nodes_copy.size() == 1){
			LinkedList<String> answer = new LinkedList<String>();
			answer.add(nodes_copy.pop());
			return answer;
		}
		
		if(nodes_copy.size() == 2){
			LinkedList<String> answer = new LinkedList<String>();
			answer.add(nodes_copy.pop());
			answer.add(nodes_copy.pop());
			return answer;
		}
		

		Node[] mynodes = {graph.getNode(nodes_copy.pop()), graph.getNode(nodes_copy.pop())};
		
		while(nodes_copy.size() > 0){
			double current_best_cost = Double.MAX_VALUE;
			Node[] current_best_path = null;
			for(int i = 0; i < mynodes.length; i++){
				Node[] test_path = this.insert(mynodes, graph.getNode(nodes_copy.get(0)), i);
				test_path = this.Two_opt_improvement(graph, test_path);
				test_path = this.One_target_Three_Opt(graph, test_path);
				double test_cost = this.CalculatePathCost(graph, test_path);
				if(test_cost < current_best_cost){
					current_best_path = test_path;
					current_best_cost = test_cost;
				}
			}
			
			mynodes = current_best_path;
			nodes_copy.pop();
			
			/*for(int i = 0; i < mynodes.length; i++)
				System.out.print(mynodes[i].getObjectId() + " ");
			System.out.print("\n");
			*/
		}
		
		
		LinkedList<String> answer = new LinkedList<String>();
		for(int i = 0; i < mynodes.length; i++)
			answer.add(mynodes[i].getObjectId());
		return answer;
		
	}
	
	public Node[] Two_opt_improvement(Graph graph, Node[] mynodes){	
		Node[] current_best_path = mynodes;
		Node[] current_best_perm = this.Two_opt_one_pass(graph, mynodes);
		while(!this.is_equal(current_best_path,current_best_perm)){
			current_best_path = current_best_perm;
			current_best_perm = this.Two_opt_one_pass(graph, current_best_path);
		}
		
		return current_best_path;
	}
	
	public Node[] Two_opt_one_pass(Graph graph, Node[] mynodes){
		Node[] new_nodes = new Node[mynodes.length];
		for(int i = 0; i < mynodes.length; i++)
			new_nodes[i] = mynodes[i];
		int size = new_nodes.length;
		
		Node[] current_best_path = new_nodes;
		double current_best_cost = this.CalculatePathCost(graph, new_nodes);
		
		for(int nb_element = 2; nb_element < size; nb_element++){
			for(int start_element = 0; start_element <= size - nb_element; start_element++){
				Node[] current_perm = this.perform_permutation(new_nodes, start_element, nb_element);
				double current_pathcost = this.CalculatePathCost(graph, current_perm);
				
				if(current_pathcost < current_best_cost){
					current_best_cost = current_pathcost;
					current_best_path = current_perm;
				}
			}
		}
		
		return current_best_path;
	}

	public Node[] perform_permutation(Node[] nodes, int start_element, int nb_element){
		Node[] new_nodes = new Node[nodes.length];
		for(int i = 0; i < nodes.length; i++)
			new_nodes[i] = nodes[i];
		
		
		for(int i = 0; i < (nb_element / 2); i++){
			Node temp = new_nodes[start_element + i];
			new_nodes[start_element + i] = new_nodes[start_element + nb_element - 1 -i];
			new_nodes[start_element + nb_element - 1 -i] = temp;
		}
		return new_nodes;
	}

	public double CalculatePathCost(Graph graph, Node[] nodes){
		double cost = 0;
		Node node1 = nodes[0], node2=null;
		
		for(int i = 0; i < nodes.length - 1; i ++){
			node2 = nodes[i+1];
			double dist = graph.getDistance(node1, node2);
			
			if(dist != Double.MAX_VALUE){
				cost += dist;
				node1 = node2;
			}
		}
		
		node2 = nodes[0];
		cost += graph.getDistance(node1, node2);
		
		return cost;
	}
	
	
	public Node[] One_target_Three_Opt(Graph graph, Node[] mynodes){
		Node[] current_best_path = mynodes;
		Double current_pathcost = this.CalculatePathCost(graph, mynodes);
		Node[] current_best_insert = this.One_target_Three_Opt_one_pass(graph, mynodes, current_pathcost);
		while(!this.is_equal(current_best_path, current_best_insert)){
			current_best_path = current_best_insert;
			current_pathcost = this.CalculatePathCost(graph, current_best_path);
			current_best_insert = this.One_target_Three_Opt_one_pass(graph, current_best_path, current_pathcost);
		}
		
		return current_best_path;
	}
	
	public Node[] One_target_Three_Opt_one_pass(Graph graph, Node[] mynodes, double pathcost){
		Node[] new_nodes = new Node[mynodes.length];
		for(int i = 0; i < mynodes.length; i++)
			new_nodes[i] = mynodes[i];
		
		for(int i = 0; i < new_nodes.length; i++){
			Node[] current_insert = this.change_place(new_nodes, i, (i == 0 ? 1 : 0));
			double current_pathcost = this.CalculatePathCost(graph, current_insert);
			for(int j = 0; j < new_nodes.length; j++){
				if(i != j){
					Node[] test_insert = this.change_place(new_nodes, i, j);
					double test_pathcost = this.CalculatePathCost(graph, test_insert);
					
					if(test_pathcost < current_pathcost){
						current_pathcost = test_pathcost;
						current_insert = test_insert;
					}
				}
			}
			if(current_pathcost < pathcost)
				return current_insert;
			
		}
		return new_nodes;
		
	}
	
	public Node[] change_place(Node[] mynodes, int ind_elt_to_move, int new_ind_of_elt){
		Node[] new_nodes = new Node[mynodes.length];
		
		if(ind_elt_to_move < new_ind_of_elt){
			for(int i = 0; i < ind_elt_to_move; i++)
				new_nodes[i] = mynodes[i];
			for(int i = ind_elt_to_move; i < new_ind_of_elt; i++)
				new_nodes[i] = mynodes[i+1];
			new_nodes[new_ind_of_elt] = mynodes[ind_elt_to_move];
			for(int i = new_ind_of_elt + 1; i < mynodes.length; i++)
				new_nodes[i] = mynodes[i];
		}
		
		if(ind_elt_to_move > new_ind_of_elt){
			for(int i = 0; i < new_ind_of_elt; i++)
				new_nodes[i] = mynodes[i];
			new_nodes[new_ind_of_elt] = mynodes[ind_elt_to_move];
			for(int i = new_ind_of_elt + 1; i <= ind_elt_to_move; i++)
				new_nodes[i] = mynodes[i-1];
			new_nodes[new_ind_of_elt] = mynodes[ind_elt_to_move];
			for(int i = ind_elt_to_move + 1; i < mynodes.length; i++)
				new_nodes[i] = mynodes[i];
		}
		
		return new_nodes;
		
	}

	public Node[] insert(Node[] mynodes, Node node, int index){
		Node[] new_nodes = new Node[mynodes.length + 1];
		for(int i = 0; i < index; i++)
			new_nodes[i] = mynodes[i];
		new_nodes[index] = node;
		for(int i = index; i < mynodes.length; i++)
			new_nodes[i+1] = mynodes[i];
		
		return new_nodes;    
	}
	
	
	public boolean is_equal(Node[] n1, Node[] n2){
		if(n1 == null || n2 == null || n1.length != n2.length)
			return false;
		
		for(int i = 0; i < n1.length; i++)
			if(!n1[i].getObjectId().equals(n2[i].getObjectId()))
				return false;
		
		return true;
	}

}
