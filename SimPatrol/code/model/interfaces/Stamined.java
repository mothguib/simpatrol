/* Stamined.java (2.0) */
package br.org.simpatrol.server.model.interfaces;

/**
 * Lets the objects that implement it have a stamina feature.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public interface Stamined {
	/**
	 * Configures the stamina of the object.
	 * 
	 * Its value cannot be bigger than the max possible stamina value. If so,
	 * the set value will be the max possible value.
	 * 
	 * @param stamina
	 *            The stamina of the object.
	 */
	void setStamina(double stamina);

	/**
	 * Returns the stamina of the object.
	 * 
	 * @return The stamina of the object.
	 */
	double getStamina();

	/**
	 * Decrements the stamina of the object by the given factor.
	 * 
	 * The stamina value cannot be smaller than zero.
	 * 
	 * @param factor
	 *            The factor to be decremented from the stamina value.
	 */
	void decStamina(double factor);

	/**
	 * Increments the stamina of the object by the given factor.
	 * 
	 * The stamina value cannot be bigger than the max possible stamina value.
	 * 
	 * @param factor
	 *            The factor to be added to the stamina value.
	 */
	void incStamina(double factor);

	/**
	 * Configures the maximum possible value for the stamina of the object.
	 * 
	 * If the given value is smaller than the current stamina of the object,
	 * such attribute will be changed to the given value.
	 * 
	 * @param maxStamina
	 *            The maximum possible value for the stamina of the object.
	 */
	void setMaxStamina(double maxStamina);

	/**
	 * Returns the maximum possible value for the stamina of the object.
	 * 
	 * @return The maximum possible value for the stamina of the object.
	 */
	double getMaxStamina();
}