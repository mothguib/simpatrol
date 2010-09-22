/* EventTypes.java */

/* The package of this class. */
package util.events;

/**
 * Holds the types of events handled by SimPatrol.
 * 
 * @see Event
 * @developer New event types must be added here.
 */
public abstract class EventTypes {
	/**
	 * The events related to to creation of an agent.
	 * 
	 * @see
	 */
	public static final int AGENT_CREATION = 0;

	/**
	 * The events related to to the death of an agent.
	 * 
	 * @see AgentDeathEvent
	 */
	public static final int AGENT_DEATH = 1;

	/**
	 * The events related to the changing of state of the agents.
	 * 
	 * @see AgentChangingStateEvent
	 */
	public static final int AGENT_CHANGING_STATE = 2;

	/**
	 * The events related to the spending of stamina of the agents.
	 * 
	 * @see AgentSpendingStaminaEvent
	 */
	public static final int AGENT_SPENDING_STAMINA = 3;

	/**
	 * The events related to the recharging of the agents.
	 * 
	 * @see AgentRechargingEvent
	 */
	public static final int AGENT_RECHARGING = 4;

	/**
	 * The events related to the teleporting of the agents.
	 * 
	 * @see AgentTeleportingEvent
	 */
	public static final int AGENT_TELEPORTING = 5;

	/**
	 * The events related to the agents visiting the vertexes.
	 * 
	 * @see AgentVisitEvent
	 */
	public static final int AGENT_VISIT = 6;

	/**
	 * The events related to the agents depositing stigmas on the graph.
	 * 
	 * @see AgentStigmatizingEvent
	 */
	public static final int AGENT_STIGMATIZING = 7;

	/**
	 * The events related to the agents broadcasting messages through the graph.
	 * 
	 * @see AgentBroadcastingEvent
	 */
	public static final int AGENT_BROADCASTING = 8;

	/**
	 * The events related to the agents receiving messages on the graph.
	 * 
	 * @see AgentReceivingMessageEvent
	 */
	public static final int AGENT_RECEIVING_MESSAGE = 9;

	/**
	 * The events related to the enabling / disabling of dynamic vertexes.
	 * 
	 * @see NodeEnablingEvent
	 */
	public static final int VERTEX_ENABLING = 10;

	/**
	 * The events related to the enabling / disabling of edges.
	 * 
	 * @see EdgeEnablingEvent
	 */
	public static final int EDGE_ENABLING = 11;
	
	public static int fromString(String event){
		int p = -1;
		try{
			p = Integer.parseInt(event);
		} catch(NumberFormatException e){
			
			if(event.equals("Creation"))
				return 0;
			else if(event.equals("Death"))
				return 1;
			else if(event.equals("Change of State"))
				return 2;
			else if(event.equals("Spending Stamina"))
				return 3;
			else if(event.equals("Recharge"))
				return 4;
			if(event.equals("Teleport"))
				return 5;
			else if(event.equals("Visit"))
				return 6;
			else if(event.equals("Stigma"))
				return 7;
			else if(event.equals("Broadcast"))
				return 8;
			else if(event.equals("Receive Message"))
				return 9;
			else if(event.equals("Vertex Enabling"))
				return 10;
			else if(event.equals("Edge Enabling"))
				return 11;
	
	
			return -1;
		}
		
		return p;
	}
}
