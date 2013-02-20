package tests;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;

public class DistanceCalculator {

	/**
	 * @param args
	 * @throws EdgeNotFoundException 
	 * @throws NodeNotFoundException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, NodeNotFoundException, EdgeNotFoundException{

		FileReader file_reader = new FileReader("/home/pouletc/experimentation/test_sim/env_0.txt");

		// holds the environment obtained from the file
		StringBuffer buffer = new StringBuffer();
		while (!file_reader.isEndOfFile()) {
			buffer.append(file_reader.readLine());
		}

		// closes the read file
		file_reader.close();
		
		Graph graph = GraphTranslator.getGraphs(GraphTranslator.parseString(buffer.toString()))[0];
		
		LinkedList<String> test_list1 = new LinkedList<String>();
		/*test_list1.add("v1");
		test_list1.add("v7");
		test_list1.add("v24");
		test_list1.add("v10");
		test_list1.add("v26");
		test_list1.add("v35");
		test_list1.add("v36");
		test_list1.add("v47");
		test_list1.add("v48");
		test_list1.add("v31");
		test_list1.add("v22");
		test_list1.add("v12");
		test_list1.add("v18");*/
		for(Node node : graph.getNodes())
			test_list1.add(node.getObjectId());
		
		LinkedList<String> test_list2 = new LinkedList<String>();
		test_list2.add("v1");
		test_list2.add("v7");
		test_list2.add("v24");
		test_list2.add("v10");
		test_list2.add("v26");
		test_list2.add("v35");
		test_list2.add("v36");
		test_list2.add("v47");
		test_list2.add("v48");
		test_list2.add("v31");
		test_list2.add("v22");
		test_list2.add("v12");
		test_list2.add("v18");
		//for(Node node : graph.getNodes())
		//	test_list2.add(node.getObjectId());
		
		LinkedList<String> test_list3 = new LinkedList<String>();
		test_list3.add("v1");
		test_list3.add("v7");
		test_list3.add("v24");
		test_list3.add("v10");
		test_list3.add("v26");
		test_list3.add("v35");
		test_list3.add("v36");
		test_list3.add("v47");
		test_list3.add("v48");
		test_list3.add("v31");
		test_list3.add("v22");
		test_list3.add("v12");
		test_list3.add("v18");
		//for(Node node : graph.getNodes())
		//	test_list3.add(node.getObjectId());

		long startTime = System.currentTimeMillis();
		LinkedList<String> ordered1 = DistanceCalculator.OrderNodes(graph, test_list1);
		for(int i = 0; i < ordered1.size(); i++)
			System.out.print(ordered1.get(i) + " ");
		System.out.print("\n");
		System.out.println(DistanceCalculator.CalculatePathCost(graph, ordered1));
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
		
		System.out.println();
		
		/*startTime = System.currentTimeMillis();
		Node[] ordered2 = DistanceCalculator.OrderNodes2(graph, test_list1);
		for(int i = 0; i < ordered2.length; i++)
			System.out.print(ordered2[i].getObjectId() + " ");
		System.out.print("\n");
		System.out.println(DistanceCalculator.CalculatePathCost(graph, ordered2));
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println(totalTime);
		
		System.out.print("\n");*/
		
		startTime = System.currentTimeMillis();
		Node[] ordered3 = DistanceCalculator.OrderNodes3(graph, test_list1);
		for(int i = 0; i < ordered3.length; i++)
			System.out.print(ordered3[i].getObjectId() + " ");
		System.out.print("\n");
		System.out.println(DistanceCalculator.CalculatePathCost(graph, ordered3));
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println(totalTime);
		
		System.out.print("\n");
		
