/* PerceptionPermission.java */

/* The package of this class. */
package model.permission;

/* Imported classes and/or interfaces. */
import model.limitation.Limitation;
import model.perception.PerceptionTypes;

/**
 * Implements the permissions that control the perceptions of an agent in
 * SimPatrol.
 */
public final class PerceptionPermission extends Permission {
	/* Attributes */
	/**
	 * The type of the allowed perceptions.
	 * 
	 * @see PerceptionTypes
	 */
	private int perception_type;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param limitations
	 *            The limitations imposed to the agent.
	 * @param perception_type
	 *            The type of the allowed perceptions.
	 * @see PerceptionTypes
	 */
	public PerceptionPermission(Limitation[] limitations, int perception_type) {
		super(limitations);
		this.perception_type = perception_type;
	}

	/**
	 * Returns the type of the allowed perception.
	 * 
	 * @return The type of the allowed perception.
	 * @see PerceptionTypes
	 */
	public int getPerception_type() {
		return this.perception_type;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<allowed_perception type=\"" + this.perception_type);

		// puts the eventual limitations in the buffer
		if (this.limitations != null) {
			buffer.append("\">\n");

			for (Limitation limitation : this.limitations)
				buffer.append(limitation.fullToXML(identation + 1));

			// closes the buffer tag
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</allowed_perception>\n");
		} else
			buffer.append("\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}