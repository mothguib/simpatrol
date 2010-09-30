package tools.graph_generator;

import java.util.Random;

import util.graph.Graph;
import util.graph.GraphBuilder;
import util.graph.GraphTranslator;
import util.graph.Node;


public class RandomGraphGenerator {
	private Random random;

	
	public RandomGraphGenerator() {
		random = new Random();
	}

	/**
	 * Generates a random graph with undirected edges and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of neighbors randomly chosen 
	 * from the interval [minDegree; maxDegree]. The neighbors are randomly chosen
	 * from the set of all nodes. All edges have unitary weights. 
	 */
	public Graph generateUndirected(int numVertices, int minDegree, int maxDegree) {
		return generateUndirected(numVertices, minDegree, maxDegree, 1, 1);
	}

	/**
	 * Generates a random graph with undirected edges and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of neighbors uniformly chosen 
	 * from the interval [minDegree; maxDegree]. The neighbors are randomly chosen
	 * from the set of all nodes. The edges are weighted with values (costs) randomly 
	 * chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateUndirected(int numVertices, int minDegree, int maxDegree, int minWeight, int maxWeight) {
		GraphBuilder g = new GraphBuilder(numVertices, false);
		
		int[] degree = new int[numVertices];
		
		int randomDegree;
		int neighbor, weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = intRandom(minDegree, maxDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (degree[v] < randomDegree) {
				neighbor = intRandom(v+1, numVertices-1);
				weight = intRandom(minWeight, maxWeight);
		
				added = false;
				
				for (int x = neighbor; x < numVertices; x++) {
					if (degree[x] < maxDegree && !g.hasArc(v, x)) {
						g.addEdge(v, x, weight);
						degree[v] ++;
						degree[x] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
		}
		
		return g.toGraph("random-undirected");
	}
	
	/**
	 * Generates a random graph with directed edges (arcs) and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of successors (target nodes) 
	 * randomly chosen from the interval [minOutDegree; maxOutDegree]. These successors
	 * are randomly chosen from the set of all nodes. All edges have unitary weights. 
	 */
	public Graph generateDirected(int numVertices, int minOutDegree, int maxOutDegree) {
		return generateDirected(numVertices, minOutDegree, maxOutDegree, 1, 1);
	}

	/**
	 * Generates a random graph with directed edges (arcs) and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of successors (target nodes) 
	 * randomly chosen from the interval [minOutDegree; maxOutDegree]. These successors
	 * are randomly chosen from the set of all nodes. The edges are weighted with values
	 * (costs) randomly chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateDirected(int numVertices, int minOutDegree, int maxOutDegree, int minWeight, int maxWeight) {
		GraphBuilder g = new GraphBuilder(numVertices, true);
		
		int[] outdegree = new int[numVertices];
		
		int randomDegree;
		int neighbor, neighborInc;
		int weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = intRandom(minOutDegree, maxOutDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (outdegree[v] < randomDegree) {
				neighbor = intRandom(0, numVertices-1);
				weight = intRandom(minWeight, maxWeight);
		
				added = false;
				
				for (int inc = 0; inc < numVertices; inc++) {
					neighborInc = (neighbor + inc) % numVertices;
					
					if (v != neighborInc && !g.hasArc(v,neighborInc)) {
						g.addEdge(v, neighborInc, weight);
						outdegree[v] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
		}
		
		return g.toGraph("random-directed");
	}

	/**
	 * Generates a random complete graph (i.e. an undirected graph with all possible edges) 
	 * with the given number of nodes/vertices. All edges have unitary weights. 
	 */
	public Graph generateComplete(int numVertices) {
		return generateComplete(numVertices, 1, 1);
	}

	/**
	 * Generates a random complete graph (i.e. an undirected graph with all possible edges) 
	 * with the given number of nodes/vertices. The edges are weighted with values (costs) 
	 * randomly chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateComplete(int numVertices, int minWeight, int maxWeight) {
		GraphBuilder graph = new GraphBuilder(numVertices, false);
		int capacity;
		
		for (int v = 0; v < numVertices; v++) {
			for (int x = v+1; x < numVertices; x++) {
				capacity = intRandom(minWeight, maxWeight);
				
				graph.addEdge(v, x, capacity);
			}
		}
		
		return graph.toGraph("random-complete");
	}

	/**
	 * Generates an undirected Eulerian graph (i.e. a graph with undirected edges in which 
	 * it is possible to find a cycle which uses each edge exactly once) with the given 
	 * number of nodes/vertices. Each node of the graph has an even number of neighbors 
	 * (to assure Eulerianity) randomly chosen from the interval [2; 2*(avgDegree-1)]. 
	 * The neighbors are randomly chosen from the set of all nodes. All edges have unitary
	 * weights. 
	 */
	public Graph generateUndirectedEulerian(int numVertices, int avgDegree) {
		return generateUndirectedEulerian(numVertices, avgDegree, 1, 1);
	}

	/**
	 * Generates an undirected Eulerian graph (i.e. a graph with undirected edges in which 
	 * it is possible to find a cycle which uses each edge exactly once) with the given 
	 * number of nodes/vertices. Each node of the graph has an even number of neighbors 
	 * (to assure Eulerianity) randomly chosen from the interval [2; 2*(avgDegree-1)]. 
	 * The neighbors are randomly chosen from the set of all nodes. The edges are weighted 
	 * with values (costs) randomly chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateUndirectedEulerian(int numVertices, int avgDegree, int minWeight, int maxWeight) {
		GraphBuilder g = new GraphBuilder(numVertices, false);
		
		int minDegree = 2;
		int maxDegree = avgDegree + (avgDegree - 2);
		
		int[] degree = new int[numVertices];
		
		int randomDegree;
		int neighbor, weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = intRandom(minDegree, maxDegree);

			// v may already have a higher degree
			if (degree[v] > randomDegree) {
				
				// if degree is odd, turns randomDegree an even number
				if ((degree[v] % 2) == 1) {
					randomDegree = degree[v] + 1;
				} else {
					randomDegree = degree[v];
				}
			
			} else if ((randomDegree % 2) == 1) {
				
				// turns randomDegree an even number
				if (randomDegree < maxDegree) {
					randomDegree ++;
				} else {
					randomDegree --;
				}
				
			}

			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (degree[v] < randomDegree) {
				neighbor = intRandom(v+1, numVertices-1);
				weight = intRandom(minWeight, maxWeight);
		
				added = false;
				
				for (int x = neighbor; x < numVertices; x++) {
					if (degree[x] < maxDegree && !g.hasArc(v, x)) {
						g.addEdge(v, x, weight);
						degree[v] ++;
						degree[x] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree -= 2;
				}
			}
		}
		
		return g.toGraph("random-eulerian");
	}

	// auxiliary function
	private int intRandom(int from, int to) {
		if (from > to) {
			return from;
		} else {
			return from + random.nextInt(to - from + 1);
		}
	}

	
	public static void main(String[] args) {
		RandomGraphGenerator generator = new RandomGraphGenerator();
		
		Graph graph = generator.generateUndirected(5, 1, 3, 10, 20);

		System.out.print (graph.fullToXML(0));
	}
	
}
