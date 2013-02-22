package util.graph2.algorithms;

import util.graph2.Graph;
import util.graph2.Node;
import util.graph2.Path;
import util.graph2.Representation;


public class AllPairsShortestPaths {
	private double[][] distance;
	private int[][] predecessor;
	private int[][] successor;

	private static final int INFINITE = Integer.MAX_VALUE / 2;
	
	
	public AllPairsShortestPaths() {
		
	}
	
	public void findShortestPaths(Graph g) {
		int numVertices = g.getNumVertices();

		distance = new double[numVertices][numVertices];
		predecessor = new int[numVertices][numVertices];
		successor = new int[numVertices][numVertices];

		for (int v = 0; v < numVertices; v ++) {
			for (int u = 0; u < numVertices; u++) {
				if (v == u) {
					distance[v][u] = 0;
					predecessor[v][u] = -1;
					successor  [v][u] = -1;
				
				} else if (g.existsEdge(v, u)) {
					distance[v][u] = g.getLength(v,u);
					predecessor[v][u] = v;
					successor  [v][u] = u;
					
				} else {
					distance[v][u] = INFINITE;
					predecessor[v][u] = -1;
					successor  [v][u] = -1;
				}
			}
		}
		
		// escolhe vertice intermediario
		for (int k = 0; k < numVertices; k++) {
			// escolhe origem
	  		for (int i = 0; i < numVertices; i++) {
				// escolhe destino
	    		for (int j = 0; j < numVertices; j++) {
	    			// se i-->k + k-->j for menor do que o caminho atual i-->j
	      			if ((distance[i][k] + distance[k][j]) < distance[i][j]) {
	        			// entao reduz a distancia do caminho i-->j fazendo i-->k-->j
	        			distance[i][j] = distance[i][k] + distance[k][j];
	        			
	        			predecessor[i][j] = predecessor[k][j];
	        			successor  [i][j] = successor[i][k];
	      			}
	    		}
	  		}
		}

	}
	
	/**
	 * Returns "true" iff there is a path from 'source' to 'destiny'.
	 */
	public boolean existsPath(int source, int destiny) {
		return distance[source][destiny] != INFINITE;
	}

	/**
	 * Returns the path from 'source' to 'destiny' or null if no such path exists.
	 */
	public Path getPath(int source, int destiny) {
		Path path;
		
		if (distance[source][destiny] != INFINITE) {
			path =  new Path();
			getPathInternal(source, destiny, path);
		} else {
			path = null;
		}

		return path;
	}

	private void getPathInternal(int source, int destiny, Path path) {
		if (destiny == source) {
			path.add(destiny);
		} else {
			getPathInternal(source, predecessor[source][destiny], path);
			path.add(destiny);
		}
	}
	
	/**
	 * Returns the node immediately after 'source' in the minimum-cost path from 'source' to 'destiny'.
	 * Nodes 'source' and 'destiny' can't be the same.
	 */
	public int getSourceSuccessor(int source, int destiny) {
		return successor[source][destiny];
	}
	
	/**
	 * Returns the node immediately before 'destiny' in the minimum-cost path from 'source' to 'destiny'.
	 * Nodes 'source' and 'destiny' can't be the same.
	 */
	public int getDestinyPredecessor(int source, int destiny) {
		return predecessor[source][destiny];
	}
	
	/**
	 * Returns the minimum distance from 'source' to 'destiny'.
	 */
	public double getDistance(int source, int destiny) {
		if (distance[source][destiny] == INFINITE) {
			return Integer.MAX_VALUE;
		} else {
			return distance[source][destiny];
		}
	}

	/**
	 * Returns a complete graph where the edges are weighted by
	 * the minimum distances between nodes.
	 */
	public Graph toDistancesGraph() {
		int order = this.distance.length;
		Graph graph = new Graph("full-distances", order, Representation.MIXED);
		
		for (int v = 0; v < order; v++) {
			for (int x = 0; x < order; x++) {
				if (distance[v][x] != INFINITE) {
					graph.addEdge("minPath("+v+","+x+")", v, x, distance[v][x]);
				}
			}
		}
		
		return graph;
	}
	
	public static void main(String[] args) {
		Graph graph = new Graph(5);
		Node a, b, c, d, e; 
		
		a = graph.addNode("a"); b = graph.addNode("b"); c = graph.addNode("c"); 
		d = graph.addNode("d"); e = graph.addNode("e");
		
		graph.addEdge("", a, b, 1.0);
		graph.addEdge("", a, e, 5.0);
		graph.addEdge("", b, e, 1.0);
		graph.addEdge("", b, c, 1.0);
		graph.addEdge("", b, d, 5.0);
		graph.addEdge("", c, d, 1.0);
		
		//graph = graph.toTranspose();
		
		AllPairsShortestPaths paths = new AllPairsShortestPaths();
		
		paths.findShortestPaths(graph);
		
		String str;
		
		System.out.println("Successors:");
		
		for (int v = 0; v < graph.getNumVertices(); v ++) {			
			for (int u = 0; u < graph.getNumVertices(); u ++) {
				int succ = paths.successor[v][u];
				if (succ == -1) {
					str = "-";					
				} else {
					str = graph.getNode(succ).getIdentifier();
				}
				System.out.printf("%s  ", str);
			}
			System.out.printf("\n");
		}
		
		System.out.println();
		
		for (int v = 0; v < graph.getNumVertices(); v ++) {			
			for (int u = 0; u < graph.getNumVertices(); u ++) {
				if (v != u && paths.existsPath(v, u)) {
					System.out.printf("In the path from %s to %s, the second node is %s\n",
							graph.getNode(v), graph.getNode(u), 
							graph.getNode(paths.getSourceSuccessor(v,u)));
				}
			}			
		}
		
	}
	
}
