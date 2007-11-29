package util.timer;

/**
 * Logs util.timer
 */
public aspect Logger {

	/**
	 * Logs Chronometer.run()
	 */
	pointcut runChronometer(Chronometer chronometer) : 
		call(* Chronometerable.startWorking(..)) &&
		this(chronometer) &&
		withincode(* Chronometer.run(..));

	after(Chronometer chronometer) : 
		runChronometer(chronometer) {
		logger.Logger.println("[SimPatrol.Chronometer(" + chronometer.getName()
				+ ")]: Started counting time.");
	}

	pointcut runChronometer1(Chronometer chronometer) : 
		call(* Chronometerable.stopWorking(..)) &&
		this(chronometer) &&
		withincode(* Chronometer.run(..));

	after(Chronometer chronometer) : 
		runChronometer1(chronometer) {
		logger.Logger.println("[SimPatrol.Chronometer(" + chronometer.getName()
				+ ")]: Stopped counting time.");
	}
}
