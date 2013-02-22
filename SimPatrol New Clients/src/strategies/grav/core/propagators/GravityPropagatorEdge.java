package strategies.grav.core.propagators;

import strategies.grav.core.GravityPropagator;
import util.graph2.Graph;


/**
 * Propagador de gravidades atuantes sobre as arestas de um grafo dado.
 * <br><br>
 * A gravidade sobre cada nó "atraído" atua na direção da aresta do menor caminho até o nó
 * "atrator", cuja massa produzia a gravidade. 
 * <br><br>
 * Em cada direção de caa aresta, várias gravidades (originadas de diferentes nós) podem atuar. 
 * Cada subclasse define como o valor final da gravidade na aresta vai ser calculada, por meio
 * de alguma "combinação" desses valores.
 * 
 * @see GravityPropagator 
 * @author Pablo A. Sampaio
 */
public abstract class GravityPropagatorEdge extends GravityPropagator {
	
	protected double[][] gravities;

	
	GravityPropagatorEdge(Graph g, double exponent) {
		super(g, exponent);
		
		int numVertices = g.getNumVertices();
		gravities = new double[numVertices][numVertices];
	}

	public double getGravity(int attracted, int neighbor) {
		return gravities[attracted][neighbor];
	}
	
	
}
