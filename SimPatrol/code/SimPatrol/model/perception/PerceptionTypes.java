/* PerceptionTypes.java (2.0) */
package br.org.simpatrol.server.model.perception;

/**
 * Enumerates the types of perceptions of the agents of SimPatrol.
 * 
 * @see Perception
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public enum PerceptionTypes {
	GRAPH((byte) 0),
	AGENTS((byte) 1),
	STIGMAS((byte) 2),
	BROADCAST((byte) 3),
	SELF((byte) 4);

	private byte type;

	private PerceptionTypes(byte type) {
		this.type = type;
	}

	/**
	 * Returns the type of the perception.
	 * 
	 * @return The type of the perception.
	 */
	public byte getType() {
		return this.type;
	}
}