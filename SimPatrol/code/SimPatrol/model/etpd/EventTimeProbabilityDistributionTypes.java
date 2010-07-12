/* EventTimeProbabilityDistributionTypes.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/**
 * Holds the event time probability distribution types.
 * 
 * @see EventTimeProbabilityDistribution
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public enum EventTimeProbabilityDistributionTypes {
	UNIFORM((byte) 0),
	EMPIRICAL((byte) 1),
	NORMAL((byte) 2),
	SPECIFIC((byte) 3),
	EXPONENTIAL((byte) 4);

	private byte type;

	private EventTimeProbabilityDistributionTypes(byte type) {
		this.type = type;
	}

	/**
	 * Returns the type of the event time probability distribution (etpd).
	 * 
	 * @return The type of the etpd.
	 */
	public byte getType() {
		return this.type;
	}
}
