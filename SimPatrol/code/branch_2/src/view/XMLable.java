/* XMLable.java */

/* The package of this interface. */
package view;

/** Lets the objects that implement it have a XML version. */
public interface XMLable {
	/**
	 * Translates the full object to XML, formatted in the given identation.
	 * 
	 * @param identation
	 *            The identation to print the XML version of the object.
	 * @return The XML version of the object.
	 */
	public String fullToXML(int identation);

	/**
	 * Translates the object to XML, in a lighter version than the full one,
	 * formatted in the given identation.
	 * 
	 * @param identation
	 *            The identation to print the XML version of the object.
	 * @return The XML version of the object.
	 */
	public String reducedToXML(int identation);

	/**
	 * Forces the object to have a unique id.
	 * 
	 * @return The id of the object.
	 */
	public String getObjectId();

	/**
	 * Forces the object to have a unique id.
	 * 
	 * @param object_id
	 *            The id of the object.
	 */
	public void setObjectId(String object_id);

	/**
	 * Forces the object to have an "equals(object)" modified method, in order
	 * to work based on the unique ids of the compared objects.
	 * 
	 * @param object
	 *            The object to be compared.
	 */
	public boolean equals(Object object);
}