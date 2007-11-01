package model.agent;

/**
 * Logs model events
 */
public aspect Logger {

	/**
	 * Setting agent state
	 */
	pointcut setAgentState(Agent agent, int state) : 
		execution(* Agent.setState(int)) && 
		this(agent) && args(state);

	before(Agent agent, int state) : setAgentState(agent, state) {
		if (state != agent.getState()) {
			logger.Logger.getInstance().log(
					"[SimPatrol.Event]: Agent " + agent.getObjectId()
							+ " changed state to " + state + ".");
		}
	}

	/**
	 * Decrements stamina
	 */
	pointcut decrementStamina(Agent agent) : execution(* Agent.decStamina(..)) && this(agent);

	after(Agent agent) : decrementStamina(agent) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event] agent " + agent.getObjectId()
						+ " spent stamina.");
	}

	/**
	 * Agent has died
	 */
	pointcut agentDied(Agent agent) : execution(* SeasonalAgent.die(..)) && this(agent);

	after(Agent agent) : agentDied(agent) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event] agent " + agent.getObjectId() + " died.");
	}
}