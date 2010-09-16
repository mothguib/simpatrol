/* Environment.java */

/* The package of this class. */
package model;

/* Imported classes and/or interfaces. */
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import model.agent.Society;
import model.graph.Graph;
import view.XMLable;

/**
 * Implements the environment (graph + societies) where the patrolling task is
 * executed.
 */
public final class Environment implements XMLable {
	/* Attributes. */
	/** The graph of the simulation. */
	private Graph graph;

	/** The set of societies of agents involved with the simulation. */
	private Set<Society> societies;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The graph to be pattroled.
	 * @param societies
	 *            The societies of patrollers of the simulation.
	 */
	public Environment(Graph graph, Society[] societies) {
		this.graph = graph;

		this.societies = Collections.synchronizedSet(new HashSet<Society>());
		for (int i = 0; i < societies.length; i++)
			this.societies.add(societies[i]);
	}

	/**
	 * Returns the graph of the environment.
	 * 
	 * @return The graph.
	 */
	public Graph getGraph() {
		return this.graph;
	}

	/**
	 * Returns the societies of the environment.
	 * 
	 * @return The societies.
	 */
	public Society[] getSocieties() {
		return this.societies.toArray(new Society[0]);
	}

	public String getObjectId() {
		// an environment doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an environment doesn't need an id
		// so, do nothing
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "environment" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<environment>\n");

		// fills the buffer with the graph
		buffer.append(this.graph.fullToXML(identation + 1));

		// fills the buffer with the societies
		for (Society society : this.societies)
			buffer.append(society.fullToXML(identation + 1));

		// applies the identation and closes the "environment" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</environment>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// an environment doesn't have a lighter version
		return this.fullToXML(identation);
	}
}