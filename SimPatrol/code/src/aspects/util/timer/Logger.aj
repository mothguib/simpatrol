package util.timer;

import util.time.Chronometer;
import util.time.Chronometerable;

/**
 * Logs util.time
 */
public aspect Logger {

	/**
	 * Logs Chronometer.run()
	 */
	pointcut runChronometer(Chronometer chronometer) : 
		call(* Chronometerable.startActing(..)) &&
		this(chronometer) &&
		withincode(* Chronometer.run(..));

	after(Chronometer chronometer) : 
		runChronometer(chronometer) {
		control.event.Logger.println("[SimPatrol.Chronometer("
				+ chronometer.getName() + ")]: Started counting time.");
	}

	pointcut runChronometer1(Chronometer chronometer) : 
		call(* Chronometerable.stopActing(..)) &&
		this(chronometer) &&
		withincode(* Chronometer.run(..));

	after(Chronometer chronometer) : 
		runChronometer1(chronometer) {
		control.event.Logger.println("[SimPatrol.Chronometer("
				+ chronometer.getName() + ")]: Stopped counting time.");
	}
}
