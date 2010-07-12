/* MaxInstantaneousIdlenessMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.graph.DynamicNode;
import model.graph.Node;

/**
 * Implements the metric that collects the maximum instantaneous idleness of the
 * graph of the simulation.
 */
public final class MaxInstantaneousIdlenessMetric extends Metric {
	/* Methods. */
	public double getValue() {
		// holds the biggest of all the current idlenesses
		double max_idleness = -1;

		// obtains the nodes from the graph of the environment
		Node[] nodes = environment.getGraph().getNodes();

		// for each node, checks if its idleness is the biggest one,
		// if the node is enabled
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];

			if (!(node instanceof DynamicNode)
					|| ((DynamicNode) node).isEnabled()) {
				double idleness = node.getIdleness();
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