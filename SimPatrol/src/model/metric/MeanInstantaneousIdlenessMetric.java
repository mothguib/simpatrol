/* MeanInstantaneousIdlenessMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.graph.DynamicNode;
import model.graph.Node;

/**
 * Implements the metric that collects the mean instantaneous idleness of the
 * graph of the simulation.
 */
public final class MeanInstantaneousIdlenessMetric extends Metric {
	/* Methods. */
	public double getValue() {
		// holds the sum of all the current idlenesses
		double idlenesses_sum = 0;

		// holds how many nodes are being considered in the mean
		int nodes_count = 0;

		// obtains the nodes from the graph of the environment
		Node[] nodes = environment.getGraph().getNodes();

		// for each node, adds its idleness to the sum, if it's enabled
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];

			if (!(node instanceof DynamicNode)
					|| ((DynamicNode) node).isEnabled()) {
				idlenesses_sum = idlenesses_sum + node.getIdleness();
				nodes_count++;
			}
		}

		// returns the mean idleness
		return Math.pow(nodes_count, -1) * idlenesses_sum;
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the buffer
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		buffer.append("<metric type=\""
				+ MetricTypes.MEAN_INSTANTANEOUS_IDLENESS + "\" value=\""
				+ this.getValue() + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}