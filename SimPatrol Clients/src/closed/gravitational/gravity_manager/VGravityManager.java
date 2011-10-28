package closed.gravitational.gravity_manager;

import util.graph2.Graph;
import util.graph2.algorithms.AllPairsShortestPaths;


// TODO: talvez criar uma interface �nica
public abstract class VGravityManager extends GravityManager {
	//protected AllPairsShortestPaths shortestPaths;
	
	//protected double distanceExponent;
	//protected double[][] propagationFactor;
	
	//protected double[] masses; // fica com -1, se desfizer a massa
	protected double[] vertexGravities;

	
	VGravityManager(Graph graph, double exponent) {
		super(graph, exponent);

		int numVertices = graph.getNumVertices();
		gravities = null; //apaga o objeto criado na superClasse
//		
//		shortestPaths = new AllPairsShortestPaths();
//		shortestPaths.findShortestPaths(graph);
//
//		distanceExponent = exponent;
//		propagationFactor = new double[numVertices][numVertices];
//		
//		for (int v = 0; v < numVertices; v++) {
//			for (int x = 0; x < numVertices; x++) {
//				if (v == x) {
//					propagationFactor[v][x] = 1.0d;  // to prevent floating-point errors in Math.pow()
//				} else {
//					propagationFactor[v][x] = 1.0d / Math.pow(shortestPaths.getDistance(v,x), exponent);
//				}
//			}
//		}
//		
//		masses = new double[numVertices];
//		
//		// negative mass indicates an unset gravities
//		for (int v = 0; v < numVertices; v++) {
//			masses[v] = -1.0d; 
//		}
		
		vertexGravities = new double[numVertices];
	}
	
	/**
	 * Retorna a for�a da gravidade (combinada) que atrai para o v�rtice
	 * "vertex" agentes posicionados em "from" ou em qualquer outro v�rtice 
	 * vizinho de "vertex". 
	 */
	public double getGravity(int from, int vertex) {
		return vertexGravities[vertex];
	}
	
	/**
	 *  Retorna a �ltima massa atribu�da ao v�rtice "v" por meio
	 *  de uma chamada a "applyGravity()".
	 */
//	public double getMass(int v) {
//		return masses[v];
//	}
	
	/**
	 * Calcula a contribui��o dada pelo v�rtice "origin" para a for�a de
	 * atra��o de todos os v�rtices do grafo, considerando que "origin" tem
	 * sua massa dada por "originMass".
	 */
	public abstract void applyGravity(int origin, double originMass);
	
	/**
	 * Desfaz a contribui��o do v�rtice "origin" sobre todos os v�rtices 
	 * do grafo.
	 */
	public abstract void undoGravity(int origin);
	
	
	/**
	 * Calcula a gravidade de todos os v�rtices para todos os v�rtices,
	 * considerando que eles tem a mesma massa "generalMass".
	 */
	public void applyAllGravities(double generalMass) {
//		assert (generalMass >= 0.0d);
//
//		for (int origin = 0; origin < getNumVertices(); origin++) {
//			applyGravity(origin, generalMass);
//		}
	}

	/**
	 * Desfaz (zera) todas as gravidades.
	 */
//	public void undoAllGravities() {
//		int numVertices = vertexGravities.length;
//		
//		for (int origin = 0; origin < numVertices; origin++) {
//			if (masses[origin] != -1) {
//				undoGravity(origin);
//			}
//		}
//
//	}
	
	/**
	 * Retorna o n�mero de v�rtices do grafo.
	 */
//	public int getNumVertices() {
//		return vertexGravities.length; 
//	}
//	
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < vertexGravities.length; i++) {
			builder.append("\t| em v" + i + ": ");
			builder.append((int)(100000*vertexGravities[i]));
			builder.append(" .10^-5\n");
		}
		
		return builder.toString();
	}
}
