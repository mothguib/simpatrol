package logging;

import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.SeasonalAgent;
import model.agent.Society;
import control.configuration.AgentCreationConfiguration;
import control.configuration.GraphCreationConfiguration;
import control.configuration.MetricCreationConfiguration;
import control.daemon.MainDaemon;
import control.simulator.Simulator;
import control.simulator.SimulatorStates;

/**
 * This aspect is responsible for logging some data about the execution of the
 * class MainDaemon.
 */
privileged aspect MainDaemonLogger {

	/**
	 * pointcuts MainDaemon.attendGraphCreationConfiguration
	 */
	pointcut attendGraphCreationConfiguration(
			GraphCreationConfiguration configuration) : 
				call(* MainDaemon.attendGraphCreationConfiguration(GraphCreationConfiguration)) 
				&& args(configuration);

	/**
	 * pointcuts MainDaemon.attendSocietiesCreationConfiguratio
	 */
	pointcut attendSocietiesCreationConfiguration() : 
					call(* MainDaemon.attendSocietiesCreationConfiguration(..));

	/**
	 * pointcuts MainDaemon.attendAgentCreationConfiguration
	 */
	pointcut attendAgentCreationConfiguration(
			AgentCreationConfiguration configuration) :
		call(* MainDaemon.attendAgentCreationConfiguration(..))
		&& args(configuration);

	/**
	 * pointcuts Simulator.startSimulation
	 */
	pointcut startSimulation(int simulation_time) : 
		call(* Simulator.startSimulation(..))
		&& args(simulation_time);

	/**
	 * pointcuts MainDaemon.attendMetricCreationConfiguration
	 */
	pointcut attendMetricCreationConfiguration(
			MetricCreationConfiguration configuration) : 
				call(* MainDaemon.attendMetricCreationConfiguration(..)) 
				&& args(configuration);

	/**
	 * pointcuts MainDaemon.stopWorking
	 */
	pointcut stopWorking() : call(* MainDaemon.stopWorking(..));

	/**
	 * pointcuts MainDaemon.processGraphCreationConfiguration
	 */
	pointcut processGraphCreationConfiguration() : execution(* MainDaemon.processGraphCreationConfiguration(..));

	/**
	 * pointcuts MainDaemon.processSimulationStartConfiguration
	 */
	pointcut processSimulationStartConfiguration() : execution(* MainDaemon.processSimulationStartConfiguration(..));

	/**
	 * Logs the obtained graph.
	 */
	after(GraphCreationConfiguration configuration) : attendGraphCreationConfiguration(configuration) {
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Graph obtained:");
		System.out.print(configuration.getGraph().fullToXML(0));
	}

	/**
	 * Logs the obtained societies.
	 */
	after() : attendSocietiesCreationConfiguration() {
		Society[] societies = MainDaemon.simulator.getSocieties();
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Societies obtained:");
		for (int i = 0; i < societies.length; i++)
			System.out.print(societies[i].fullToXML(0));
	}

	/**
	 * Logs the obtained agents.
	 */
	after(AgentCreationConfiguration configuration) : attendAgentCreationConfiguration(configuration) {
		Agent agent = configuration.getAgent();
		Society[] simulator_societies = MainDaemon.simulator.getSocieties();
		String society_id = configuration.getSociety_id();

		Society society = null;
		for (int i = 0; i < simulator_societies.length; i++) {
			if (simulator_societies[i].getObjectId().equals(society_id)) {
				society = simulator_societies[i];
				break;
			}
		}

		if (society != null) {
			if (MainDaemon.simulator.getState() == SimulatorStates.CONFIGURING) {
				if (society instanceof ClosedSociety) {
					agent = ((SeasonalAgent) agent).toPerpetualVersion();
				}
				System.out.println("[SimPatrol.MainDaemon] Agent obtained:");
				System.out.print(agent.fullToXML(0));
			} else if (society instanceof OpenSociety) {
				System.out.println("[SimPatrol.MainDaemon] Agent obtained:");
				System.out.print(agent.fullToXML(0));
			}
		}
	}

	/**
	 * Logs the simulation start.
	 */
	after(int simulation_time) : startSimulation(simulation_time) {
		System.out.println("[SimPatrol.MainDaemon] Simulation started:");
		System.out.println("Planned simulation time: " + simulation_time);
	}

	/**
	 * Logs the beginning of the execution of an agent.
	 */
	after(MetricCreationConfiguration configuration) : attendMetricCreationConfiguration(configuration) {
		System.out.println("[SimPatrol.MetricDaemon("
				+ configuration.getMetric().getType() + ")] Working.");
	}

	/**
	 * Register the daemon stop.
	 */
	after() : stopWorking() {
		System.out.println("[SimPatrol.MainDaemon] Stopped working.");
	}

	/**
	 * Logs the beginning of a listening for a graph configuration.
	 */
	before() : processGraphCreationConfiguration() {
		System.out
				.println("[SimPatrol.MainDaemon] Listening to a graph creation configuration...");
	}

	/**
	 * Logs the beginning of a listening for a generic configuration.
	 */
	before() : processSimulationStartConfiguration() {
		System.out
				.println("[SimPatrol.MainDaemon] Listening to a configuration...");
	}

}
