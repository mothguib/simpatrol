/* PerceptionAbility.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.limitation.Limitation;
import br.org.simpatrol.server.model.perception.PerceptionTypes;

/**
 * Implements the abilities related to the perceptions of an agent, in
 * SimPatrol.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class PerceptionAbility extends Ability {
	/* Attributes */
	/** The type of the perception of which ability is this object. */
	private PerceptionTypes perceptionType;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param limitations
	 *            The limitations imposed to the ability of the agent.
	 * @param perceptionType
	 *            The type of the perception of which ability is this object.
	 */
	public PerceptionAbility(Set<Limitation> limitations,
			PerceptionTypes perceptionType) {
		super(limitations);
		this.perceptionType = perceptionType;
	}

	/**
	 * Returns the type of the perception of which ability is this object.
	 * 
	 * @return The type of the perception of which ability is this object.
	 */
	public PerceptionTypes getPerceptionType() {
		return this.perceptionType;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<perception_ability type=\""
				+ this.perceptionType.getType());

		// puts the eventual limitations in the buffer
		if (this.limitations != null && this.limitations.size() > 0) {
			buffer.append("\">");

			for (Limitation limitation : this.limitations)
				buffer.append(limitation.fullToXML());

			// closes the buffer tag
			buffer.append("</perception_ability>");
		} else
			buffer.append("\"/>");

		// returns the answer
		return buffer.toString();
	}
}