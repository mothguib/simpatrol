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
import util.timer.TimedObject;

/**
 * Implements a coordinator that assures the correct elapsing of cycles when the
 * patrolling task is being simulated.
 * 
 * Used only by cycled simulators.
 * 
 * @see CycledSimulator
 * @modeller This class must have its behaviour modelled.
 */
public final class Coordinator extends Thread implements TimedObject {
	/* Attributes. */
	/** The number of cycles to be simulated. */
	private final int NUMBER_OF_CYCLES;

	/** The number of cycles simulated. */
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
		Object[] staminas_array = this.staminas.toArray();
		for (int i = 0; i < staminas_array.length; i++) {
			AgentAndSpentStaminas agent_staminas = (AgentAndSpentStaminas) staminas_array[i];

			if (agent_staminas.AGENT.equals(agent)) {
				// configures the stamina spent with actions
				agent_staminas.actions_spent_stamina = spent_stamina;

				// quits the method
				return;
			}
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
		if (this.staminas != null) {
			Object[] staminas_array = this.staminas.toArray();
			for (int i = 0; i < staminas_array.length; i++) {
				AgentAndSpentStaminas agent_staminas = (AgentAndSpentStaminas) staminas_array[i];

				if (agent_staminas.AGENT.equals(agent))
					return agent_staminas.actions_spent_stamina;
			}
		}

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
		Object[] staminas_array = this.staminas.toArray();
		for (int i = 0; i < staminas_array.length; i++) {
			AgentAndSpentStaminas agent_staminas = (AgentAndSpentStaminas) staminas_array[i];

			if (agent_staminas.AGENT.equals(agent)) {
				// configures the stamina spent with perceptions
				agent_staminas.perceptions_spent_stamina = spent_stamina;

				// quits the method
				return;
			}
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
		if (this.staminas != null) {
			Object[] staminas_array = this.staminas.toArray();
			for (int i = 0; i < staminas_array.length; i++) {
				AgentAndSpentStaminas current_trio = (AgentAndSpentStaminas) staminas_array[i];
				if (current_trio.AGENT.equals(agent)) {
					this.staminas.remove(current_trio);
					return;
				}
			}
		}
	}

	/** Assures the dynamic objects the correct behaviour. */
	private void assureDynamicity() {
		// obtains the dynamic objects to be controlled
		Dynamic[] dynamic_objects = simulator.getDynamicObjects();

		// for each dynamic object
		for (int i = 0; i < dynamic_objects.length; i++) {
			// obtains the current dynamic object
			Dynamic current_object = dynamic_objects[i];

			// if the dynamic object is enabled
			if (current_object.isEnabled()) {
				// atualizes the enabling tpd
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

				// atualizes the disabling tpd
				current_object.getDisablingTPD().nextBoolean();
			}
		}
	}

	/**
	 * Assures the mortal objects the correct behaviour.
	 * 
	 * @throws IOException
	 */
	private void assureMortality() throws IOException {
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

	/** Assures the stamined agents the correct behaviour. */
	private void assureStaminaControl() {
		// obtains the stamined agents to be controlled
		Agent[] agents = simulator.getStaminedObjects();

		// for each obtained agent
		for (int i = 0; i < agents.length; i++) {
			// holds the total amount of spent stamina
			double spent_stamina = 0;

			// tries to set the total amount of staminas
			if (this.staminas != null) {
				Object[] staminas_array = this.staminas.toArray();

				for (int j = 0; j < staminas_array.length; j++) {
					AgentAndSpentStaminas agents_staminas = (AgentAndSpentStaminas) staminas_array[j];

					if (agents_staminas.AGENT.equals(agents[i])) {
						spent_stamina = agents_staminas.actions_spent_stamina
								+ agents_staminas.perceptions_spent_stamina;
						break;
					}
				}
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
			action_daemons[i].act(1);
	}

	/** @modeller This method must be modelled. */
	public void run() {
		try {
			// for the number of cycles to be simulated...
			for (int i = 0; i < this.NUMBER_OF_CYCLES; i++) {
				// lets agents perceive, act and updates the environment
				this.makeAgentsPerceive();
				this.makeAgentsAct();
				this.updateEnvironmentModel();

				// increments the cycle count
				this.cycles_count++;
			}

			// stops the simulation
			simulator.stopSimulation();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Forces the agents to perceive the environment. */
	private void makeAgentsPerceive() {
		// while there are agents that didn't perceive yet, wait...
		while (!simulator.allAgentsJustPerceived())
			simulator.lockAgentsPerceptions(false);

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

	/**
	 * Updates the environment's model
	 * 
	 * @throws IOException
	 */
	private void updateEnvironmentModel() throws IOException {
		this.assureDynamicity();
		this.assureMortality();
		this.assureStaminaControl();
	}

	public int getElapsedTime() {
		return this.cycles_count;
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
