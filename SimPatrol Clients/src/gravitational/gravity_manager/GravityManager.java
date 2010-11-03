package gravitational.gravity_manager;

import util.graph2.Graph;
import util.graph2.Representation;
import util.graph2.algorithms.AllPairsShortestPaths;


/**
 * Gerenciador de gravidades atuantes sobre as arestas de um grafo dado.
 * 
 * A gravidade exercida sobre cada vértice atua sobre a aresta do menor
 * caminho, e a sua intensidade é dada pela massa dividida pelo inverso 
 * da distância mínima (ou seja, do menor caminho) elevada ao expoente 
 * "distanceExponent" (valores típicos são 1 e 2). 
 * 
 * Em cada aresta, várias gravidades (originadas de diferentes vértices)
 * podem atuar. Cada subclasse define como o valor final da gravidade na
 * aresta vai ser calculada "combinando" esses valores de alguma forma.
 * 
 * @author Pablo
 */
public abstract class GravityManager {
	protected AllPairsShortestPaths shortestPaths;
	
	protected double distanceExponent;
	protected double[][] propagationFactor;
	
	protected double[] masses; // fica com -1, se desfizer a massa
	protected double[][] gravities;

	
	GravityManager(Graph graph, double exponent) {
		int numVertices = graph.getNumVertices();
		
		shortestPaths = new AllPairsShortestPaths();
		shortestPaths.findShortestPaths(graph);

		distanceExponent = exponent;
		propagationFactor = new double[numVertices][numVertices];
		
		for (int v = 0; v < numVertices; v++) {
			for (int x = 0; x < numVertices; x++) {
				if (v != x) {
					propagationFactor[v][x] = 1.0d / Math.pow(shortestPaths.getDistance(v,x), exponent);
				}
			}
		}
		
		masses = new double[numVertices];
		
		// negative mass indicates an unset gravities
		for (int v = 0; v < masses.length; v++) {
			masses[v] = -1.0d; 
		}
		
		gravities = new double[numVertices][numVertices];
	}

	/**
	 * Retorna a gravidade (combinada) que atua na aresta saindo
	 * do vértice "from" e chegando no vértice "to".   
	 */
	public double getGravity(int from, int to) {
		return gravities[from][to];
	}
	
	/**
	 *  Retorna a última massa atribuída ao vértice "v" por meio
	 *  de uma chamada a "applyGravity()".
	 */
	public double getMass(int v) {
		return masses[v];
	}
	
	/**
	 * Calcula a força de gravidade exercida por "origin" sobre todos 
	 * os vértices do grafo, considerando que ele tem massa "originMass".
	 */
	public abstract void applyGravity(int origin, double originMass);
	
	/**
	 * Desfaz a gravidade exercida pelo vértice "origin" sobre todos os
	 * vértices do grafo.
	 */
	public abstract void undoGravity(int origin);

	/**
	 * Aplica a gravidade de todos os vértices para todos os vértices,
	 * considerando que eles tem uma mesma massa.
	 */
	public void applyAllGravities(double generalMass) {
		assert (generalMass >= 0.0d);

		for (int origin = 0; origin < getNumVertices(); origin++) {
			applyGravity(origin, generalMass);
		}
	}

	/**
	 * Desfaz (zera) todas as gravidades.
	 */
	public void undoAllGravities() {
		int numVertices = gravities.length;
		
		for (int origin = 0; origin < numVertices; origin++) {
			if (masses[origin] != -1) {
				undoGravity(origin);
			}
		}

	}
	
	/**
	 * Retorna o número de vértices do grafo.
	 */
	public int getNumVertices() {
		return gravities.length; 
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < gravities.length; i++) {
			for (int j = 0; j < gravities.length; j++) {
				if (gravities[i][j] != 0.0d) {
					builder.append("\t| de v" + i + " para v" + j + ": ");
					builder.append((int)(1000*gravities[i][j]));
					builder.append(" .10^-3\n");
				}
			}
		}
		
		return builder.toString();
	}
	
}
