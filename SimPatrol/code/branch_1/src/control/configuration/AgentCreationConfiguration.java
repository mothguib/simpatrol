/* AgentCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/** Implements objects that express configurations to add
 *  an agent to a simulation.
 *  
 *  @see Agent */
public final class AgentCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The new agent to be added to the simulation. */
	private Agent agent;
	
	/** The id of the society where the new agent must be added. */
	private String society_id;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param sender_address The The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender.
	 *  @param agent The agent to be added.
	 *  @param society_id The id of the society where the agent must be added. */
	public AgentCreationConfiguration(String sender_address, int sender_socket, Agent agent, String society_id) {
		super(sender_address, sender_socket);
		this.agent = agent;
		this.society_id = society_id;
	}
	
	/** Returns the agent of the configuration.
	 * 
	 *  @return The agent pf the configuration. */
	public Agent getAgent() {
		return this.agent;
	}
	
	/** Returns the id of the society where the new agent must be added.
	 * 
	 *  @return The id of the society where the new agent must be added.*/
	public String getSociety_id() {
		return this.society_id;
	}
	
	@Override
	protected int getType() {
		return ConfigurationTypes.AGENT_CREATION; 
	}
	
	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.AGENT_CREATION +
				      "\" sender_adress=\"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\" parameter=\"" + this.society_id +
				      "\">\n");
		
		// puts the agent
		buffer.append(this.agent.fullToXML(identation + 1));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}