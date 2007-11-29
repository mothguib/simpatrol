package control.daemon;

import logger.event.AgentBroadcastingEvent;
import logger.event.AgentReceivingMessageEvent;
import logger.event.AgentRechargingEvent;
import logger.event.AgentTeleportingEvent;
import logger.event.AgentVisitEvent;
import logger.event.AgentStigmatizingEvent;
import model.graph.Graph;
import model.graph.Vertex;
import model.stigma.Stigma;
import model.agent.Agent;
import model.agent.Society;
import model.action.TeleportAction;

/**
 * This aspect is responsible for logging data of all daemon classes
 */
public aspect Logger {

	/**
	 * Logs a visit of an agent
	 */
	pointcut setLastVisitTime(ActionDaemon daemon) : call(* Vertex.setLast_visit_time(..)) && this(daemon);

	after(ActionDaemon daemon) : setLastVisitTime(daemon) {
		AgentVisitEvent event = new AgentVisitEvent(daemon.AGENT.getObjectId());
		logger.Logger.send(event);
	}

	/**
	 * Logs the recharging of an agent
	 */
	pointcut incStamina(ActionDaemon daemon) : call(* Agent.incStamina(..)) && this(daemon);

	after(ActionDaemon daemon) : incStamina(daemon) {
		AgentRechargingEvent event = new AgentRechargingEvent(daemon.AGENT
				.getObjectId(), daemon.AGENT.getStamina());		
		logger.Logger.send(event);
	}

	/**
	 * Logs the creation of stigmas in ActionDaemon.attendStigmatizeAction()
	 */
	pointcut attendStigmatizeAction(ActionDaemon daemon, Stigma stigma) : 
		call(* Graph.addStigma(..)) &&
		this(daemon) && 
		withincode(* ActionDaemon.attendStigmatizeAction(..)) &&
		args(stigma);

	after(ActionDaemon daemon, Stigma stigma) : attendStigmatizeAction(daemon, stigma) {
		AgentStigmatizingEvent event = new AgentStigmatizingEvent(daemon.AGENT
				.getObjectId(), stigma);
		logger.Logger.send(event);
	}

	/**
	 * Logs the message broadcasting
	 */
	pointcut broadcastMessage(ActionDaemon daemon) :
		call(* ActionDaemon.broadcastMessage(..))
		&& this(daemon);

	after(ActionDaemon daemon) :
		broadcastMessage(daemon) {
		AgentBroadcastingEvent event = new AgentBroadcastingEvent(daemon.AGENT
				.getObjectId(), daemon.action_message);		
		logger.Logger.send(event);
	}

	/**
	 * Logs the first teleport action
	 */
	pointcut teleportAction1(ActionDaemon daemon) : 
		call(* TeleportAction.assureTeleportVisibilityEffect()) 
		&& withincode(* ActionDaemon.attendTeleportAction(..)) && this(daemon);

	after(ActionDaemon daemon) : teleportAction1(daemon) {
		Agent agent = daemon.AGENT;
		AgentTeleportingEvent event = new AgentTeleportingEvent(agent
				.getObjectId(), agent.getVertex().getObjectId(), agent
				.getEdge().getObjectId(), agent.getElapsed_length());
		logger.Logger.send(event);
	}

	/**
	 * Logs the second teleport action
	 */
	pointcut teleportAction2(ActionDaemon daemon) : 
		call(* TeleportAction.assureTeleportVisibilityEffect()) 
		&& withincode(* ActionDaemon.attendPlannedTeleportAction(..)) && this(daemon);

	after(ActionDaemon daemon) : teleportAction2(daemon) {
		Agent agent = daemon.AGENT;
		
		String edge_id = null;
		double length = 0;
		if(agent.getEdge() != null) {
			edge_id = agent.getEdge().getObjectId();
			length = agent.getElapsed_length(); 
		}
		
		AgentTeleportingEvent event = new AgentTeleportingEvent(agent
				.getObjectId(), agent.getVertex().getObjectId(), edge_id, length);
		logger.Logger.send(event);
	}

	/**
	 * ActionDaemon starts working
	 */
	pointcut startActionDaemon(ActionDaemon daemon) : execution(* ActionDaemon.start(..)) && this(daemon);

	after(ActionDaemon daemon) : startActionDaemon(daemon) {
		logger.Logger.println("[SimPatrol.ActionDaemon("
				+ daemon.AGENT.getObjectId() + ")]: Started working.");
	}

	/**
	 * ActionDaemon stop working
	 */
	pointcut stopActionDaemon(ActionDaemon daemon) : execution(* ActionDaemon.stopWorking(..)) && this(daemon);

	after(ActionDaemon daemon) : stopActionDaemon(daemon) {
		logger.Logger.println("[SimPatrol.ActionDaemon("
				+ daemon.AGENT.getObjectId() + ")]: Stopped working.");
	}

	/**
	 * Main daemon is attending an environment creation request
	 */
	pointcut attendEnvironmentCreationConfiguration(): call(* MainDaemon.attendEnvironmentCreationConfiguration(..));

	before() : attendEnvironmentCreationConfiguration() {
		logger.Logger
				.println("[SimPatrol.MainDaemon]: \"Environment's creation\" configuration received.");
	}

	/**
	 * Main daemon is attending an agent creation request
	 */
	pointcut attendAgentCreationConfiguration(): call(* MainDaemon.attendAgentCreationConfiguration(..));

	before() : attendAgentCreationConfiguration() {
		logger.Logger
				.println("[SimPatrol.MainDaemon]: \"Agent's creation\" configuration received.");
	}

	/**
	 * Main daemon is attending an agent death request
	 */
	pointcut attendAgentDeathConfiguration(): call(* MainDaemon.attendAgentDeathConfiguration(..));

	before() : attendAgentDeathConfiguration() {
		logger.Logger
				.println("[SimPatrol.MainDaemon]: \"Agent's death\" configuration received.");
	}

	/**
	 * Main daemon is attending an event collecting request
	 */
	pointcut attendEventCollectingConfiguration(): call(* MainDaemon.attendEventCollectingConfiguration(..));

	before() : attendEventCollectingConfiguration() {
		logger.Logger
				.println("[SimPatrol.MainDaemon]: \"Event collecting\" configuration received.");
	}

	/**
	 * Main daemon is attending a metric creation request
	 */
	pointcut attendMetricCreationConfiguration(): call(* MainDaemon.attendMetricCreationConfiguration(..));

	before() : attendMetricCreationConfiguration() {
		logger.Logger
				.println("[SimPatrol.MainDaemon]: \"Metric creation\" configuration received.");
	}

	/**
	 * Main daemon is attending a simulation start request
	 */
	pointcut attendSimulationStartConfiguration(): call(* MainDaemon.attendSimulationStartConfiguration(..));

	before() : attendSimulationStartConfiguration() {
		logger.Logger
				.println("[SimPatrol.MainDaemon]: \"Start simulation\" configuration received.");
	}

	/**
	 * MainDaemon starts working
	 */
	pointcut startMainDaemon() : execution(* MainDaemon.start(..));

	after() : startMainDaemon() {
		logger.Logger.println("[SimPatrol.MainDaemon]: Started working.");
	}

	/**
	 * MainDaemon creating agents
	 */
	pointcut createAgent(Agent agent, Society society) : 
		call(* MainDaemon.completeAgentCreationAttendment(..)) && args(agent, society);

	after(Agent agent, Society society) : createAgent(agent, society) {
		logger.Logger.println("[SimPatrol.Event]: Agent "
				+ agent.reducedToXML(0) + " created in society "
				+ society.getObjectId() + ".");
	}

	/**
	 * MetricDaemon starts working
	 */
	pointcut startMetricDaemon(MetricDaemon daemon) : execution(* MetricDaemon.start(..)) && this(daemon);

	after(MetricDaemon daemon) : startMetricDaemon(daemon) {
		logger.Logger.println("[SimPatrol.MetricDaemon("
				+ daemon.getMetric().getClass().getName()
				+ ")]: Started working.");
	}

	/**
	 * MetricDaemon stops working
	 */
	pointcut stopMetricDaemon(MetricDaemon daemon) : execution(*
	MetricDaemon.stopWorking(..)) && this(daemon);

	after(MetricDaemon daemon) : stopMetricDaemon(daemon) {
		logger.Logger.println("[SimPatrol.MetricDaemon("
				+ daemon.getMetric().getClass().getName()
				+ ")]: Stopped working.");
	}

	/**
	 * PerceptionDaemon starts working
	 */
	pointcut startPerceptionDaemon(PerceptionDaemon daemon) : execution(* PerceptionDaemon.start(..)) && this(daemon);

	after(PerceptionDaemon daemon) : startPerceptionDaemon(daemon) {
		logger.Logger.println("[SimPatrol.PerceptionDaemon("
				+ daemon.AGENT.getObjectId() + ")]: Started working.");
	}

	/**
	 * PerceptionDaemon.insertMessage
	 */
	pointcut insertMessage(PerceptionDaemon daemon, String message) : 
		call(* PerceptionDaemon.insertMessage(..)) && this(daemon) && args(message);

	after(PerceptionDaemon daemon, String message) : insertMessage(daemon, message) {
		AgentReceivingMessageEvent event = new AgentReceivingMessageEvent(
				daemon.AGENT.getObjectId(), message);
		logger.Logger.send(event);
	}

	/**
	 * PerceptionDaemon stops working
	 */
	pointcut stopPerceptionDaemon(PerceptionDaemon daemon) : execution(*
	PerceptionDaemon.stopWorking(..)) && this(daemon);

	after(PerceptionDaemon daemon) : stopPerceptionDaemon(daemon) {
		logger.Logger.println("[SimPatrol.PerceptionDaemon("
				+ daemon.AGENT.getObjectId() + ")]: Stopped working.");
	}
}