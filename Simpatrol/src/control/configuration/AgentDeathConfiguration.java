/* AgentDeathConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.agent.SeasonalAgent;

/**
 * Implements objects that express configurations to kill seasonal agents.
 * 
 * @see SeasonalAgent
 */
public final class AgentDeathConfiguration extends Configuration {
	/* Attributes. */
	/** The id of the seasonal agent that must be killed. */
	private final String AGENT_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the seasonal agent that must be killed.
	 */
	public AgentDeathConfiguration(String agent_id) {
		this.AGENT_ID = agent_id;
	}

	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the "configuration" tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.AGENT_DEATH
				+ "\" parameter=\"" + this.AGENT_ID + "\"/>\n");

		// return the answer to the method
		return buffer.toString();
	}
}