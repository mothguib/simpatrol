/* CycledSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import model.agent.Agent;
import model.agent.AgentStates;
import model.agent.Society;
import model.graph.Node;
import control.coordinator.Coordinator;
import control.daemon.ActionDaemon;
import control.daemon.AgentDaemon;
import control.daemon.PerceptionDaemon;

/**
 * Implements a simulator of the patrolling task of which time counting is based
 * on the agents' perceive-think-act cycle.
 * 
 * @modeler This class must have its behavior modeled.
 */
public final class CycledSimulator extends Simulator {
	/* Attributes. */
	/**
	 * The coordinator of the simulation, that assures the correct elapsing of
	 * cycles when the simulator is working.
	 */
	private Coordinator coordinator;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param local_socket_number
	 *            The number of the TCP socket of the main connection.
	 * @param update_time_rate
	 *            The time rate, in seconds, to actualize the internal model of
	 *            the simulation.
	 * @throws IOException
	 */
	public CycledSimulator(int local_socket_number, double update_time_rate)
			throws IOException {
		super(local_socket_number, update_time_rate);
		this.coordinator = null;
	}

	/**
	 * Verifies if all agents just acted.
	 * 
	 * @return TRUE if all agent are in the JUST_ACTED state, FALSE if not.
	 */
	public boolean allAgentsJustActed() {
		// for each society
		Society[] societies = this.environment.getSocieties();
		for (int i = 0; i < societies.length; i++) {
			// for each agent
			Agent[] agents = societies[i].getAgents();
			for (int j = 0; j < agents.length; j++)
				if (agents[j].getAgentState() != AgentStates.JUST_ACTED)
					return false;
		}

		// default answer
		return true;
	}

	/**
	 * Verifies if all agents just perceived.
	 * 
	 * @return TRUE if all agent are in the JUST_PERCEIVED state, FALSE if not.
	 */
	public boolean allAgentsJustPerceived() {
		// for each society
		Society[] societies = this.environment.getSocieties();
		for (int i = 0; i < societies.length; i++) {
			// for each agent
			Agent[] agents = societies[i].getAgents();
			for (int j = 0; j < agents.length; j++)
				if (agents[j].getAgentState() != AgentStates.JUST_PERCEIVED)
					return false;
		}

		// default answer
		return true;
	}

	/**
	 * Locks all the agents' perceptions.
	 * 
	 * @param lock
	 *            FALSE if the agents can perceive, TRUE if cannot.
	 */
	public void lockAgentsPerceptions(boolean lock) {
		for (PerceptionDaemon daemon : this.PERCEPTION_DAEMONS)
			daemon.setIs_blocked(lock);
	}

	/**
	 * Locks or unlocks all the agents' actions.
	 * 
	 * @param lock
	 *            FALSE if the agents can act, TRUE if cannot.
	 */
	public void lockAgentsActions(boolean lock) {
		for (ActionDaemon daemon : this.ACTION_DAEMONS)
			daemon.setIs_blocked(lock);
	}

	/**
	 * Removes the eventual "agent - action spent stamina - perception spent
	 * stamina" trio memorized in the coordinator of the simulation.
	 * 
	 * Used when the given agent dies.
	 * 
	 * @param agent
	 *            The agent of which trio must be removed.
	 */
	public void removeAgentSpentStaminas(Agent agent) {
		this.coordinator.removeAgentSpentStaminas(agent);
	}

	/** @modeler This method must be modeled. */
	public void startSimulation(double simulation_time) {
		// super code execution
		super.startSimulation(simulation_time);

		// creates the coordinator, and sets it as the
		// time counter to the nodes
		this.coordinator = new Coordinator(this, (int) simulation_time);
		Node.setTime_counter(this);

		// sets the coordinator to the agent daemons
		AgentDaemon.setStamina_coordinator(this.coordinator);

		// starts the coordinator (and so, the simulation)
		this.coordinator.start();
	}

	public double getElapsedTime() {
		return coordinator.getCycles_count();
	}
}