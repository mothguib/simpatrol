/* Visible.java (2.0) */
package br.org.simpatrol.server.model.interfaces;

/**
 * Lets the objects that implement it become invisible to the sense of the
 * agents.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public interface Visible {
	/**
	 * Configures the visibility of the object.
	 * 
	 * @param visibility
	 *            The visibility of the object.
	 */
	void setVisible(boolean visibility);

	/**
	 * Verifies the visibility of the object.
	 * 
	 * @return TRUE if the object is visible, FALSE if not.
	 */
	boolean isVisible();
}