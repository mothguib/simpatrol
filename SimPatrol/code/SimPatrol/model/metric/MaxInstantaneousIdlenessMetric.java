/* MaxInstantaneousIdlenessMetric.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.DynamicVertex;
import br.org.simpatrol.server.model.graph.Vertex;

/**
 * Implements the metric that collects the maximum instantaneous idleness of the
 * graph of the simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class MaxInstantaneousIdlenessMetric extends InstantaneousMetric {
	/* Methods. */
	protected void initMetricType() {
		this.metricType = MetricTypes.MAX_INSTANTANEOUS_IDLENESS;
	}

	protected double getValue() {
		// holds the biggest of all the current idlenesses
		double maxIdleness = -1;

		// obtains the vertexes from the graph of the environment
		Set<Vertex> vertexes = Metric.environment.getGraph().getVertexes();

		// for each vertex, checks if its idleness is the biggest one,
		// if the vertex is enabled
		for (Vertex vertex : vertexes)
			if (!(vertex instanceof DynamicVertex)
					|| ((DynamicVertex) vertex).isEnabled()) {
				double idleness = vertex.getIdleness();
				if (idleness > maxIdleness)
					maxIdleness = idleness;
			}

		// returns the biggest idleness
		return maxIdleness;
	}
}