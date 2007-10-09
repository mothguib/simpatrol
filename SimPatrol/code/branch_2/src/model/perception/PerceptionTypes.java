/* PerceptionTypes.java */

/* The package of this class. */
package model.perception;

/** Holds the types of perceptions of the agents of SimPatrol.
 * 
 *  @developer New perception types must be added here. */
public abstract class PerceptionTypes {
	/** The perceptions of the graph of the simulation.
	 *  
	 *  @see GraphPerception */
	public static final int GRAPH_PERCEPTION = 0;
	
	/** The perceptions of other agents.
	 * 
	 *  @see AgentsPerception */
	public static final int AGENTS_PERCEPTION = 1;
	
	/** The perceptions of stigmas deposited on the graph.
	 * 
	 *  @see StigmasPerception */
	public static final int STIGMAS_PERCEPTION = 2;
	
	/** The perceptions of broadcasted messages through the graph.
	 * 
	 *  @see BroadcastPerception */
	public static final int BROADCAST_PERCEPTION = 3;
	
	/** The perceptions of itself.
	 * 
	 *  @see SelfPerception */
	public static final int SELF_PERCEPTION = 4;
}