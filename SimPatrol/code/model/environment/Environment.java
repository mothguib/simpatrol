/* Environment.java (2.0) */
package br.org.simpatrol.server.model.environment;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.agent.Society;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.interfaces.XMLable;
import br.org.simpatrol.server.model.metric.Metric;

/**
 * Implements the environment (graph + societies + metrics) where the patrolling
 * task is executed.
 * 
 * @see Graph
 * @see Society
 * @see Metric
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class Environment implements XMLable {
	/* Attributes. */
	/** The graph of the simulation. */
	private Graph graph;

	/** The set of societies of agents involved with the simulation. */
	private Set<Society> societies;

	/** The set of metrics collected during the simulation. */
	private Set<Metric> metrics;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The graph to be pattroled.
	 * @param societies
	 *            The societies of patrollers of the simulation.
	 * @param metrics
	 *            The metrics collected during the simulation.
	 */
	public Environment(Graph graph, Set<Society> societies, Set<Metric> metrics) {
		this.graph = graph;
		this.societies = societies;
		this.metrics = metrics;
	}

	/**
	 * Returns the graph of the environment.
	 * 
	 * @return The graph of the environment.
	 */
	public Graph getGraph() {
		return this.graph;
	}

	/**
	 * Returns the societies of the environment.
	 * 
	 * @return The societies of the environment.
	 */
	public Set<Society> getSocieties() {
		return this.societies;
	}

	/**
	 * Returns the metrics of the environment.
	 * 
	 * @return The metrics of the environment.
	 */
	public Set<Metric> getMetrics() {
		return this.metrics;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// opens the "environment" tag
		buffer.append("<environment>");

		// fills the buffer with the graph
		buffer.append(this.graph.fullToXML());

		// fills the buffer with the societies
		for (Society society : this.societies)
			buffer.append(society.fullToXML());

		// fills the buffer with the metrics
		for (Metric metric : this.metrics)
			buffer.append(metric.fullToXML());

		// closes the "environment" tag
		buffer.append("</environment>");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML() {
		// an environment doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// an environment doesn't need an id
		return null;
	}
}