package util;

import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import util.file.FileReader;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;


/**
 * This is a hybrid between Proximity path and Insertion Path
 * it creates a first path by ProximityPath, then refines it using the permutation functions of InsertionPath
 * 
 * It is a little slower than proximity but is more accurate on high numbers of nodes
 * @author pouletc
 *
 */
public class HS_HybridPath {
	HS_Graph graph;
	
	public HS_HybridPath(HS_Graph graph){
		this.graph = graph;
	}
	

	
	public LinkedList<String> GetShortestPath(LinkedList<String> nodes) {

		LinkedList<String> ordered1 = this.OrderNodes(nodes);
		if(ordered1.size() < 2)
			return ordered1;
		
		Node[] ordered1_list = new Node[ordered1.size()];
		for(int i = 0; i < ordered1.size(); i++)
			ordered1_list[i] = graph.getNode(ordered1.get(i));
		
		Node[] ordered2 = this.Two_opt_improvement(ordered1_list);
		ordered2 = this.One_target_Three_Opt(ordered2);
		
		LinkedList<String> answer = new LinkedList<String>();
		for(int i = 0; i < ordered2.length; i++)
			answer.add(ordered2[i].getObjectId());
		return answer;
	}

	
	
	public LinkedList<String> OrderNodes(LinkedList<String> nodes){
		if(nodes.size() < 2)
			return nodes;
		
		// first we associate the list of labels to the corresponding list of nodes
		Node[] orderedNodes = new Node[nodes.size()];
		for(int i = 0; i < nodes.size(); i++){
			orderedNodes[i] = graph.getNode(nodes.get(i));
		}
				
		// we put the node closest to all others first
		Double min = Double.MAX_VALUE;
		int pos = -1;
		for(int i = 0; i < orderedNodes.length; i++){
			double val = 0;
			for(int j = 0; j < orderedNodes.length; j++){
				if(i!=j)
					val += graph.getDistance(orderedNodes[i], orderedNodes[j]);
			}
			
			if(val < min){
				pos = i;
				min = val;
			}
		}
		
		if(pos != -1){
			Node temp = orderedNodes[0];
			orderedNodes[0] = orderedNodes[pos];
			orderedNodes[pos] = temp;
		}
		
		
		// then we order the list of nodes
		for(int i = 0; i < orderedNodes.length - 2; i++){
			// we find the closest node
			int next_pos = i+1;
			double next_dist = graph.getDistance(orderedNodes[i], orderedNodes[i+1]);
			for(int j = i+2; j < orderedNodes.length; j++){
				double dist = graph.getDistance(orderedNodes[i], orderedNodes[j]);
				if(dist < next_dist){
					next_pos = j;
					next_dist = dist;
				}
			}
			// we exchange it with the current next node
			Node temp = orderedNodes[i+1];
			orderedNodes[i+1] = orderedNodes[next_pos];
			orderedNodes[next_pos] = temp;
		}
		
		// we reassociate the node list to a label list
		LinkedList<String> results = new LinkedList<String>();
		for(int i = 0; i < orderedNodes.length; i++)
			results.add(orderedNodes[i].getObjectId());
		
		return results;
	}
	
	
	public Node[] Two_opt_improvement(Node[] mynodes){	
		Node[] current_best_path = mynodes;
		Node[] current_best_perm = this.Two_opt_one_pass(mynodes);
		while(!this.is_equivalent(current_best_path,current_best_perm)){
			current_best_path = current_best_perm;
			current_best_perm = this.Two_opt_one_pass(current_best_path);
		}
		
		return current_best_path;
	}
	
