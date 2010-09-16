/* MaxInstantaneousIdlenessMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.graph.DynamicVertex;
import model.graph.Vertex;

/**
 * Implements the metric that collects the maximum instantaneous idleness of the
 * graph of the simulation.
 */
public final class MaxInstantaneousIdlenessMetric extends Metric {
	/* Methods. */
	public double getValue() {
		// holds the biggest of all the current idlenesses
		double max_idleness = -1;

		// obtains the vertexes from the graph of the environment
		Vertex[] vertexes = environment.getGraph().getVertexes();

		// for each vertex, checks if its idleness is the biggest one,
		// if the vertex is enabled
		for (int i = 0; i < vertexes.length; i++) {
			Vertex vertex = vertexes[i];

			if (!(vertex instanceof DynamicVertex)
					|| ((DynamicVertex) vertex).isEnabled()) {
				double idleness = vertex.getIdleness();
				if (idleness > max_idleness)
					max_idleness = idleness;
			}
		}

		// returns the biggest idleness
		return max_idleness;
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the buffer
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<metric type=\""
				+ MetricTypes.MAX_INSTANTANEOUS_IDLENESS + "\" value=\""
				+ this.getValue() + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}