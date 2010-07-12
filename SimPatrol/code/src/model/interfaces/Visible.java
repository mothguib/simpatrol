/* Visible.java */

/* The package of this interface. */
package model.interfaces;

/**
 * Lets the objects that implement it become invisible to the sense of the
 * agents.
 */
public interface Visible {
	/**
	 * Configures the visibility of the object.
	 * 
	 * @param visibility
	 *            The visibility.
	 */
	public void setVisibility(boolean visibility);

	/**
	 * Verifies the visibility of the object.
	 * 
	 * @return TRUE if the object is visible, FALSE if not.
	 */
	public boolean isVisible();
}