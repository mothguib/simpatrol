package control.coordinator;

/**
 * This aspect is responsible for logging data of all coordinator classes.
 */
public aspect Logger {

	/**
	 * Pointcut in Coordinator.run()
	 */
	pointcut run1() : execution(* Coordinator.run(..));

	before(): run1() {
		logger.Logger.getInstance().log(
				"[SimPatrol.Coordinator]: Simulation started.");
	}

	after(): run1() {
		logger.Logger.getInstance().log(
				"[SimPatrol.Coordinator]: Simulation stopped.");
	}

	/**
	 * Another Pointcut in Coordinator.run()
	 */
	pointcut run2() : execution(* Coordinator.makeAgentsPerceive(..));

	before(): run2(){
		logger.Logger.getInstance().log(
				"[SimPatrol.Coordinator]: Agents are perceiving.");
	}

	after(): run2(){
		logger.Logger.getInstance().log(
				"[SimPatrol.Coordinator]: Agents are thinking.");
	}

	/**
	 * Another Pointcut in Coordinator.run()
	 */
	pointcut run3() : execution(* Coordinator.makeAgentsAct(..));

	before(): run3() {
		logger.Logger.getInstance().log(
				"[SimPatrol.Coordinator]: Agents are acting.");
	}

	/**
	 * Another Pointcut in Coordinator.run()
	 */
	pointcut run4() : execution(* Coordinator.updateEnvironmentModel(..));

	before(): run4() {
		logger.Logger.getInstance().log(
				"[SimPatrol.Coordinator]: The environment is being atualized.");
	}
}
