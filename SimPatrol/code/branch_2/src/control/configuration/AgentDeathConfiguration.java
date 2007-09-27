/* AgentDeathConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/** Implements objects that express configurations to kill seasonal agents.
 * 
 *  @see Agent */
public class AgentDeathConfiguration extends Configuration {
	/* Attributes. */
	/** The id of the seasonal agent that must be killed. */
	private String agent_id;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param sender_address The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender.
	 *  @param agent_id The id of the seasonal agent that must be killed. */

	public AgentDeathConfiguration(String sender_address, int sender_socket, String agent_id) {
		super(sender_address, sender_socket);
		this.agent_id = agent_id;
	}

	@Override
	protected int getType() {
		return ConfigurationTypes.AGENT_DEATH;
	}

	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.AGENT_DEATH +
				      "\" sender_adress=\"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\" parameter=\"" + this.agent_id +
				      "\"/>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}