package FBA;

import java.util.LinkedList;

import util.graph.Graph;
import util.graph.Node;

import common.Agent;

public class CommunicatorAgent2 extends Agent {

	Graph graph;
	
	LinkedList<StringAndDouble> myNodes;
	static final int IDLENESS_PAST_SIZE = 10;
	long exchanges;
	
	String agent_id;
	
	
	
	public CommunicatorAgent2(String id) {
		super();
		
		myNodes = new LinkedList<StringAndDouble>();
		exchanges = 0;
		
		agent_id = id;
	}
	
	
	public CommunicatorAgent2(String id, LinkedList<String> nodes) {
		super();

		exchanges = 0;
		agent_id = id;
		myNodes = new LinkedList<StringAndDouble>();
		for(String node : nodes)
			this.addToMyNodes(node);
	}

	
	/**
	 * Calculates the average idleness on the IDLENESS_PAST_SIZE past values
	 * 
	 * @param position : the position in the table of the node of interest
	 * @return the average value of the idleness
	 */
	protected double estimatedIdleness(String node){
		for(StringAndDouble no : myNodes){
			if(no.STRING.equals(node))
				return no.double_value;
		}
		
		return -1;
	}
	
	protected double estimatedIdleness(int i){
		if(myNodes.size() <= i)
			return -1;
		
		return myNodes.get(i).double_value;
	}

	/**
	 * Calculates highest average Idleness in the list of watched nodes
	 * 
	 * @return highest average idleness
	 */
	protected double calculateHighestIdleness(){
		double result = myNodes.get(0).double_value;
		for(int i = 1; i < myNodes.size(); i++){
			double aux = myNodes.get(i).double_value;
			if(result < aux)
				result = aux;
		}
		return result;
	}
	
	/**
	 * Calculates smallest average Idleness in the list of watched nodes
	 * 
	 * @return smallest average idleness
	 */
	protected double calculateMinorIdleness(){
		double result = myNodes.get(0).double_value;
		for(int i = 1; i < myNodes.size(); i++){
			double aux = myNodes.get(i).double_value;
			if(result >= aux)
				result = aux;
		}
		return result;
	}
	
	
	
