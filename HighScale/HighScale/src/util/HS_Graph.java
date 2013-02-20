/* Graph.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.util.Iterator;
import java.util.LinkedList;

import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;

/** Implements graphs that represent the territories to be patrolled. */
public final class HS_Graph extends Graph{
	/* Attributes. */

	/**
	 * Table that holds, for each node, its list of distances to the other
	 * nodes of the graph.
	 */
	private double[][] distances_table;
	private int[][] next_table;
	private int[][] hop_table;
	private LinkedList<String> distances_id;

	private static int max_hops;


	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the graph.
	 * @param nodes
	 *            The nodes of the graph.
	 */
	public HS_Graph(String label, Node[] nodes) {
		super(label, nodes);
	}

	/**
	 * Fills the table that holds, for each node, its list of distances to the
	 * other nodes of the graph.
	 */
	protected synchronized void calculateDistances() {
		double[][] table = new double[this.nodes.size()][this.nodes.size()];
		int[][] hops = new int[this.nodes.size()][this.nodes.size()];
		int[][] next = new int[this.nodes.size()][this.nodes.size()];
		distances_id = new LinkedList<String>();
		
		Object[] nodes_array = this.nodes.toArray();
		
		for(int i = 0; i < nodes_array.length; i++)
			distances_id.add(((Node) nodes_array[i]).getObjectId());
		
		// initialisation
		/* for(int i = 0; i < nodes_array.length; i++){
			Node current_node = (Node) nodes_array[i];
			Edge[] edges = current_node.getEdges();
			for(int j = 0; j < edges.length; j++){
				int ind_j = distances_id.indexOf(edges[j].getOtherNode(current_node).getObjectId());
				table[i][ind_j] = edges[j].getLength();
				hops[i][ind_j] = 1;
			}
			for(int j = 0; j < nodes_array.length; j++){
				if(i != j && table[i][j] == 0){
					table[i][j] = Double.MAX_VALUE;
					hops[i][j] = Integer.MAX_VALUE;
				}
				next[i][j] = -1;
			}
		} */
		for(int i = 0; i < nodes_array.length; i++){
			for(int j = 0; j < nodes_array.length; j++){
				if(i != j){
					table[i][j] = Double.MAX_VALUE;
					hops[i][j] = Integer.MAX_VALUE;
				}
				else {
					table[i][i] = 0;
					hops[i][j] = 0;
				}				
				next[i][j] = -1;
			}
		}
		
		Object[] edges = this.edges.toArray();
		for(int i = 0; i < edges.length; i++){
			int ind_i = distances_id.indexOf(((Edge)edges[i]).getNodees()[0].getObjectId());
			int ind_j = distances_id.indexOf(((Edge)edges[i]).getNodees()[1].getObjectId());
			
			table[ind_i][ind_j] = ((Edge)edges[i]).getLength();
			table[ind_j][ind_i] = ((Edge)edges[i]).getLength();
			hops[ind_i][ind_j] = 1;
			hops[ind_j][ind_i] = 1;
			
		}
		
		
		// algo Floyd-Warshall
		for(int k = 0; k < this.nodes.size(); k++)
			for(int i = 0; i < this.nodes.size(); i++)
				for(int j = 0; j < this.nodes.size(); j++)
					if(table[i][k]+table[k][j] < table[i][j]) {
						table[i][j] = table[i][k]+table[k][j];
						hops[i][j] = hops[i][k]+hops[k][j];
						next[i][j] = k;
					}
				
		
		
		distances_table = table;
		hop_table = hops;
		next_table = next;
		
		double min = Double.MAX_VALUE;
		double max = 0;
		int maxh = 0;
		
		for(int i = 0; i < this.nodes.size(); i++)
			for(int j = 0; j < this.nodes.size(); j++){
				if(distances_table[i][j] < min && distances_table[i][j] > 0)
					min = distances_table[i][j];
				if(distances_table[i][j] > max)
					max = distances_table[i][j];
				if(hop_table[i][j] > maxh)
					maxh = hop_table[i][j];
			}
		
		biggest_distance = max;
		smallest_distance = min;
		max_hops = maxh;
		
	}

