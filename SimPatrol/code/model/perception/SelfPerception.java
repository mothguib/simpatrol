/* SelfPerception.java (2.0) */
package br.org.simpatrol.server.model.perception;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.agent.Agent;

/**
 * Implements the ability of an agent to perceive itself.
 * 
 * @see Agent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class SelfPerception extends Perception {
	/* Attributes. */
	/** The agent itself. */
	private Agent itself;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param itself
	 *            The agent itself.
	 */
	public SelfPerception(Agent itelf) {
		super();
		this.itself = itelf;
	}

	protected void initPerceptionType() {
		this.perceptionType = PerceptionTypes.SELF;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// opens the "perception" tag
		buffer.append("<perception type=\"" + this.perceptionType.getType()
				+ "\">");

		// puts the agent
		buffer.append(this.itself.fullToXML());

		// closes the "perception" tag
		buffer.append("</perception>");

		// returns the answer
		return buffer.toString();
	}
}