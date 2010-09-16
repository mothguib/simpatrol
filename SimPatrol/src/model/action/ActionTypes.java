/* ActionTypes.java */

/* The package of this class. */
package model.action;

/**
 * Holds the types of actions for the agents of SimPatrol.
 * 
 * @see Action
 * @developer New action types must be added here.
 */
public abstract class ActionTypes {
	/**
	 * The actions of teleporting through the graph.
	 * 
	 * @see TeleportAction
	 */
	public static final int TELEPORT = 0;

	/**
	 * The actions of moving an agent on the graph.
	 * 
	 * @see GoToAction
	 */
	public static final int GOTO = 1;

	/**
	 * The actions of visiting vertexes.
	 * 
	 * @see VisitAction
	 */
	public static final int VISIT = 2;

	/**
	 * The actions of broadcasting messages through the graph.
	 * 
	 * @see BroadcastAction
	 */
	public static final int BROADCAST = 3;

	/**
	 * The actions of putting stigmas on the graph.
	 * 
	 * @see StigmatizeAction
	 */
	public static final int STIGMATIZE = 4;

	/**
	 * The actions of recharging stamina.
	 * 
	 * @see RechargeAction
	 */
	public static final int RECHARGE = 5;

	/**
	 * The actions of recharging stamina, having immediate effects.
	 * 
	 * @see AtomicRechargeAction
	 */
	public static final int ATOMIC_RECHARGE = 6;
}