package util.graph;

import java.util.Vector;


/**
 * This class helps constructing a graph incrementally, by adding
 * nodes and edge randomly. After building the desired graph, it
 * allows conversion to "Graph" class with method "toGraph()".    
 */
public class GraphBuilder {
	private Vector<Node> allNodes;
	private boolean directedEdges;

	
	/**
	 * Creates an empty graph. 
	 */
	public GraphBuilder() {
		allNodes = new Vector<Node>();
	}

	/**
	 * Creates a graph with the given number of nodes. 
	 * Nodes are identified by numbers from 0 to (numNodes-1).  
	 */
	public GraphBuilder(int numNodes, boolean directed) {
		allNodes = new Vector<Node>();
		
		Node n;
		for (int i = 0; i < numNodes; i++) {
			n = new Node("node"+i);
			n.setObjectId(n.getLabel());
			allNodes.add(n);
		}
		
		directedEdges = directed;
	}
	
	/**
	 * Adds a new node and returns a positive integer identifying it.
	 * The identifier is always the same as number of nodes prior to
	 * the call to this method.  
	 */
	public int addNode() {
		int n = allNodes.size();
		allNodes.add(new Node("node"+n));
		return n;
	}

	/**
	 * Adds an edge from "source" node to "target" node, with a cost given 
	 * by "weight" parameter. If the graph is undirected, it is not necessary 
	 * to add another edge in the reverse direction.
	 */
	public void addEdge(int source, int target, double weight) {
		Node sourceNode = allNodes.get(source);
		Node targetNode = allNodes.get(target);
		
		// attention: the edge adds itself to the nodes
		Edge edge = new Edge(sourceNode, targetNode, directedEdges, weight);
		
		edge.setObjectId("edge" + source + "-" + target);
	}
	
	/**
	 * Returns the (current) number of nodes.
	 */
	public int getNumNodes() {
		return allNodes.size();
	}

	/**
	 * If the graph is directed, tests if there is an edge going from "source" 
	 * to "target". 
	 * If the graph is undirected, tests if there is any edge with these two
	 * endpoints (in any order). 
	 */
	public boolean hasEdge(int source, int target) {
		Node sourceNode = allNodes.get(source);
		Node targetNode = allNodes.get(target);
		Node[] endNodes;
		
		Edge[] edgesToSearch;
		
		if (directedEdges) {
			edgesToSearch = sourceNode.getOutEdges(); 
		} else {
			edgesToSearch = sourceNode.getEdges();
		}
		
		for (Edge edge : edgesToSearch) {
			endNodes = edge.getNodees();
			if (endNodes[1].equals(targetNode)) {
				return true;
			}
		}		
		
		return false;
	}
	
	/**
	 * Converts this object to an object of "Graph" class. 
	 */
	public Graph toGraph(String name) {
		Node[] ns = new Node[allNodes.size()];		
		allNodes.toArray(ns);		
		return new Graph(name, ns);
	}
	
}