	/**
	 * Returns, for two given nodes, the distance between them.
	 * 
	 * @return The distance between the two given nodes. Returns the maximum
	 *         possible value if the nodes are not in the same partition.
	 */
	public double getDistance(Node node_1, Node node_2) {
		if(node_1 == node_2)
			return 0;
		if (distances_table == null)
			this.calculateDistances();
			
		int ind_1 = distances_id.indexOf(node_1.getObjectId());
		int ind_2 = distances_id.indexOf(node_2.getObjectId());
		
		return distances_table[ind_1][ind_2];
	}
	
	public double getHopDistance(Node node_1, Node node_2) {
		if(node_1 == node_2)
			return 0;
		if (distances_table == null)
			this.calculateDistances();
			
		int ind_1 = distances_id.indexOf(node_1.getObjectId());
		int ind_2 = distances_id.indexOf(node_2.getObjectId());
		
		return hop_table[ind_1][ind_2];
	}


	/**
	 * Returns the smallest and biggest distances among the nodes of the
	 * graph.
	 */
	public double[] getSmallestAndBiggestDistances() {
		if (distances_table == null)
			this.calculateDistances();

		double[] answer = { smallest_distance, biggest_distance };
		return answer;
	}
	
	public double getDiameter(){
		if (distances_table == null)
			this.calculateDistances();
		
		return biggest_distance;
	}
	
	public double getHopDiameter(){
		if (hop_table == null)
			this.calculateDistances();
		
		return max_hops;
	}

	public HS_Graph getFloydWarshallPath(Node begin_node, Node end_node){
		if (distances_table == null)
			this.calculateDistances();
		
		if(begin_node.getObjectId().equals(end_node.getObjectId()))
			return new HS_Graph("FWpath", new Node[0]);
		
		int ind_begin = distances_id.indexOf(begin_node.getObjectId());
		int ind_end = distances_id.indexOf(end_node.getObjectId());
		
		if(distances_table[ind_begin][ind_end] == Double.MAX_VALUE)
			return new HS_Graph("FWpath", new Node[0]);
		
		String[] path = FloydWarshallPath(begin_node.getObjectId(), end_node.getObjectId());
		Node[] path_nodes = new Node[path.length + 2];
		for(int i = 0; i < path.length; i++)
			path_nodes[i] = this.getNode(path[i]);
		
		path_nodes[path.length] = begin_node;
		path_nodes[path.length + 1] = end_node;
		
		return new HS_Graph("FWpath", path_nodes);
		
		
	}
	
	private String[] FloydWarshallPath(String begin_node, String end_node){
		int ind_begin = distances_id.indexOf(begin_node);
		int ind_end = distances_id.indexOf(end_node);
		
		int next = next_table[ind_begin][ind_end];
		
		if(next == -1)
			return new String[0];
		else {
			String[] before = FloydWarshallPath(begin_node, distances_id.get(next));
			String[] after = FloydWarshallPath(distances_id.get(next), end_node);
			
			String[] answer = new String[before.length + 1 + after.length];
			for(int i = 0; i < before.length; i++)
				answer[i] = before[i];
			answer[before.length] = distances_id.get(next);
			for(int i = 0; i < after.length; i++)
				answer[before.length + 1 + i] = after[i];
			
			return answer;
		}
	}
	
	
	public HS_Graph getSubgraphByDistance(Node node, double distance){
		if (distances_table == null)
			this.calculateDistances();
		
		int ind = distances_id.indexOf(node.getObjectId());
		LinkedList<String> elected = new LinkedList<String>();
		
		for(int i = 0; i < distances_id.size(); i++)
			if(distances_table[ind][i] < distance)
				elected.add(distances_id.get(i));
		
		Node[] elected_nodes = new Node[elected.size()];
		for(int i = 0; i < elected.size(); i++)
			elected_nodes[i] = this.getNode(elected.get(i));
		
		return new HS_Graph("subgraph", elected_nodes);	
	}
	
