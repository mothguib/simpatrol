/* PerceptionTypes.java */

/* The package of this class. */
package model.perception;

/** Holds the types of perceptions of the agents of SimPatrol.
 *  New types must be added here! */
public abstract class PerceptionTypes {
	/** The perceptions that actually don't require any perception. */
	public static final int EMPTY_PERCEPTION = 0;
	
	/** The perceptions of the graph of the simulation. */
	public static final int GRAPH_PERCEPTION = 1;
}