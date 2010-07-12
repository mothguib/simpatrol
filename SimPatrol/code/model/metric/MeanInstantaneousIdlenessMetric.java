/* MeanInstantaneousIdlenessMetric.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.DynamicVertex;
import br.org.simpatrol.server.model.graph.Vertex;

/**
 * Implements the metric that collects the mean instantaneous idleness of the
 * graph of the simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class MeanInstantaneousIdlenessMetric extends InstantaneousMetric {
	/* Methods. */
	protected void initMetricType() {
		this.metricType = MetricTypes.MEAN_INSTANTANEOUS_IDLENESS;
	}

	protected double getValue() {
		// holds the sum of all the current idlenesses
		double idlenessesSum = 0;

		// holds how many vertexes are being considered in the mean value
		int vertexesCount = 0;

		// obtains the vertexes from the graph of the environment
		Set<Vertex> vertexes = Metric.environment.getGraph().getVertexes();

		// for each vertex, adds its idleness to the sum, if it's enabled
		for (Vertex vertex : vertexes)
			if (!(vertex instanceof DynamicVertex)
					|| ((DynamicVertex) vertex).isEnabled()) {
				idlenessesSum = idlenessesSum + vertex.getIdleness();
				vertexesCount++;
			}

		// returns the mean idleness
		return idlenessesSum * Math.pow(vertexesCount, -1);
	}
}