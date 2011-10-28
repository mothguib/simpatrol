package util.graph2;

import java.util.LinkedList;
import java.util.List;


/**
 * Represents a path with repetitions of vertexes allowed 
 * (i.e. its not necessarily a simple path).  
 *
 * @author Pablo
 */
public class Path {
	protected LinkedList<Integer> vertexes;
	
	
	public Path() {
		vertexes = new LinkedList<Integer>();
	}
	
	public Path(List<Integer> list) {
		vertexes = new LinkedList<Integer>(list);
	}
	
	
	public void add(int v) {
		vertexes.add(v);
	}

	/**
	 * Returns the path as a list of vertexes.
	 * 
	 * Attention: it is not recommended to return the internal 
	 * state of the object; return a copy, instead.
	 */
	public List<Integer> toVertexList() {
		return new LinkedList<Integer>(vertexes);
	}
	
	/**
	 * Tests if the (directed) edges of the path really exist 
	 * in the graph.
	 */
	public boolean isValid(Graph graph) {
		return getCost(graph) == -1;
	}
	
	/**
	 * Calculates the cost of the path in the given graph. 
	 * Returns -1 if this is not a valid path in the graph.
	 */
	public int getCost(Graph graph) {
		List<Integer> path = toVertexList();
		int cost = 0;
		
		for (int i = 1; i < path.size(); i++) {
			if (! graph.existsEdge(path.get(i-1), path.get(i)) ) {
				System.err.println(">> não existe aresta (" + path.get(i-1) + ", " + path.get(i) + ")!");
				return -1;
			}
			cost += graph.getLength(path.get(i-1), path.get(i));
		}
		
		return cost;
	}
	
	/**
	 * Tests if the start and end vertex of the path are the same 
	 * i.e. tests if this is a closed path (not necessarily simple).
	 */
	public boolean isCycle(Graph graph) {
		List<Integer> list = toVertexList();
		
		int first = list.get(0);
		int last  = list.get(list.size()-1);
		
		return (first == last);
	}

	public String toString() {
		return vertexes.toString();
	}
	
}
