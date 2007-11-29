package model.agent;

import logger.event.AgentChangingStateEvent;
import logger.event.AgentDeathEvent;
import logger.event.AgentSpendingStaminaEvent;

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
		if (state != agent.getAgentState()) {
			AgentChangingStateEvent event = new AgentChangingStateEvent(
					agent.getObjectId());
			logger.Logger.send(event);
		}
	}

	/**
	 * Decrements stamina
	 */
	pointcut decrementStamina(Agent agent) : execution(* Agent.decStamina(..)) && this(agent);

	after(Agent agent) : decrementStamina(agent) {
		AgentSpendingStaminaEvent event = new AgentSpendingStaminaEvent(agent.getObjectId(), agent.getStamina());
		logger.Logger.send(event);
	}

	/**
	 * Agent has died
	 */
	pointcut agentDied(Agent agent) : execution(* SeasonalAgent.die(..)) && this(agent);

	after(Agent agent) : agentDied(agent) {
		AgentDeathEvent event = new AgentDeathEvent(agent.getObjectId());
		logger.Logger.send(event);
	}
}
