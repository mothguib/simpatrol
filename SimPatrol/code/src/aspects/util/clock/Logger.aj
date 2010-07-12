package util.clock;

import util.time.Clock;

/**
 * Logs util.time.clock events
 */
public aspect Logger {

	/**
	 * Logs Clock.run
	 */
	pointcut runClock(Clock clock) : execution(* Clock.run(..)) && this(clock);

	before(Clock clock) : runClock(clock) {
		control.event.Logger.println("[SimPatrol.Clock(" + clock.getName()
				+ ")]: Started working.");
	}

	after(Clock clock) : runClock(clock) {
		control.event.Logger.println("[SimPatrol.Clock(" + clock.getName()
				+ ")]: Stopped working.");
	}
}
