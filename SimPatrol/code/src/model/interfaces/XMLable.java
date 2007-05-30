/* XMLable.java */

/* The package of this class. */
package model.interfaces;

/** Lets the objects that implement it have a XML version. */
public interface XMLable {
	/** Traduces the object to XML, formatted in the given identation.
	 * @param identation The identation to print the XML version of the object.
	 * @return The XML version of the object. */
	public String toXML(int identation);
	
	/** Forces the object to have a unique id.
	 *  @return The object id. */
	public String getObjectId();
	
	/** Forces the object to have a unique id.
	 *  @param object_id The object id. */
	public void setObjectId(String object_id);
}