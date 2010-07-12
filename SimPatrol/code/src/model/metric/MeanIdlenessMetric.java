/* MeanIdlenessMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.graph.DynamicNode;
import model.graph.Node;

/**
 * Implements the metric that collects the current mean idleness of the graph of
 * the simulation.
 */
public final class MeanIdlenessMetric extends IntegralMetric {
	/* Attributes. */
	/** Counts how many times the value of the metric is being calculated. */
	private int collectings_count;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param value
	 *            The initial value of the metric.
	 */
	public MeanIdlenessMetric(double value) {
		super("mean idleness", value);
		this.collectings_count = 1;
	}

	public double getValue() {
		return this.previous_value * Math.pow(this.collectings_count, -1);
	}

	public void act() {
		// increases the collecting_count attribute
		this.collectings_count++;

		// holds the sum of all the current idlenesses
		double idlenesses_sum = 0;

		// holds how many nodes are being considered in the mean
		int nodes_count = 0;

		// obtains the nodes from the graph of the environment
		Node[] nodes = environment.getGraph().getNodes();

		// for each enabled node, adds its idleness to the sum
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];

			if (!(node instanceof DynamicNode)
					|| ((DynamicNode) node).isEnabled()) {
				idlenesses_sum = idlenesses_sum + node.getIdleness();
				nodes_count++;
			}
		}

		// adds the current mean idleness to the previous collected value
		this.previous_value = this.previous_value + idlenesses_sum
				* Math.pow(nodes_count, -1);
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the buffer
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<metric type=\"" + MetricTypes.MEAN_IDLENESS
				+ "\" value=\"" + this.getValue() + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
