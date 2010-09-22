/* AgentCreationEvent.java */

/* The package of this class. */
package util.events;

import util.agent.Agent;
import util.agent.AgentStates;

/* Imported classes and/or interfaces. */

/**
 * Implements the events that are related to the creation of an agent.
 */
public final class AgentCreationEvent {
	/* Attributes. */
	/** The agent just created. */
	private final Agent AGENT;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent
	 *            The agent just being created.
	 * @param society_id
	 *            The id of the society to where the new agent is being added.
	 */
	public AgentCreationEvent(Agent agent) {
		
		this.AGENT = agent;
	}
	

	public void perform_event() {
		AGENT.setState(AgentStates.MOVING);
		
	}

}
