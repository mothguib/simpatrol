package control.simulator;

import java.util.Calendar;

import control.daemon.MainDaemon;

/**
 * This aspect is responsible for logging data of all simulators classes
 */
public aspect Logger {

	/**
	 * Pointcut that refers to the beginning of a real time simulation
	 */
	pointcut startWorkingRealTimeSimulator() :  execution (* RealTimeSimulator.startActing());

	before(): startWorkingRealTimeSimulator() {
		control.event.Logger
				.println("[SimPatrol.Simulator]: Simulation started at "
						+ Calendar.getInstance().getTime().toString() + ".");
	}

	/**
	 * Pointcut that refers to the end of a real time simulation
	 */
	pointcut stopWorkingRealTimeSimulator() : execution (* RealTimeSimulator.stopActing());

	before(): stopWorkingRealTimeSimulator() {
		control.event.Logger
				.println("[SimPatrol.Simulator]: Simulation stopped at "
						+ Calendar.getInstance().getTime().toString() + ".");
	}

	/**
	 * Logs data about the <code>Simulator</code> construction.
	 */
	pointcut simulatorConstructor() : execution(Simulator.new(..));

	before(): simulatorConstructor(){
		control.event.Logger.println("[SimPatrol.Simulator]: Online.");
	}

	/**
	 * The pointcut actually crosscuts the method <code>stopWorking()</code>
	 * of class <code>MainDaemon</code> because it is used in Simulator.exit()
	 */
	pointcut exitSimulator() : execution(* MainDaemon.stopActing());

	after(): exitSimulator() {
		control.event.Logger.println("[SimPatrol.Simulator]: offline.");
		control.event.Logger
				.println("[SimPatrol.MainDaemon]: Stopped working.");
	}
}