	/**
	 * Orders the list of node Ids by physical proximity, starting by the
	 * first one (the 2nd is the closest to the 1rst, the 3rd is the closest 
	 * to the 2nd of the remaining nodes, etc)
	 * 
	 * @param nodes : a LinkedList<String> of node Ids
	 * @return the ordered list of node labels
	 */
	protected LinkedList<String> OrderNodes(LinkedList<String> nodes){
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
	

	/**
	 * Orders the list of node Ids by physical proximity, starting by the
	 * first one (the 2nd is the closest to the 1rst, the 3rd is the closest 
	 * to the 2nd of the remaining nodes, etc)
	 * 
	 * @param nodes : a LinkedList<String> of node Ids
	 * @return the ordered list of node labels
	 */
	protected void OrderMyNodes(){
		LinkedList<String> myNodes_str = this.getMyNodes();
		myNodes_str = this.OrderNodes(myNodes_str);
		
		LinkedList<StringAndDouble> myNewNodes = new LinkedList<StringAndDouble>();
		for(int i = 0; i < myNodes_str.size(); i++){
			int j = this.indexInMyNodes(myNodes_str.get(i));
			myNewNodes.add(myNodes.get(j));	
		}
	
		this.myNodes = myNewNodes;
	}

	/**
	 * calculates the cost (length) of the path made of the list of 
	 * successive node Ids
	 * 
	 * @param nodes : the path, as a LinkedList<String> of node Ids
	 * 
	 * @return the length of the path
	 */
	protected double CalculatePathCost(LinkedList<String> nodes){
		double cost = 0;
		Node node1 = graph.getNode(nodes.get(0)), node2=null;
		
		for(int i = 0; i < nodes.size() - 1; i ++){
			node2 = graph.getNode(nodes.get(i+1));
			cost += graph.getDistance(node1, node2);
			node1 = node2;
		}
		
		node2 = graph.getNode(nodes.get(0));
		cost += graph.getDistance(node1, node2);
		
		return cost;
	}
	
	protected double CalculateMyPathCost(){
		return this.CalculatePathCost(this.getMyNodes());
	}
	
	
	/**
	 * Calculates the smallest distance between 2 consecutive nodes in the list
	 * 
	 * @param nodes : the list of nodes as a LinkedList<String> of Ids
	 * 
	 * @return the smallest distance between 2 consecutive nodes
	 */
	protected double calculateMinorDistance(LinkedList<String> nodes){
		Node[] orderedNodes = new Node[nodes.size()];
		for(int i = 0; i < nodes.size(); i++){
			orderedNodes[i] = graph.getNode(nodes.get(i));
		}
		
		double dist_min = graph.getDistance(orderedNodes[0], orderedNodes[1]);
		for(int i = 1; i < orderedNodes.length - 1; i++){
			double dist = graph.getDistance(orderedNodes[i], orderedNodes[i+1]);
				if(dist < dist_min){
					dist_min = dist;
				}
		}
		
		double dist = graph.getDistance(orderedNodes[orderedNodes.length-1], orderedNodes[0]);
		if(dist < dist_min){
			dist_min = dist;
		}
		
		return dist_min;
	}
	
	/**
	 * Calculates the biggest distance between 2 consecutive nodes in the list
	 * 
	 * @param nodes : the list of nodes as a LinkedList<String> of Ids
	 * 
	 * @return the biggest distance between 2 consecutive nodes
	 */
	protected double calculateHighestDistance(LinkedList<String> nodes){
		Node[] orderedNodes = new Node[nodes.size()];
		for(int i = 0; i < nodes.size(); i++){
			orderedNodes[i] = graph.getNode(nodes.get(i));
		}
		
		double dist_max = graph.getDistance(orderedNodes[0], orderedNodes[1]);
		for(int i = 1; i < orderedNodes.length - 1; i++){
			double dist = graph.getDistance(orderedNodes[i], orderedNodes[i+1]);
				if(dist > dist_max){
					dist_max = dist;
				}
		}
		
		double dist = graph.getDistance(orderedNodes[orderedNodes.length-1], orderedNodes[0]);
		if(dist > dist_max){
			dist_max = dist;
		}
		
		return dist_max;
	}
	
	
	/**
	 * Resets the idleness values of all watched nodes EXCEPT those in the list
	 * (puts all table to 0)
	 * 
	 * @param nodes :the list of nodes to not reset as a LinkedList<String> of Ids
	 */
	protected void restartIdleness(LinkedList<String> nodes){
		for(int i = 0; i < myNodes.size(); i++){
			if(!nodes.contains(myNodes.get(i).STRING))
				myNodes.get(i).double_value = 0;
		}
	}
	
	/**
	 * stores the given idleness in first position of the associated table 
	 * (given by position), and shift the backward the stored idlenesses
	 * 
	 * @param position : the node to update, as its position in av_idleness
	 * @param idleness : the value to store
	 */
	protected void setIdleness(String node, int idleness){
		for(StringAndDouble no : myNodes)
			if(no.STRING.equals(node)){
				no.double_value = idleness;
				break;
			}	
	}
	
	protected void updateAllIdleness(double idleness){
		for(int i = 0; i < myNodes.size(); i++)
				myNodes.get(i).double_value += idleness;
	}
	
	/**
	 * Returns the list of watched nodes
	 * 
	 * @return LinkedList<String> of Ids of the watched nodes
	 */
	protected LinkedList<String> getMyNodes(){
		LinkedList<String> res = new LinkedList<String>();
		for(StringAndDouble node : myNodes)
			res.add(node.STRING);
		return res;
	}
	
	protected String getMyNodes(int i){
		return myNodes.get(i).STRING;
	}
	
	protected int indexInMyNodes(String node){
		for(int i = 0; i < myNodes.size(); i++)
			if(myNodes.get(i).STRING.equals(node))
				return i;
		
		return -1;
	}
	
	/**
	 * Getter for exchanges
	 * 
	 * @return exchanges
	 */
	protected long getExchanges(){
		return this.exchanges;
	}
	
	/**
	 * Setter for exchanges
	 */
	protected void reinitializeExchanges(){
		this.exchanges = 0;
	}
	
	protected void addToMyNodes(String node){
		if(node == null)
			return;
		myNodes.add(new StringAndDouble(node, 0));
	}
	
	protected void addToMyNodes(String node, double idleness){
		if(node == null)
			return;
		myNodes.add(new StringAndDouble(node, idleness));
	}
	
	protected void removeFromMyNodes(String node){
		if(node == null)
			return;
		
		for(int i = 0; i < myNodes.size(); i++)
			if(myNodes.get(i).STRING.equals(node)){
				myNodes.remove(i);
				return;
			}
				
		
	}

}
