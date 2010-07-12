/* LimitationTypes.java (2.0) */
package br.org.simpatrol.server.model.limitation;

/**
 * Enumerates the types of limitations imposed to the abilities of the agents in
 * SimPatrol.
 * 
 * @see Limitation
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public enum LimitationTypes {
	DEPTH((byte) 0),
	STAMINA((byte) 1),
	SPEED((byte) 2),
	ACCELERATION((byte) 3);

	private byte type;

	private LimitationTypes(byte type) {
		this.type = type;
	}

	/**
	 * Returns the type of the limitation.
	 * 
	 * @return The type of the limitation.
	 */
	public byte getType() {
		return this.type;
	}
}