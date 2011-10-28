package closed.FBA2;

import java.util.LinkedList;

import util.StringAndDouble;
import util.graph.Node;

import common.OpenAgent;

/***************************************
 * 
 * Communicator Agent 2
 * 
 * This class is a variation of the FBA.CommunicatorAgent
 * 
 * It provides the same set of functions to handle the internal representation of the list of nodes
 * This representation is however different :
 * 
 *    - FBA.CommunicatorAgent uses observation to estimate the current idleness of a list 
 * (it keeps the 10 last observed idleness of each node, and the agent assumes that its idleness it the average of these values.
 * Each node is only updated when the agent visits it)
 * 
 *    - FBA2.FBA.CommunicatorAgent2 estimates the current idleness of each node by estimating it from the last time it visited it
 * ( each node is associated with a single value, and ALL nodes are updated after each visit of the agent on a node : the visited node 
 * is set to 0, the others are added the length of the last edge + 1 (the time to visit) )
 * In this way, the agent assumes that he is the only one to visit the node
 * Also, it does not need to know the real idleness of the node (thus the simulator does not need to give it in the perceptions)
 * 
 * 
 * This class is also based on the OpenAgent class
 * 
 * @author Cyril Poulet
 ***************************************/
public abstract class CommunicatorAgent2 extends OpenAgent {

	
	protected LinkedList<StringAndDouble> myNodes;
	protected long exchanges;
	
	
	
	public CommunicatorAgent2(String id) {
		super(id);
		
		myNodes = new LinkedList<StringAndDouble>();
		exchanges = 0;
	}
	
	public CommunicatorAgent2(String id, double entering_time, double quitting_time, String Society) {
		super(id, entering_time, quitting_time, Society);
		
		myNodes = new LinkedList<StringAndDouble>();
		exchanges = 0;
	}
	
	
	public CommunicatorAgent2(String id, LinkedList<String> nodes) {
		super(id);

		exchanges = 0;
		myNodes = new LinkedList<StringAndDouble>();
		for(String node : nodes)
			this.addToMyNodes(node);
	}
	
	public CommunicatorAgent2(String id, double entering_time, double quitting_time, String Society, LinkedList<String> nodes) {
		super(id, entering_time, quitting_time, Society);

		exchanges = 0;
		myNodes = new LinkedList<StringAndDouble>();
		for(String node : nodes)
			this.addToMyNodes(node);
	}

	
	/**
	 * Returns the estimated idleness of the node @param "node"
	 */
	protected double estimatedIdleness(String node){
		for(StringAndDouble no : myNodes){
			if(no.STRING.equals(node))
				return no.double_value;
		}
		
		return -1;
	}
	
	/**
	 * Returns the estimated idleness of the node in the position @param i in the list
	 */
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
	 * one which minimizes the distance to all others, the the 2nd is the closest to the 1rst, the 3rd is the closest 
	 * to the 2nd of the remaining nodes, etc.
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
	 * Orders the nodes of the agent by physical proximity, starting by the
	 * one which minimizes the distance to all others, the the 2nd is the closest to the 1rst, the 3rd is the closest 
	 * to the 2nd of the remaining nodes, etc.
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
	 * (puts all values to 0)
	 * 
	 * @param nodes :the list of nodes not to reset as a LinkedList<String> of Ids
	 */
	protected void restartIdleness(LinkedList<String> nodes){
		for(int i = 0; i < myNodes.size(); i++){
			if(!nodes.contains(myNodes.get(i).STRING))
				myNodes.get(i).double_value = 0;
		}
	}
	
	/**
	 * Sets the estimated idleness of @param node to @param idleness
	 * 
	 */
	protected void setIdleness(String node, int idleness){
		for(StringAndDouble no : myNodes)
			if(no.STRING.equals(node)){
				no.double_value = idleness;
				break;
			}	
	}
	
	/**
	 * Adds @param idleness to the estimated idleness of all nodes of the agent
	 * 
	 */
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
	
	/**
	 * Returns the Id of the node in position @param i in the list
	 * 
	 */
	protected String getMyNodes(int i){
		return myNodes.get(i).STRING;
	}
	
	/**
	 * Returns the position in the list of the node whose Id is @param node
	 * 
	 */
	protected int indexInMyNodes(String node){
		for(int i = 0; i < myNodes.size(); i++)
			if(myNodes.get(i).STRING.equals(node))
				return i;
		
		return -1;
	}
	
	/**
	 * Adds node of id @param node in the watched list, with an estimated idleness of 0
	 * 
	 */
	protected void addToMyNodes(String node){
		if(node == null)
			return;
		myNodes.add(new StringAndDouble(node, 0));
	}
	
	/**
	 * Adds node of id @param node in the watched list, with an estimated idleness of @param idleness
	 * 
	 */
	protected void addToMyNodes(String node, double idleness){
		if(node == null)
			return;
		myNodes.add(new StringAndDouble(node, idleness));
	}
	
	/**
	 * Removes node of id @param node from the watched list
	 * 
	 */
	protected void removeFromMyNodes(String node){
		if(node == null)
			return;
		
		for(int i = 0; i < myNodes.size(); i++)
			if(myNodes.get(i).STRING.equals(node)){
				myNodes.remove(i);
				return;
			}
				
		
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
	
	
	

}
