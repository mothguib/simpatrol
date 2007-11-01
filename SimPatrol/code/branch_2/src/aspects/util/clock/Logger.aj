package util.clock;

/**
 * Logs util.clock events
 */
public aspect Logger {

	/**
	 * Logs Clock.run
	 */
	pointcut runClock(Clock clock) : execution(* Clock.run(..)) && this(clock);

	before(Clock clock) : runClock(clock) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Clock(" + clock.getName() + ")]: Started working.");
	}

	after(Clock clock) : runClock(clock) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Clock(" + clock.getName() + ")]: Stopped working.");
	}
}
