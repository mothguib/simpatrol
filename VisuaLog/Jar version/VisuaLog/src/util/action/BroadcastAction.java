/* BroadcastAction.java */

/* The package of this class. */
package util.action;

import util.agent.Agent;
import util.agent.AgentStates;


/**
 * Implements the action of broadcasting a message through the graph.
 * 
 * Its effect can be controlled by depth and stamina limitations.
 * 
 * @see DepthLimitation
 * @see StaminaLimitation
 */
public final class BroadcastAction extends AtomicAction {
	/* Attributes. */
	/** The message to be broadcasted. */
	private String message;

	/** The depth of the graph that the broadcasted message must reach. */
	private int message_depth;

	/* Mehods. */
	/**
	 * Constructor.
	 * 
	 * @param message
	 *            The message to be broadcasted.
	 * @param message_depth
	 *            The depth of the graph that the broadcasted message must
	 *            reach.
	 */
	public BroadcastAction(String message, int message_depth) {
		this.message = message;
		this.message_depth = message_depth;
	}

	/**
	 * Returns the broadcasted message.
	 * 
	 * @return The message to be broadcasted.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Returns the depth of the graph that the broadcasted message must reach.
	 * 
	 * @return The depth of the graph that the broadcasted message must reach.
	 */
	public int getMessage_depth() {
		return this.message_depth;
	}

	@Override
	public boolean perform_action(Agent agent) {
		agent.setState(AgentStates.BROADCASTING);
		return true;
		
	}

}