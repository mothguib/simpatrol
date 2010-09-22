/* ActionTypes.java */

/* The package of this class. */
package simpatrol.userclient.util.action;

/**
 * Holds the types of actions for the agents of SimPatrol.
 * 
 * @see Action
 * @developer New action types must be added here.
 */
public class ActionTypes {
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
	
	
	
	public static int fromString(String state){
		if(state.equals("Teleport")||state.equals("5"))
			return 0;
		else if(state.equals("Visit")||state.equals("6"))
			return 2;
		else if(state.equals("Broadcast")||state.equals("8"))
			return 3;
		else if(state.equals("Stigma")||state.equals("7"))
			return 4;
		else if(state.equals("Recharge")||state.equals("4"))
			return 5;
		return -1;
	}
	
}