	public Node[] Two_opt_one_pass(Node[] mynodes){
		Node[] new_nodes = new Node[mynodes.length];
		for(int i = 0; i < mynodes.length; i++)
			new_nodes[i] = mynodes[i];
		int size = new_nodes.length;
		
		Node[] current_best_path = new_nodes;
		double current_best_cost = this.CalculatePathCost(new_nodes);
		
		for(int nb_element = 2; nb_element < size; nb_element++){
			for(int start_element = 0; start_element <= size - nb_element; start_element++){
				Node[] current_perm = this.perform_permutation(new_nodes, start_element, nb_element);
				double current_pathcost = this.CalculatePathCost(current_perm);
				
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

	public double CalculatePathCost(Node[] nodes){
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
	
	
	public Node[] One_target_Three_Opt(Node[] mynodes){
		Node[] current_best_path = mynodes;
		Double current_pathcost = this.CalculatePathCost(mynodes);
		Node[] current_best_insert = this.One_target_Three_Opt_one_pass(mynodes, current_pathcost);
		while(!this.is_equivalent(current_best_path, current_best_insert)){
			current_best_path = current_best_insert;
			current_pathcost = this.CalculatePathCost(current_best_path);
			current_best_insert = this.One_target_Three_Opt_one_pass(current_best_path, current_pathcost);
		}
		
		return current_best_path;
	}
	
	public Node[] One_target_Three_Opt_one_pass(Node[] mynodes, double pathcost){
		Node[] new_nodes = new Node[mynodes.length];
		for(int i = 0; i < mynodes.length; i++)
			new_nodes[i] = mynodes[i];
		
		for(int i = 0; i < new_nodes.length; i++){
			Node[] current_insert = this.change_place(new_nodes, i, (i == 0 ? 1 : 0));
			double current_pathcost = this.CalculatePathCost(current_insert);
			for(int j = 0; j < new_nodes.length; j++){
				if(i != j){
					Node[] test_insert = this.change_place(new_nodes, i, j);
					double test_pathcost = this.CalculatePathCost(test_insert);
					
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
	
	public boolean is_equivalent(Node[] n1, Node[] n2){
		if(n1 == null || n2 == null || n1.length != n2.length)
			return false;
		
		double pl1 = this.CalculatePathCost(n1);
		double pl2 = this.CalculatePathCost(n2);
		
		if(Math.abs(pl1 - pl2) < 0.001)
			return true;
		
		return false;
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException{
		FileReader file_reader = new FileReader("/home/pouletc/experimentation/Simulations/high_scale/coord/env_s3bis.txt");

		// holds the environment obtained from the file
		StringBuffer buffer = new StringBuffer();
		while (!file_reader.isEndOfFile()) {
			buffer.append(file_reader.readLine());
		}

		// closes the read file
		file_reader.close();
		Graph parsed_graph = null;
		try {
			parsed_graph = GraphTranslator.getGraphs(GraphTranslator.parseString(buffer.toString()))[0];
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Node[] nodes = parsed_graph.getNodes();
		HS_Graph hs_graph = new HS_Graph("hs_graph", nodes);
		hs_graph.getDiameter();
		
		LinkedList<String> allnodes = new LinkedList<String>();
		for(Node node : hs_graph.getNodes())
			allnodes.add(node.getObjectId());
		HS_HybridPath shortest_path = new HS_HybridPath(hs_graph);
		LinkedList<String> shortestpath = shortest_path.GetShortestPath(allnodes);
		
		double solution_length = 0;
		for (int j = 1; j < shortestpath.size(); j++){
			Edge[] edges = hs_graph.getDijkstraPath(hs_graph.getNode(shortestpath.get(j - 1)), hs_graph.getNode(shortestpath.get(j))).getEdges();
			double length = 0;
			for(Edge edge : edges)
				length += Math.ceil(edge.getLength());
			solution_length += length;
		}
		
		System.out.println("solution length " + solution_length);
		System.out.print("{");
		for(int i = 0; i < shortestpath.size(); i++){
			System.out.print(shortestpath.get(i) + ", ");
			if(i%20 == 0)
				System.out.print("\n");
		}
		System.out.print("}");
		
	}
}
