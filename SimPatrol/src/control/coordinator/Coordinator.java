/* Coordinator.java */

/* The package of this class. */
package control.coordinator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import model.agent.Agent;
import model.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import control.daemon.ActionDaemon;
import control.simulator.CycledSimulator;

/**
 * Implements a coordinator that assures the correct elapsing of cycles when the
 * patrolling task is being simulated.
 * 
 * Used only by cycled simulators.
 * 
 * @see CycledSimulator
 * @modeler This class must have its behavior modeled.
 */
public final class Coordinator extends Thread {
	/* Attributes. */
	/** The number of cycles to be simulated. */
	private final int NUMBER_OF_CYCLES;

	/** The number of simulated cycles. */
	private int cycles_count;

	/** The simulator of the patrolling task. */
	private static CycledSimulator simulator;

	/**
	 * The set of trios formed by the agents and their respective spent staminas
	 * due to their actions and perceptions.
	 */
	private Set<AgentAndSpentStaminas> staminas;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param cycled_simulator
	 *            The simulator of the patrolling task.
	 */
	public Coordinator(CycledSimulator cycled_simulator, int number_of_cycles) {
		simulator = cycled_simulator;
		this.NUMBER_OF_CYCLES = number_of_cycles;
		this.cycles_count = 0;
		this.staminas = null;
	}

	/**
	 * Returns the number of simulated cycles.
	 * 
	 * @return The number of simulated cycles.
	 */
	public int getCycles_count() {
		return this.cycles_count;
	}

	/**
	 * Configures the given amount of stamina to be spent due to the actions of
	 * the given agent.
	 * 
	 * @param agent
	 *            The agent whose stamina is being configured.
	 * @param spent_stamina
	 *            The amount of stamina to be spent due to the actions of the
	 *            given agent.
	 */
	public void setActionsSpentStamina(Agent agent, double spent_stamina) {
		if (this.staminas == null)
			this.staminas = Collections
					.synchronizedSet(new HashSet<AgentAndSpentStaminas>());

		// tries to find the trio that has the given agent
		// and to configure it
		for (AgentAndSpentStaminas agent_staminas : this.staminas)
			if (agent_staminas.AGENT.equals(agent)) {
				// configures the stamina spent with actions
				agent_staminas.actions_spent_stamina = spent_stamina;

				// quits the method
				return;
			}

		// if the method reached here, no trio was found...
		// so, create one and add it to the set of staminas
		AgentAndSpentStaminas agent_staminas = new AgentAndSpentStaminas(agent);
		agent_staminas.actions_spent_stamina = spent_stamina;
		this.staminas.add(agent_staminas);
	}

	/**
	 * Returns the amount of stamina to be spent due to the actions of the given
	 * agent.
	 * 
	 * @param agent
	 *            The agent whose stamina is wanted.
	 * @return The amount of stamina to be spent due to the actions of the given
	 *         agent.
	 */
	public double getActionsSpentStamina(Agent agent) {
		if (this.staminas != null)
			for (AgentAndSpentStaminas agent_staminas : this.staminas)
				if (agent_staminas.AGENT.equals(agent))
					return agent_staminas.actions_spent_stamina;

		return 0;
	}

	/**
	 * Configures the given amount of stamina to be spent due to the perceptions
	 * of the given agent.
	 * 
	 * @param agent
	 *            The agent whose stamina is being configured.
	 * @param spent_stamina
	 *            The amount of stamina to be spent due to the perceptions of
	 *            the given agent.
	 */
	public void setPerceptionsSpentStamina(Agent agent, double spent_stamina) {
		if (this.staminas == null)
			this.staminas = Collections
					.synchronizedSet(new HashSet<AgentAndSpentStaminas>());

		// tries to find the trio that has the given agent
		// and to configure it
		for (AgentAndSpentStaminas agent_staminas : this.staminas)
			if (agent_staminas.AGENT.equals(agent)) {
				// configures the stamina spent with perceptions
				agent_staminas.perceptions_spent_stamina = spent_stamina;

				// quits the method
				return;
			}

		// if the method reached here, no trio was found...
		// so, create one and add it to the set of staminas
		AgentAndSpentStaminas agent_staminas = new AgentAndSpentStaminas(agent);
		agent_staminas.perceptions_spent_stamina = spent_stamina;
		this.staminas.add(agent_staminas);
	}

	/**
	 * Removes the eventual "agent - action spent stamina - perception spent
	 * stamina" trio related to the given agent from the set of such kind of
	 * trios.
	 * 
	 * Used when the agent dies.
	 * 
	 * @param agent
	 *            The agent of which trio must be removed.
	 */
	public void removeAgentSpentStaminas(Agent agent) {
		if (this.staminas != null)
			for (AgentAndSpentStaminas agent_staminas : this.staminas)
				if (agent_staminas.AGENT.equals(agent)) {
					this.staminas.remove(agent_staminas);
					return;
				}
	}

