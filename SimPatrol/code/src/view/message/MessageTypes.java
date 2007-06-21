/* MessageTypes.java */

/* The package of this class. */
package view.message;

/** Holds the types of messages.
 *  @see Message */
public abstract class MessageTypes {
	/** Messages of configuration of the simulation. */
	public static final int CONFIGURATION = 0;
	
	/** Messages of orientation to the configuration messages. */
	public static final int ORIENTATION = 1;
	
	/** Messages of requisition for perceptions. */
	public static final int REQUISITION = 2;
	
	/** Messages of answer to the requisitions for perceptions. */
	public static final int ANSWER = 3;
	
	/** Messages of intention to act. */
	public static final int INTENTION = 4;
}
