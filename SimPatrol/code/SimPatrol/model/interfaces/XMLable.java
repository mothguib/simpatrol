/* XMLable.java (2.0) */
package br.org.simpatrol.server.model.interfaces;

/**
 * Lets the objects that implement it have a XML version.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public interface XMLable {
	/**
	 * Returns the "id" of the object (i.e. the string value used to identify
	 * the object).
	 * 
	 * @return The "id" of the object.
	 */
	String getId();

	/**
	 * Returns a XML version of the object with all of its attributes.
	 * 
	 * @return A complete XML version of the object.
	 */
	String fullToXML();

	/**
	 * Returns a XML version of the object with only its essential attributes.
	 * 
	 * @return A simplified XML version of the object.
	 */

	String reducedToXML();
}
