package logging;

import control.daemon.ActionDaemon;

/**
 * Logs data about the class ActionDaemon.
 */
public aspect ActionDaemonLogger {

	/**
	 * pointcuts ActionDaemon.stopWorking
	 */
	pointcut stopWorking(ActionDaemon daemon) : 
		call (* ActionDaemon.stopWorking(..))
		&& target(daemon);

	/**
	 * pointcuts ActionDaemon.run
	 */
	pointcut run(ActionDaemon daemon) :
		execution (* ActionDaemon.run(..))
		&& target(daemon);

	/**
	 * Register the daemon stop.
	 */
	after(ActionDaemon daemon) : stopWorking(daemon) {
		// screen message
		System.out.println("[SimPatrol.ActionDaemon("
				+ daemon.getAgent().getObjectId() + ")]: Stopped working.");
	}

	/**
	 * Logs the beginning of the daemon execution.
	 */
	before(ActionDaemon daemon) : run(daemon) {
		// screen message
		System.out.println("[SimPatrol.ActionDaemon("
				+ daemon.getAgent().getObjectId()
				+ ")]: Listening to an intention...");
	}

}