	public HS_Graph getSubgraphByHops(Node node, int nbHop){
		if (hop_table == null)
			this.calculateDistances();
		
		LinkedList<String> elected = new LinkedList<String>();	
		
		int current_hops = 1;
		elected.add(node.getObjectId());
		int start = 0;
		int end = 1;
		
		while(current_hops <= nbHop){
			for(int i = start; i < end; i++){
				Node[] neighbour = this.getNode(elected.get(i)).getNeighbourhood();
				for(int j = 0; j < neighbour.length; j++)
					if(!elected.contains(neighbour[j].getObjectId()))
						elected.add(neighbour[j].getObjectId());
			}
			
			current_hops++;
			start = end;
			end = elected.size();
		}
		
		
		/*
		for(int i = 0; i < distances_id.size(); i++)
			if(hop_table[ind][i] <= nbHop)
				elected.add(distances_id.get(i));
			*/
		
		Node[] elected_nodes = new Node[elected.size()];
		for(int i = 0; i < elected.size(); i++){
			elected_nodes[i] = this.getNode(elected.get(i)).getCopy();	
		}		
		
		LinkedList<Edge> new_edges = new LinkedList<Edge>();
		
		for(Edge edge : this.edges){
			int ind0 = -1, ind1 = -1;
			for(int j = 0; j < elected_nodes.length; j++){
				if(elected_nodes[j].getObjectId().equals(edge.getNodees()[0].getObjectId()))
					ind0 = j;
				if(elected_nodes[j].getObjectId().equals(edge.getNodees()[1].getObjectId()))
					ind1 = j;
			}
			
			if(ind0 != -1 && ind1 != -1){
				Edge new_edge = null;
				if(this.getNode(elected_nodes[ind0].getObjectId()).isEmitterOf(edge) && 
						this.getNode(elected_nodes[ind0].getObjectId()).isCollectorOf(edge)){
					new_edge = new Edge(elected_nodes[ind0], elected_nodes[ind1], false, edge.getLength());
					new_edge.setObjectId(edge.getObjectId());
					new_edges.add(new_edge);
					elected_nodes[ind0].addEdge(new_edge);
				}
					
				else if(this.getNode(elected_nodes[ind0].getObjectId()).isEmitterOf(edge)){
					new_edge = new Edge(elected_nodes[ind0], elected_nodes[ind1], true, edge.getLength());
					new_edge.setObjectId(edge.getObjectId());
					new_edges.add(new_edge);
					elected_nodes[ind0].addEdge(new_edge);
				}
				
				else {
					new_edge = new Edge(elected_nodes[ind1], elected_nodes[ind0], true, edge.getLength());
					new_edge.setObjectId(edge.getObjectId());
					new_edges.add(new_edge);
					elected_nodes[ind1].addEdge(new_edge);
				}
			}
			
		}
		
		
		/*
		for(int i = new_graph.getEdges().length -1; i >= 0; i--){
			Node[] neighbours = new_graph.getEdges()[i].getNodees();
			if(new_graph.getNode(neighbours[0].getObjectId()) == null 
					|| new_graph.getNode(neighbours[1].getObjectId()) == null)
				new_graph.removeEdge(new_graph.getEdges()[i].getObjectId());
			
		}
		*/
		return new HS_Graph("subgraph", elected_nodes);
		
	}
	
	
	public void removeNode(String id){
		if(this.getNode(id) == null)
			return;
		
		this.nodes.remove(this.getNode(id));
		Iterator<Node> itr = this.nodes.iterator();
		while(itr.hasNext()){
			Node node = itr.next();
			Edge[] edges = node.getEdges();
			for(int i = edges.length - 1; i >= 0; i--)
				if(edges[i].getOtherNode(node).getObjectId().equals(id))
					node.removeEdge(edges[i]);
		}
		
	}
	
	public void removeEdge(String id){
		if(this.getEdge(id) == null)
			return;
	
		Iterator<Node> itr = this.nodes.iterator();
		while(itr.hasNext()){
			Node node = itr.next();
			node.removeEdge(this.getEdge(id));
		}
		
		this.edges.remove(this.getEdge(id));
	}
	
	public boolean hasGap(){
		LinkedList<String> connected = new LinkedList<String>();
		int i = 0;
		connected.add(this.getNodes()[0].getObjectId());
		while( i <= connected.size()){
			if(i == connected.size())
				break;
			Node[] neighbour = this.getNode(connected.get(i)).getNeighbourhood();
			if(neighbour != null)
				for(int j = 0; j < neighbour.length; j++)
					if(!connected.contains(neighbour[j].getObjectId()))
						connected.add(neighbour[j].getObjectId());
		
			i++;
		}
		
		if(connected.size() != this.nodes.size())
			return true;
		
		return false;
		
	}
	
	
}