/* MaxIdlenessMetric.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.DynamicVertex;
import br.org.simpatrol.server.model.graph.Vertex;

/**
 * Implements the metric that registers the biggest idleness the graph ever had.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class MaxIdlenessMetric extends IntegralMetric {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param initialValue
	 *            The initial value of the metric.
	 */
	public MaxIdlenessMetric(double initialValue) {
		super(initialValue);
	}

	protected void initMetricType() {
		this.metricType = MetricTypes.MAX_IDLENESS;
	}

	protected double getValue() {
		return this.previousValue;
	}

	public void collect() {
		// obtains the vertexes from the graph of the environment
		Set<Vertex> vertexes = Metric.environment.getGraph().getVertexes();

		// for each vertex, checks if its idleness is the biggest one, if the
		// vertex is enabled
		for (Vertex vertex : vertexes)
			if (!(vertex instanceof DynamicVertex)
					|| ((DynamicVertex) vertex).isEnabled()) {
				double idleness = vertex.getIdleness();
				if (idleness > this.previousValue)
					this.previousValue = idleness;
			}
	}
}
