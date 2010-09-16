/* AgentCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.agent.Society;

/**
 * Implements objects that express configurations to add an agent to a
 * simulation.
 */
public final class AgentCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The new agent to be added to the simulation. */
	private final Agent AGENT;

	/**
	 * The id of the society where the new agent must be added.
	 * 
	 * @see Society
	 */
	private final String SOCIETY_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent
	 *            The agent to be added.
	 * @param society_id
	 *            The id of the society where the agent must be added.
	 */
	public AgentCreationConfiguration(Agent agent, String society_id) {
		this.AGENT = agent;
		this.SOCIETY_ID = society_id;
	}

	/**
	 * Returns the agent of the configuration.
	 * 
	 * @return The agent of the configuration.
	 */
	public Agent getAgent() {
		return this.AGENT;
	}

	/**
	 * Returns the id of the society where the new agent must be added.
	 * 
	 * @return The id of the society where the new agent must be added.
	 */
	public String getSociety_id() {
		return this.SOCIETY_ID;
	}

	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the "configuration" tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("<configuration type=\""
				+ ConfigurationTypes.AGENT_CREATION + "\" parameter=\""
				+ this.SOCIETY_ID + "\">\n");

		// puts the agent
		buffer.append(this.AGENT.fullToXML(identation + 1));

		// closes the tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("</configuration>\n");

		// return the answer to the method
		return buffer.toString();
	}
}