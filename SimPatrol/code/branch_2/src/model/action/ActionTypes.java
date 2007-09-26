/* ActionTypes.java */

/* The package of this class. */
package model.action;

/** Holds the types of actions for the agents of SimPatrol.
 * 
 *  @see Action
 *  @developer New action types must be added here. */
public class ActionTypes {
	/** The actions of teleporting through the graph.
	 *  
	 *  @see TeleportAction */
	public static final int TELEPORT_ACTION = 0;
	
	/** The actions of moving an agent on the graph.
	 * 
	 *  @see GoToAction*/
	public static final int GOTO_ACTION = 1;
	
	/** The actions of visiting vertexes.
	 *
	 *  @see VisitAction */
	public static final int VISIT_ACTION = 2;
	
	/** The actions of broadcasting messages through the graph.
	 * 
	 * @see BroadcastAction */
	public static final int BROADCAST_ACTION = 3;
	
	/** The actions of putting stigmas on the graph.
	 * 
	 *  @see StigmatizeAction */
	public static final int STIGMATIZE_ACTION = 4;
	
	/** The actions of recharging stamina.
	 * 
	 *  @see RechargeAction */
	public static final int RECHARGE_ACTION = 5;
	
	/** The actions of recharging stamina, having
	 *  immediate effects.
	 * 
	 *  @see AtomicRechargeAction */
	public static final int ATOMIC_RECHARGE_ACTION = 6;
}