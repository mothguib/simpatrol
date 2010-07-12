/* MetricTypes.java (2.0) */
package br.org.simpatrol.server.model.metric;

/**
 * Enumerates the types of patrolling metrics implemented by SimPatrol.
 * 
 * @see Metric
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public enum MetricTypes {
	MEAN_INSTANTANEOUS_IDLENESS((byte) 0),
	MAX_INSTANTANEOUS_IDLENESS((byte) 1),
	MEAN_IDLENESS((byte) 2),
	MAX_IDLENESS((byte) 3);

	private byte type;

	private MetricTypes(byte type) {
		this.type = type;
	}

	/**
	 * Returns the type of the metric.
	 * 
	 * @return The type of the metric.
	 */
	public byte getType() {
		return this.type;
	}
}