	/** Assures the dynamic objects the correct behavior. */
	private void assureDynamicity() {
		// obtains the dynamic objects to be controlled
		Dynamic[] dynamic_objects = simulator.getDynamicObjects();

		// for each dynamic object
		for (int i = 0; i < dynamic_objects.length; i++) {
			// obtains the current dynamic object
			Dynamic current_object = dynamic_objects[i];

			// if the dynamic object is enabled
			if (current_object.isEnabled()) {
				// updates the enabling tpd
				current_object.getEnablingTPD().nextBoolean();

				// verifies if the object must be disabled now
				if (current_object.getDisablingTPD().nextBoolean())
					current_object.setIsEnabled(false);
			}
			// else
			else {
				// verifies if the object must be enabled now
				if (current_object.getEnablingTPD().nextBoolean())
					current_object.setIsEnabled(true);

				// updates the disabling tpd
				current_object.getDisablingTPD().nextBoolean();
			}
		}
	}

	/** Assures the mortal objects the correct behavior. */
	private void assureMortality() {
		// obtains the mortal objects to be controlled
		Mortal[] mortal_objects = simulator.getMortalObjects();

		// for each mortal object
		for (int i = 0; i < mortal_objects.length; i++) {
			// obtains the current mortal object
			Mortal current_object = mortal_objects[i];

			// obtains the probability distribution for the death of the mortal
			// object
			EventTimeProbabilityDistribution death_tpd = current_object
					.getDeathTPD();

			// if there's a death tpd and the object must die now
			if (death_tpd != null && death_tpd.nextBoolean()) {
				// kills the object
				current_object.die();

				// if the object is an agent
				if (current_object instanceof Agent) {
					// obtains it as an agent
					Agent agent = (Agent) current_object;

					// stops its agent_daemons
					simulator.stopAndRemoveAgentDaemons(agent);

					// removes its eventual trio "agent - action spent stamina -
					// perception spent stamina"
					this.removeAgentSpentStaminas(agent);
				}
			}
		}
	}

	/** Assures the stamined agents the correct behavior. */
	private void assureStaminaControl() {
		// obtains the stamined agents to be controlled
		Agent[] agents = simulator.getStaminedObjects();

		// for each obtained agent
		for (int i = 0; i < agents.length; i++) {
			// holds the total amount of spent stamina
			double spent_stamina = 0;

			// tries to set the total amount of staminas
			if (this.staminas != null)
				for (AgentAndSpentStaminas agent_staminas : this.staminas)
					if (agent_staminas.AGENT.equals(agents[i])) {
						spent_stamina = agent_staminas.actions_spent_stamina
								+ agent_staminas.perceptions_spent_stamina;
						break;
					}

			// if it's bigger than 0, decrements it from the current agent
			if (spent_stamina > 0)
				agents[i].decStamina(spent_stamina);
		}
	}

	/** Forces the agents to act as they eventually planned. */
	private void forceAgentsActAsPlanned() {
		// obtains the action daemons of the simulator
		ActionDaemon[] action_daemons = simulator.getActionDaemons();

		// for each action daemon, forces the act method
		for (int i = 0; i < action_daemons.length; i++)
			action_daemons[i].act();
	}

	/** @modeler This method must be modeled. */
	public void run() {
		// for the number of cycles to be simulated...
		for (int i = 0; i < this.NUMBER_OF_CYCLES; i++) {
			// lets agents perceive, act and updates the environment
			this.makeAgentsPerceive();
			this.makeAgentsAct();
			this.updateEnvironmentModel();

			// increments the cycle count
			this.cycles_count++;
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			System.err.println("Cycle " + i);
		}

		// stops the simulation
		try {
			simulator.stopSimulation();
		} catch (IOException e) {
			e.printStackTrace(); // traced IO exception
		}
	}

	/** Forces the agents to perceive the environment. */
	private void makeAgentsPerceive() {
		// while there are agents that didn't perceive yet, wait...
		while (!simulator.allAgentsJustPerceived()){
			simulator.lockAgentsPerceptions(false);
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
			

		// all agents perceived, so don't let them perceive anymore
		simulator.lockAgentsPerceptions(true);

		// agents shall be thinking now...
	}

	/** Forces the agents to act. */
	private void makeAgentsAct() {
		// forces the agents that have plans to act according to it
		this.forceAgentsActAsPlanned();

		// while there are agents that didn't act yet, wait...
		while (!simulator.allAgentsJustActed())
			simulator.lockAgentsActions(false);

		// all agents acted, so don't let them act anymore
		simulator.lockAgentsActions(true);
	}

	/** Updates the environment's model. */
	private void updateEnvironmentModel() {
		this.assureDynamicity();
		this.assureMortality();
		this.assureStaminaControl();
	}
}

/**
 * Internal class that holds together an agent and the stamina values to be
 * spent due to the perceptions and to the actions of such agent, respectively.
 */
final class AgentAndSpentStaminas {
	/** The agent whose stamina is being controlled. */
	public final Agent AGENT;

	/** The stamina spent due to the actions of the agent. */
	public double actions_spent_stamina;

	/** The stamina spent due to the perceptions of the agent. */
	public double perceptions_spent_stamina;

	/**
	 * Constructor.
	 * 
	 * @param agent
	 *            The agent whose stamina is being controlled.
	 */
	public AgentAndSpentStaminas(Agent agent) {
		this.AGENT = agent;
		this.actions_spent_stamina = 0;
		this.perceptions_spent_stamina = 0;
	}
}
