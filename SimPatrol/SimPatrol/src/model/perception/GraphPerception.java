/* GraphPerception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import model.graph.Graph;

/** Implements the perceptions of the graph of the simulation. */
public final class GraphPerception extends Perception {
	/* Attributes. */
	/** The perceived graph. */
	private Graph graph;

	/* Methods. */
	/*
	 * Constructor.
	 * 
	 * @param graph The perceived graph.
	 */
	public GraphPerception(Graph graph) {
		this.graph = graph;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<perception type=\"" + PerceptionTypes.GRAPH + "\">\n");

		// puts the graph, in a lighter version
		buffer.append(this.graph.reducedToXML(identation + 1));

		// applies the identation and closes the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</perception>\n");

		// returns the answer
		return buffer.toString();
	}
}