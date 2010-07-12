/* ActionAbility.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.action.ActionTypes;
import br.org.simpatrol.server.model.limitation.Limitation;

/**
 * Implements the abilities related to the actions of an agent, in SimPatrol.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class ActionAbility extends Ability {
	/* Attributes */
	/** The type of the action of which ability is this object. */
	private ActionTypes actionType;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param limitations
	 *            The limitations imposed to the ability of the agent.
	 * @param actionType
	 *            The type of the action of which ability is this object.
	 */
	public ActionAbility(Set<Limitation> limitations, ActionTypes actionType) {
		super(limitations);
		this.actionType = actionType;
	}

	/**
	 * Returns the type of the action of which ability is this object.
	 * 
	 * @return The type of the action of which ability is this object.
	 */
	public ActionTypes getActionType() {
		return this.actionType;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action_ability type=\"" + this.actionType.getType());

		// puts the eventual limitations in the buffer
		if (this.limitations != null && this.limitations.size() > 0) {
			buffer.append("\">");

			for (Limitation limitation : this.limitations)
				buffer.append(limitation.fullToXML());

			// closes the buffer tag
			buffer.append("</action_ability>");
		} else
			buffer.append("\"/>");

		// returns the answer
		return buffer.toString();
	}
}