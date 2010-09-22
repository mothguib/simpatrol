package util.agent;


public class AgentStates {
	
	public static final int DEAD = -1;
	/**
	 * The state of teleporting through the graph.
	 * 
	 * @see TeleportAction
	 */
	public static final int MOVING = 0;

	/**
	 * The state of visiting vertexes.
	 * 
	 * @see VisitAction
	 */
	public static final int VISITING = 1;

	/**
	 * The state of broadcasting messages through the graph.
	 * 
	 * @see BroadcastAction
	 */
	public static final int BROADCASTING = 2;

	/**
	 * The state of putting stigmas on the graph.
	 * 
	 * @see StigmatizeAction
	 */
	public static final int STIGMATIZING = 3;

	/**
	 * The state of recharging stamina.
	 * 
	 * @see RechargeAction
	 */
	public static final int RECHARGING = 4;
	

}
