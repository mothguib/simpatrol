/* MaxIdlenessMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.graph.DynamicNode;
import model.graph.Node;

/** Implements the metric that collects the biggest idleness the graph ever had. */
public final class MaxIdlenessMetric extends IntegralMetric {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param value
	 *            The initial value of the metric.
	 */
	public MaxIdlenessMetric(double value) {
		super("max idleness", value);
	}

	public double getValue() {
		return this.previous_value;
	}

	public void act() {
		// obtains the nodes from the graph of the environment
		Node[] nodes = environment.getGraph().getNodes();

		// for each node, checks if its idleness is the biggest one,
		// if the node is enabled
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];

			if (!(node instanceof DynamicNode)
					|| ((DynamicNode) node).isEnabled()) {
				double idleness = node.getIdleness();
				if (idleness > this.previous_value)
					this.previous_value = idleness;
			}
		}
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the buffer
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<metric type=\"" + MetricTypes.MAX_IDLENESS
				+ "\" value=\"" + this.getValue() + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
