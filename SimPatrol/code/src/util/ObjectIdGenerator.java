/* ObjectIdGenerator.java */

/* The package of this class. */
package util;

/** Implements a generator of IDs for objects. */ 
public abstract class ObjectIdGenerator {
	/** Generates an unique random time related id to the given object.
	 *  @return The object ID. */
	public static String generateObjectId(Object object) {
		StringBuffer id = new StringBuffer();
		
		id.append(object.getClass().getName() + "@" +
				  Integer.toHexString(object.hashCode()) + "#" +
				  (Math.random()));
		
		return id.toString();
	}
}
