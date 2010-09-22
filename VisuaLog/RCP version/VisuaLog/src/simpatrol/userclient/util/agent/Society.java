/* Society.java */

/* The package of this class. */
package simpatrol.userclient.util.agent;

/* Imported classes and/or interfaces. */
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Implements the societies of agents of SimPatrol. */
public class Society {
	/* Attributes. */
	/**
	 * The object id of the society. Not part of the patrol problem modelling.
	 */
	private String id;

	/** The label of the society. */
	private String label;

	/** The set of agents of the society. */
	protected Set<Agent> agents;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the society.
	 * @param agents
	 *            The agents that compound the society.
	 */
	public Society(String label, Agent[] agents) {
		this.label = label;

		this.agents = Collections.synchronizedSet(new HashSet<Agent>());
		for (int i = 0; i < agents.length; i++)
			this.agents.add(agents[i]);
	}

	/**
	 * Returns the label of the society.
	 * 
	 * @return The label of the society.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Returns the agents of the society.
	 * 
	 * @return The set of agents of the society.
	 */
	public Agent[] getAgents() {
		Object[] agents_array = this.agents.toArray();

		Agent[] answer = new Agent[agents_array.length];
		for (int i = 0; i < answer.length; i++)
			answer[i] = (Agent) agents_array[i];

		return answer;
	}

	/**
	 * Adds a given agent to the society.
	 * 
	 * @param agent
	 *            The agent to be added.
	 * @return TRUE if the agent was added successfully, FALSE if not.
	 */
	public boolean addAgent(Agent agent){
		return agents.add(agent);
	}


	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}