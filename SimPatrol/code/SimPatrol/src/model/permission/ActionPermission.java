/* ActionPermission.java */

/* The package of this class. */
package model.permission;

/* Imported classes and/or interfaces. */
import model.action.ActionTypes;
import model.limitation.Limitation;

/**
 * Implements the permissions that control the actions of an agent in SimPatrol.
 */
public final class ActionPermission extends Permission {
	/* Attributes */
	/**
	 * The type of the allowed actions.
	 * 
	 * @see ActionTypes
	 */
	private int action_type;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param limitations
	 *            The limitations imposed to the agent.
	 * @param action_type
	 *            The type of the allowed actions.
	 * @see ActionTypes
	 */
	public ActionPermission(Limitation[] limitations, int action_type) {
		super(limitations);
		this.action_type = action_type;
	}

	/**
	 * Returns the type of the allowed actions.
	 * 
	 * @return The type of the allowed actions.
	 */
	public int getAction_type() {
		return this.action_type;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<allowed_action type=\"" + this.action_type);

		// puts the eventual limitations in the buffer
		if (this.limitations != null) {
			buffer.append("\">\n");

			for (Limitation limitation : this.limitations)
				buffer.append(limitation.fullToXML(identation + 1));

			// closes the buffer tag
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</allowed_action>\n");
		} else
			buffer.append("\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
