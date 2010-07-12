/* MeanIdlenessMetric.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.DynamicVertex;
import br.org.simpatrol.server.model.graph.Vertex;

/**
 * Implements the metric that collects the current mean idleness of the graph of
 * the simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class MeanIdlenessMetric extends IntegralMetric {
	/* Attributes. */
	/** Counts up how many times the value of the metric is being calculated. */
	private int collectingsCount;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param initialValue
	 *            The initial value of the metric.
	 */
	public MeanIdlenessMetric(double initialValue) {
		super(initialValue);
		this.collectingsCount = 1;
	}

	protected void initMetricType() {
		this.metricType = MetricTypes.MEAN_IDLENESS;
	}

	protected double getValue() {
		return this.previousValue * Math.pow(this.collectingsCount, -1);
	}

	public void collect() {
		// increases the collectingsCount attribute
		this.collectingsCount++;

		// holds the sum of all the current idlenesses
		double idlenessesSum = 0;

		// holds how many vertexes are being considered in the mean value
		int vertexesCount = 0;

		// obtains the vertexes from the graph of the environment
		Set<Vertex> vertexes = Metric.environment.getGraph().getVertexes();

		// for each enabled vertex, adds its idleness to the sum
		for (Vertex vertex : vertexes)
			if (!(vertex instanceof DynamicVertex)
					|| ((DynamicVertex) vertex).isEnabled()) {
				idlenessesSum = idlenessesSum + vertex.getIdleness();
				vertexesCount++;
			}

		// adds the current mean idleness to the previously collected values
		this.previousValue = this.previousValue + idlenessesSum
				* Math.pow(vertexesCount, -1);
	}
}