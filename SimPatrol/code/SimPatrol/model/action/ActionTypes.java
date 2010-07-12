/* ActionTypes.java (2.0) */
package br.org.simpatrol.server.model.action;

/**
 * Enumerates the types of actions for the agents of SimPatrol.
 * 
 * @see Action
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public enum ActionTypes {
	TELEPORT((byte) 0),
	GOTO((byte) 1),

	VISIT((byte) 2),
	BROADCAST((byte) 3),
	STIGMATIZE((byte) 4),

	ATOMIC_RECHARGE((byte) 5),
	RECHARGE((byte) 6);

	private byte type;

	private ActionTypes(byte type) {
		this.type = type;
	}

	/**
	 * Returns the type of the action.
	 * 
	 * @return The type of the action.
	 */
	public byte getType() {
		return this.type;
	}
}