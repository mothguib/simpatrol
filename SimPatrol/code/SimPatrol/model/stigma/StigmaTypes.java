/* StigmaTypes.java (2.0) */
package br.org.simpatrol.server.model.stigma;

/**
 * Holds the types of {@link Stigma} implemented in SimPatrol.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public enum StigmaTypes {
	PHEROMONE((byte) 0),
	MESSAGED((byte) 1);

	private byte type;

	private StigmaTypes(byte type) {
		this.type = type;
	}

	/**
	 * Returns the type of the stigma.
	 * 
	 * @return The type of the stigma.
	 */
	public byte getType() {
		return this.type;
	}
}