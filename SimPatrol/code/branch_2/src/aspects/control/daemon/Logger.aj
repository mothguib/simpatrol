package control.daemon;

import model.graph.Vertex;
import model.graph.Edge;
import model.stigma.Stigma;
import model.agent.Agent;
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
		logger.Logger.getInstance().log(
				"[SimPatrol.Event]: Agent " + daemon.AGENT.getObjectId()
						+ " visited vertex "
						+ daemon.AGENT.getVertex().reducedToXML(0));
	}

	/**
	 * Logs the recharging of an agent
	 */
	pointcut incStamina(ActionDaemon daemon) : call(* Agent.incStamina(..)) && this(daemon);

	after(ActionDaemon daemon) : incStamina(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event]: Agent " + daemon.AGENT.getObjectId()
						+ " recharged.");
	}

	/**
	 * Logs the creation of stigmas in ActionDaemon.attendStigmatizeAction()
	 */
	pointcut attendStigmatizeAction(ActionDaemon daemon, Object object) : 
		call(Stigma.new(Vertex || Edge)) && 
		this(daemon) && 
		withincode(* ActionDaemon.attendStigmatizeAction(..)) &&
		args(object);

	after(ActionDaemon daemon, Object object) : attendStigmatizeAction(daemon, object) {
		String result = "";
		if (object instanceof Vertex) {
			result = "vertex " + daemon.AGENT.getVertex().reducedToXML(0);

		} else if (object instanceof Edge) {
			result = "edge " + daemon.AGENT.getEdge().reducedToXML(0);
		}
		logger.Logger.getInstance().log(
				"[SimPatrol.Event]: Agent " + daemon.AGENT.getObjectId()
						+ " stigmatized " + result);
	}

	/**
	 * Logs the message broadcasting
	 */
	pointcut broadcastMessage(ActionDaemon daemon) : call(* ActionDaemon.broadCastMessage(..)) && this(daemon);

	after(ActionDaemon daemon) : broadcastMessage(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event]: Agent " + daemon.AGENT.getObjectId()
						+ " broadcasted a message.");
	}

	/**
	 * Logs the first teleport action
	 */
	pointcut teleportAction1(ActionDaemon daemon) : 
		call(* TeleportAction.assureTeleportVisibilityEffect()) 
		&& withincode(* ActionDaemon.attendTeleportAction(..)) && this(daemon);

	after(ActionDaemon daemon) : teleportAction1(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event]: Agent " + daemon.AGENT.getObjectId()
						+ " teleported.");
	}

	/**
	 * Logs the second teleport action
	 */
	pointcut teleportAction2(ActionDaemon daemon) : 
		call(* TeleportAction.assureTeleportVisibilityEffect()) 
		&& withincode(* ActionDaemon.attendPlannedTeleportAction(..)) && this(daemon);

	after(ActionDaemon daemon) : teleportAction2(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event]: Agent " + daemon.AGENT.getObjectId()
						+ " teleported to "
						+ daemon.AGENT.getVertex().getObjectId()
						+ ", elapsed length "
						+ daemon.AGENT.getElapsed_length());
	}

	/**
	 * ActionDaemon starts working
	 */
	pointcut startActionDaemon(ActionDaemon daemon) : execution(* ActionDaemon.start(..)) && this(daemon);

	after(ActionDaemon daemon) : startActionDaemon(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.ActionDaemon(" + daemon.AGENT.getObjectId()
						+ ")]: Started working.");
	}

	/**
	 * ActionDaemon stop working
	 */
	pointcut stopActionDaemon(ActionDaemon daemon) : execution(* ActionDaemon.stopWorking(..)) && this(daemon);

	after(ActionDaemon daemon) : stopActionDaemon(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.ActionDaemon(" + daemon.AGENT.getObjectId()
						+ ")]: Stopped working.");
	}

	/**
	 * Main daemon is attending an environment creation request
	 */
	pointcut attendEnvironmentCreationConfiguration(): call(* MainDaemon.attendEnvironmentCreationConfiguration(..));

	before() : attendEnvironmentCreationConfiguration() {
		logger.Logger
				.getInstance()
				.log(
						"[SimPatrol.MainDaemon]: \"Environment's creation\" configuration received.");
	}

	/**
	 * Main daemon is attending an agent creation request
	 */
	pointcut attendAgentCreationConfiguration(): call(* MainDaemon.attendAgentCreationConfiguration(..));

	before() : attendAgentCreationConfiguration() {
		logger.Logger
				.getInstance()
				.log(
						"[SimPatrol.MainDaemon]: \"Agent's creation\" configuration received.");
	}

	/**
	 * Main daemon is attending an agent death request
	 */
	pointcut attendAgentDeathConfiguration(): call(* MainDaemon.attendAgentDeathConfiguration(..));

	before() : attendAgentDeathConfiguration() {
		logger.Logger
				.getInstance()
				.log(
						"[SimPatrol.MainDaemon]: \"Agent's death\" configuration received.");
	}

	/**
	 * Main daemon is attending a metric creation request
	 */
	pointcut attendMetricCreationConfiguration(): call(* MainDaemon.attendMetricCreationConfiguration(..));

	before() : attendMetricCreationConfiguration() {
		logger.Logger
				.getInstance()
				.log(
						"[SimPatrol.MainDaemon]: \"Metric creation\" configuration received.");
	}

	/**
	 * Main daemon is attending a simulation start request
	 */
	pointcut attendSimulationStartConfiguration(): call(* MainDaemon.attendSimulationStartConfiguration(..));

	before() : attendSimulationStartConfiguration() {
		logger.Logger
				.getInstance()
				.log(
						"[SimPatrol.MainDaemon]: \"Start simulation\" configuration received.");
	}

	/**
	 * MainDaemon starts working
	 */
	pointcut startMainDaemon() : execution(* MainDaemon.start(..));

	after() : startMainDaemon() {
		logger.Logger.getInstance().log(
				"[SimPatrol.MainDaemon]: Started working.");
	}

	/**
	 * MetricDaemon starts working
	 */
	pointcut startMetricDaemon(MetricDaemon daemon) : execution(* MetricDaemon.start(..)) && this(daemon);

	after(MetricDaemon daemon) : startMetricDaemon(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.MetricDaemon("
						+ daemon.getMetric().getClass().getName()
						+ ")]: Started working.");
	}

	/**
	 * MetricDaemon stops working
	 */
	pointcut stopMetricDaemon(MetricDaemon daemon) : execution(* MetricDaemon.stopWorking(..)) && this(daemon);

	after(MetricDaemon daemon) : stopMetricDaemon(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.MetricDaemon("
						+ daemon.getMetric().getClass().getName()
						+ ")]: Stopped working.");
	}

	/**
	 * PerceptionDaemon starts working
	 */
	pointcut startPerceptionDaemon(PerceptionDaemon daemon) : execution(* PerceptionDaemon.start(..)) && this(daemon);

	after(PerceptionDaemon daemon) : startPerceptionDaemon(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.PerceptionDaemon(" + daemon.AGENT.getObjectId()
						+ ")]: Started working.");
	}

	/**
	 * PerceptionDaemon stops working
	 */
	pointcut stopPerceptionDaemon(PerceptionDaemon daemon) : execution(* PerceptionDaemon.stopWorking(..)) && this(daemon);

	after(PerceptionDaemon daemon) : stopPerceptionDaemon(daemon) {
		logger.Logger.getInstance().log(
				"[SimPatrol.PerceptionDaemon(" + daemon.AGENT.getObjectId()
						+ ")]: Stopped working.");
	}
}