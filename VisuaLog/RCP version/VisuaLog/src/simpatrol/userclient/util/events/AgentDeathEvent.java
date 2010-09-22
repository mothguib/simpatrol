/* AgentDeathEvent.java */

/* The package of this class. */
package simpatrol.userclient.util.events;

import simpatrol.userclient.util.agent.Agent;
import simpatrol.userclient.util.agent.AgentStates;

/** Implements the events that are related to the death of an agent. */
public final class AgentDeathEvent{
	
	private final Agent AGENT;
	
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 */
	public AgentDeathEvent(Agent agent) {
		this.AGENT = agent;
	}

	public void perform_event() {
		AGENT.setState(AgentStates.DEAD);
		
	}
}