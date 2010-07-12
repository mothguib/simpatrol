/* GraphPerception.java (2.0) */
package br.org.simpatrol.server.model.perception;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.graph.Graph;

/**
 * Implements the perceptions of the graph of the simulation.
 * 
 * @see Graph
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class GraphPerception extends Perception {
	/* Attributes. */
	/** The perceived graph. */
	private Graph graph;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The perceived graph.
	 */
	public GraphPerception(Graph graph) {
		super();
		this.graph = graph;
	}

	protected void initPerceptionType() {
		this.perceptionType = PerceptionTypes.GRAPH;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// mounts the answer
		buffer.append("<perception type=\"" + this.perceptionType.getType()
				+ "\">");

		// puts the graph, in a lighter version
		buffer.append(this.graph.reducedToXML());

		// closes the buffer content
		buffer.append("</perception>");

		// returns the answer
		return buffer.toString();
	}
}