/* GraphWithoutVertexesException.java (2.0) */
package br.org.simpatrol.server.model.graph;

/**
 * Exception thrown when a Graph object is instantiated with no valid vertexes.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class GraphWithoutVertexesException extends Exception {
	private static final long serialVersionUID = -5883040936121117674L;

	public GraphWithoutVertexesException() {
		super("A graph must have at least one valid vertex.");
	}
}