		startTime = System.currentTimeMillis();
		System.out.println(DistanceCalculator.average_path(graph, test_list1));
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println(totalTime);
		
	}

	
	
	public static double calculate_distance1(Graph graph, LinkedList<String> nodes){
		return DistanceCalculator.CalculatePathCost(graph, DistanceCalculator.OrderNodes(graph, nodes));
	}
	
	public static LinkedList<String> OrderNodes(Graph graph, LinkedList<String> nodes){
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
	
	public static double CalculatePathCost(Graph graph, LinkedList<String> nodes){
		double cost = 0;
		Node node1 = graph.getNode(nodes.get(0)), node2=null;
		
		for(int i = 0; i < nodes.size() - 1; i ++){
			node2 = graph.getNode(nodes.get(i+1));
			double dist = graph.getDistance(node1, node2);
			
			if(dist != Double.MAX_VALUE){
				cost += dist;
				node1 = node2;
			}
		}
		
		node2 = graph.getNode(nodes.get(0));
		cost += graph.getDistance(node1, node2);
		
		return cost;
	}


	
	
	
	
	public static double calculate_distance2(Graph graph, LinkedList<String> nodes){
		if(nodes.size() < 2)
			return 0;
		
		Node[] ordered_nodes = DistanceCalculator.OrderNodes2(graph, nodes);
		return DistanceCalculator.CalculatePathCost(graph, ordered_nodes);
	}
	
	public static Node[] OrderNodes2(Graph graph, LinkedList<String> nodesToInsert){
		LinkedList<String> nodes_copy = new LinkedList<String>();
		for(int i = 0; i < nodesToInsert.size(); i++)
			nodes_copy.add(nodesToInsert.get(i));
		
		if(graph == null)
			return null;
		if(nodes_copy.size() == 0)
			return new Node[0];
		if(nodes_copy.size() == 1){
			Node[] answer = {graph.getNode(nodes_copy.pop())};
			return answer;
		}
		
		if(nodes_copy.size() == 2){
			Node[] answer = {graph.getNode(nodes_copy.pop()), graph.getNode(nodes_copy.pop())};
			return answer;
		}
		

		Node[] mynodes = {graph.getNode(nodes_copy.pop()), graph.getNode(nodes_copy.pop())};
		
		while(nodes_copy.size() > 0){
			double current_best_cost = Double.MAX_VALUE;
			Node[] current_best_path = null;
			for(int i = 0; i < mynodes.length; i++){
				Node[] test_path = DistanceCalculator.insert(mynodes, graph.getNode(nodes_copy.get(0)), i);
				test_path = DistanceCalculator.Two_opt_improvement(graph, test_path);
				test_path = DistanceCalculator.One_target_Three_Opt(graph, test_path);
				double test_cost = DistanceCalculator.CalculatePathCost(graph, test_path);
				if(test_cost < current_best_cost){
					current_best_path = test_path;
					current_best_cost = test_cost;
				}
			}
			
			mynodes = current_best_path;
			nodes_copy.pop();
			
			for(int i = 0; i < mynodes.length; i++)
				System.out.print(mynodes[i].getObjectId() + " ");
			System.out.print("\n");
		}
		
		return mynodes;
		
	}
	
	public static Node[] Two_opt_improvement(Graph graph, Node[] mynodes){	
		Node[] current_best_path = mynodes;
		Node[] current_best_perm = DistanceCalculator.Two_opt_one_pass(graph, mynodes);
		while(!DistanceCalculator.is_equal(current_best_path,current_best_perm)){
			current_best_path = current_best_perm;
			current_best_perm = DistanceCalculator.Two_opt_one_pass(graph, current_best_path);
		}
		
		return current_best_path;
	}
	
	public static Node[] Two_opt_one_pass(Graph graph, Node[] mynodes){
		Node[] new_nodes = new Node[mynodes.length];
		for(int i = 0; i < mynodes.length; i++)
			new_nodes[i] = mynodes[i];
		int size = new_nodes.length;
		
		Node[] current_best_path = new_nodes;
		double current_best_cost = DistanceCalculator.CalculatePathCost(graph, new_nodes);
		
		for(int nb_element = 2; nb_element < size; nb_element++){
			for(int start_element = 0; start_element <= size - nb_element; start_element++){
				Node[] current_perm = DistanceCalculator.perform_permutation(new_nodes, start_element, nb_element);
				double current_pathcost = DistanceCalculator.CalculatePathCost(graph, current_perm);
				
				if(current_pathcost < current_best_cost){
					current_best_cost = current_pathcost;
					current_best_path = current_perm;
				}
			}
		}
		
		return current_best_path;
	}

	public static Node[] perform_permutation(Node[] nodes, int start_element, int nb_element){
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

	public static double CalculatePathCost(Graph graph, Node[] nodes){
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
	
	
	public static Node[] One_target_Three_Opt(Graph graph, Node[] mynodes){
		Node[] current_best_path = mynodes;
		Double current_pathcost = DistanceCalculator.CalculatePathCost(graph, mynodes);
		Node[] current_best_insert = DistanceCalculator.One_target_Three_Opt_one_pass(graph, mynodes, current_pathcost);
		while(!DistanceCalculator.is_equal(current_best_path, current_best_insert)){
			current_best_path = current_best_insert;
			current_pathcost = DistanceCalculator.CalculatePathCost(graph, current_best_path);
			current_best_insert = DistanceCalculator.One_target_Three_Opt_one_pass(graph, current_best_path, current_pathcost);
		}
		
		return current_best_path;
	}
	
	public static Node[] One_target_Three_Opt_one_pass(Graph graph, Node[] mynodes, double pathcost){
		Node[] new_nodes = new Node[mynodes.length];
		for(int i = 0; i < mynodes.length; i++)
			new_nodes[i] = mynodes[i];
		
		for(int i = 0; i < new_nodes.length; i++){
			Node[] current_insert = DistanceCalculator.change_place(new_nodes, i, (i == 0 ? 1 : 0));
			double current_pathcost = DistanceCalculator.CalculatePathCost(graph, current_insert);
			for(int j = 0; j < new_nodes.length; j++){
				if(i != j){
					Node[] test_insert = DistanceCalculator.change_place(new_nodes, i, j);
					double test_pathcost = DistanceCalculator.CalculatePathCost(graph, test_insert);
					
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
	
	public static Node[] change_place(Node[] mynodes, int ind_elt_to_move, int new_ind_of_elt){
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

	public static Node[] insert(Node[] mynodes, Node node, int index){
		Node[] new_nodes = new Node[mynodes.length + 1];
		for(int i = 0; i < index; i++)
			new_nodes[i] = mynodes[i];
		new_nodes[index] = node;
		for(int i = index; i < mynodes.length; i++)
			new_nodes[i+1] = mynodes[i];
		
		return new_nodes;    
	}



	public static Node[] OrderNodes3(Graph graph, LinkedList<String> nodesToInsert){
		LinkedList<String> ordered1 = DistanceCalculator.OrderNodes(graph, nodesToInsert);
		Node[] ordered1_list = new Node[ordered1.size()];
		for(int i = 0; i < ordered1.size(); i++)
			ordered1_list[i] = graph.getNode(ordered1.get(i));
		
		Node[] ordered2 = DistanceCalculator.Two_opt_improvement(graph, ordered1_list);
		ordered2 = DistanceCalculator.One_target_Three_Opt(graph, ordered2);
		
		return ordered2;
	}
	
	

	public static double average_path(Graph graph, Node[] nodes){
		if(nodes.length < 2)
			return 0;
		
		double pathlength = 0;
		double pathnum = 0;
		for(int i = 0; i < nodes.length -1; i++)
			for(int j = i+1; j < nodes.length; j++){
				pathlength += graph.getDistance(nodes[i], nodes[j]);
				pathnum++;
			}
		
		return pathlength / pathnum;
	}
	
	public static double average_path(Graph graph, LinkedList<String> nodes){
		
		// first we associate the list of labels to the corresponding list of nodes
		Node[] Nodes = new Node[nodes.size()];
		for(int i = 0; i < nodes.size(); i++){
			Nodes[i] = graph.getNode(nodes.get(i));
		}
		
		return average_path(graph, Nodes);
	}
	
	
	
	public static boolean is_equal(Node[] n1, Node[] n2){
		if(n1 == null || n2 == null || n1.length != n2.length)
			return false;
		
		for(int i = 0; i < n1.length; i++)
			if(!n1[i].getObjectId().equals(n2[i].getObjectId()))
				return false;
		
		return true;
	}

}
