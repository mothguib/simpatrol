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
		control.event.Logger
				.println("[SimPatrol.Coordinator]: Simulation started.");
	}

	after(): run1() {
		control.event.Logger
				.println("[SimPatrol.Coordinator]: Simulation stopped.");
	}

	/**
	 * Another Pointcut in Coordinator.run()
	 */
	pointcut run2() : execution(* Coordinator.makeAgentsPerceive(..));

	before(): run2(){
		control.event.Logger
				.println("[SimPatrol.Coordinator]: Agents are perceiving.");
	}

	after(): run2(){
		control.event.Logger
				.println("[SimPatrol.Coordinator]: Agents are thinking.");
	}

	/**
	 * Another Pointcut in Coordinator.run()
	 */
	pointcut run3() : execution(* Coordinator.makeAgentsAct(..));

	before(): run3() {
		control.event.Logger
				.println("[SimPatrol.Coordinator]: Agents are acting.");
	}

	/**
	 * Another Pointcut in Coordinator.run()
	 */
	pointcut run4() : execution(* Coordinator.updateEnvironmentModel(..));

	before(): run4() {
		control.event.Logger
				.println("[SimPatrol.Coordinator]: The environment is being updated.");
	}
}
