/* PerceptionTypes.java */

/* The package of this class. */
package model.perception;

/** Holds the types of perceptions of the agents of SimPatrol.
 * 
 *  @developer New perception types must be added here. */
public abstract class PerceptionTypes {
	/** The perceptions of the graph of the simulation. */
	public static final int GRAPH_PERCEPTION = 0;
	
	/** The perceptions of other agents. */
	public static final int AGENTS_PERCEPTION = 1;
	
	/** The perceptions of stigmas deposited on the graph. */
	public static final int STIGMAS_PERCEPTION = 2;
}