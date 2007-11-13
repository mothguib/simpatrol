/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import model.agent.Agent;
import model.graph.Vertex;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import util.timer.Chronometer;
import util.timer.Chronometerable;
import control.daemon.ActionDaemon;
import control.daemon.PerceptionDaemon;
import control.robot.DynamicityControllerRobot;
import control.robot.MortalityControllerRobot;
import control.robot.Robot;
import control.robot.StaminaControllerRobot;

/**
 * Implements a real time simulator of the patrolling task.
 * 
 * @modeller This class must have its behaviour modelled.
 */
public final class RealTimeSimulator extends Simulator implements
		Chronometerable {
	/* Attributes. */
	/** The chronometer of the real time simulation. */
	private Chronometer chronometer;

	/**
	 * The robots that assure the dynamic objects the correct behaviour. Its
	 * default value is NULL.
	 */
	private Set<DynamicityControllerRobot> dynamic_robots = null;

	/**
	 * The robots that assure the mortal objects the correct behaviour. Its
	 * default value is NULL.
	 */
	private Set<MortalityControllerRobot> mortal_robots = null;

	/**
	 * The robots that assure the agents the correct stamina values. Its default
	 * value is NULL.
	 */
	private Set<StaminaControllerRobot> stamina_robots = null;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param local_socket_number
	 *            The number of the UDP socket of the main connection.
	 * @param actualization_time_rate
	 *            The time rate, in seconds, to actualize the internal model of
	 *            the simulation.
	 * @throws IOException
	 */
	public RealTimeSimulator(int local_socket_number,
			double actualization_time_rate) throws IOException {
		super(local_socket_number, actualization_time_rate);
		this.chronometer = null;
		Robot.setSimulator(this);
	}

	/**
	 * Obtains the dynamic objects and creates the respective dymamicity
	 * controller robots.
	 */
	private void createDynamicityControllerRobots() {
		// obtains the dynamic objects
		Dynamic[] dynamic_objects = this.getDynamicObjects();

		// if there are any dynamic objects
		if (dynamic_objects.length > 0) {
			// initiates the dynamic robots set
			this.dynamic_robots = new HashSet<DynamicityControllerRobot>();

			// for each one, creates a dynamicity controller robot
			for (int i = 0; i < dynamic_objects.length; i++)
				this.dynamic_robots.add(new DynamicityControllerRobot(
						dynamic_objects[i] + "'s dynamic robot's clock",
						dynamic_objects[i]));
		} else {
			this.dynamic_robots = null;
		}
	}

	/**
	 * Obtains the mortal objects and creates the respective mortality
	 * controller robots.
	 */
	private void createMortalityControllerDaemons() {
		// obtains the mortal objects
		Mortal[] mortal_objects = this.getMortalObjects();

		// if there are any mortal objects
		if (mortal_objects.length > 0) {
			// initiates the mortal robots set
			this.mortal_robots = Collections
					.synchronizedSet(new HashSet<MortalityControllerRobot>());

			// for each one, creates a mortality controller robot
			for (int i = 0; i < mortal_objects.length; i++)
				this.mortal_robots.add(new MortalityControllerRobot(
						mortal_objects[i] + "'s mortal robot's clock",
						mortal_objects[i]));
		} else {
			this.mortal_robots = null;
		}

		// configures the simulator to the robots
		MortalityControllerRobot.setSimulator(this);
	}

	/**
	 * Obtains the agents that must have their stamina controlled and creates
	 * their respective stamina controller robots.
	 */
	private void createStaminaControllerDaemons() {
		// obtains the agents to be controlled
		Agent[] agents = this.getStaminedObjects();

		// if there are any objects to be controlled
		if (agents.length > 0) {
			// initiates the stamina robots set
			this.stamina_robots = Collections
					.synchronizedSet(new HashSet<StaminaControllerRobot>());

			// for each one, creates a stamina controller robot
			for (int i = 0; i < agents.length; i++) {
				StaminaControllerRobot stamina_robot = new StaminaControllerRobot(
						agents[i].getObjectId() + "'s stamina robot's clock",
						agents[i]);
				this.stamina_robots.add(stamina_robot);

				// sets the robot to the perception daemon
				Object[] perception_daemons_array = this.PERCEPTION_DAEMONS
						.toArray();
				for (int j = 0; j < perception_daemons_array.length; j++) {
					PerceptionDaemon perception_daemon = (PerceptionDaemon) perception_daemons_array[j];

					if (perception_daemon.getAgent().equals(agents[i])) {
						perception_daemon.setStamina_robot(stamina_robot);
						break;
					}
				}

				// sets the robot to the action daemon
				Object[] action_daemons_array = this.ACTION_DAEMONS.toArray();
				for (int j = 0; j < action_daemons_array.length; j++) {
					ActionDaemon action_daemon = (ActionDaemon) action_daemons_array[j];

					if (action_daemon.getAgent().equals(agents[i])) {
						action_daemon.setStamina_robot(stamina_robot);
						break;
					}
				}
			}
		} else {
			this.stamina_robots = null;
		}
	}

	/** Starts each one of the current dynamicity controller robots. */
	private void startDynamicityControllerRobots() {
		if (this.dynamic_robots != null) {
			Object[] dynamic_robots_array = this.dynamic_robots.toArray();
			for (int i = 0; i < dynamic_robots_array.length; i++)
				((DynamicityControllerRobot) dynamic_robots_array[i]).start();
		}
	}

	/** Starts each one of the current mortality controller robots. */
	private void startMortalityControllerRobots() {
		if (this.mortal_robots != null) {
			Object[] mortal_robots_array = this.mortal_robots.toArray();
			for (int i = 0; i < mortal_robots_array.length; i++)
				((MortalityControllerRobot) mortal_robots_array[i]).start();
		}
	}

	/** Starts each one of the current stamina controller robots. */
	private void startStaminaControllerRobots() {
		if (this.stamina_robots != null) {
			Object[] stamina_robots_array = this.stamina_robots.toArray();
			for (int i = 0; i < stamina_robots_array.length; i++)
				((StaminaControllerRobot) stamina_robots_array[i]).start();
		}
	}

	/**
	 * Creates and starts a mortality controller robot, given its mortal object.
	 * 
	 * @param object
	 *            The mortal object to be controlled by the robot.
	 */
	public void createAndStartMortalityControlerRobot(Mortal object) {
		MortalityControllerRobot robot = new MortalityControllerRobot(object
				+ "'s mortal robot's clock", object);

		if (this.mortal_robots == null)
			this.mortal_robots = Collections
					.synchronizedSet(new HashSet<MortalityControllerRobot>());

		this.mortal_robots.add(robot);
		robot.start();
	}

	/**
	 * Creates and starts a stamina controller robot, given its agent.
	 * 
	 * @param agent
	 *            The agent to be controlled by the robot.
	 */
	public void createAndStartStaminaControlerRobot(Agent agent) {
		// obtains the agents to be controlled
		Agent[] agents = this.getStaminedObjects();

		// if one of these agents is the given one
		for (int i = 0; i < agents.length; i++)
			if (agents[i].equals(agents)) {
				StaminaControllerRobot robot = new StaminaControllerRobot(agent
						.getObjectId()
						+ "'s stamina robot's clock", agent);

				// sets the robot to the perception daemon
				Object[] perception_daemons_array = this.PERCEPTION_DAEMONS
						.toArray();
				for (int j = 0; j < perception_daemons_array.length; j++) {
					PerceptionDaemon perception_daemon = (PerceptionDaemon) perception_daemons_array[j];

					if (perception_daemon.getAgent().equals(agents[i])) {
						perception_daemon.setStamina_robot(robot);
						break;
					}
				}

				// sets the robot to the action daemon
				Object[] action_daemons_array = this.ACTION_DAEMONS.toArray();
				for (int j = 0; j < action_daemons_array.length; j++) {
					ActionDaemon action_daemon = (ActionDaemon) action_daemons_array[j];

					if (action_daemon.getAgent().equals(agents[i])) {
						action_daemon.setStamina_robot(robot);
						break;
					}
				}

				if (this.stamina_robots == null)
					this.stamina_robots = Collections
							.synchronizedSet(new HashSet<StaminaControllerRobot>());

				this.stamina_robots.add(robot);
				robot.start();

				return;
			}
	}

	/** Stops and removes each one of the current dynamicity controller robots. */
	private void stopAndRemoveDynamicityControllerRobots() {
		if (this.dynamic_robots != null) {
			Object[] dynamic_robots_array = this.dynamic_robots.toArray();
			for (int i = 0; i < dynamic_robots_array.length; i++)
				((DynamicityControllerRobot) dynamic_robots_array[i])
						.stopWorking();

			this.dynamic_robots.clear();
			this.dynamic_robots = null;
		}
	}

	/** Stops each one of the current mortality controller robots. */
	private void stopAndRemoveMortalityControllerRobots() {
		if (this.mortal_robots != null) {
			Object[] mortal_robots_array = this.mortal_robots.toArray();
			for (int i = 0; i < mortal_robots_array.length; i++)
				((MortalityControllerRobot) mortal_robots_array[i])
						.stopWorking();

			this.mortal_robots.clear();
			this.mortal_robots = null;
		}
	}

	/** Stops each one of the current stamina controller robots. */
	private void stopAndRemoveStaminaControllerRobots() {
		if (this.stamina_robots != null) {
			Object[] stamina_robots_array = this.stamina_robots.toArray();
			for (int i = 0; i < stamina_robots_array.length; i++)
				((StaminaControllerRobot) stamina_robots_array[i])
						.stopWorking();

			this.stamina_robots.clear();
			this.stamina_robots = null;
		}
	}

	/**
	 * Removes a given mortality controller robot from the set of mortality
	 * controller robots.
	 * 
	 * @param mortal_robot
	 *            The mortality controller robot to be removed.
	 */
	public void removeMortalityControllerRobot(
			MortalityControllerRobot mortal_robot) {
		this.mortal_robots.remove(mortal_robot);
	}

	/**
	 * Removes and stops the mortality controller robot that controls the given
	 * object.
	 * 
	 * @param object
	 *            The object controlled by the robot to be removed.
	 */
	public void stopAndRemoveMortalityControllerRobot(Mortal object) {
		// finds the mortality robot of the given object
		if (this.mortal_robots != null) {
			Object[] mortal_robots_array = this.mortal_robots.toArray();
			for (int i = 0; i < mortal_robots_array.length; i++) {
				MortalityControllerRobot robot = (MortalityControllerRobot) mortal_robots_array[i];

				if (robot.getObject().equals(object)) {
					robot.stopWorking();
					this.mortal_robots.remove(robot);
					return;
				}
			}
		}
	}

	/**
	 * Removes and stops the stamina controller robot that controls the given
	 * agent.
	 * 
	 * @param agent
	 *            The agent controlled by the robot to be removed.
	 */
	public void stopAndRemoveStaminaControllerRobot(Agent agent) {
		// finds the stamina robot of the given agent
		if (this.stamina_robots != null) {
			Object[] stamina_robots_array = this.stamina_robots.toArray();
			for (int i = 0; i < stamina_robots_array.length; i++) {
				StaminaControllerRobot robot = (StaminaControllerRobot) stamina_robots_array[i];

				if (robot.getAgent().equals(agent)) {
					robot.stopWorking();
					this.stamina_robots.remove(robot);
					return;
				}
			}
		}
	}

	/** @modeller This method must be modelled. */
	public void startSimulation(int simulation_time) {
		// 0th. super code execution
		super.startSimulation(simulation_time);

		// 1st. creating things
		// creates the chronometer and sets it to the vertexes of the graph
		this.chronometer = new Chronometer("chronometer", this, simulation_time);
		Vertex.setTime_counter(this.chronometer);

		// creates the dynamicity controller robots
		this.createDynamicityControllerRobots();

		// creates the mortality controller robots
		this.createMortalityControllerDaemons();

		// creates the stamina controller robots
		this.createStaminaControllerDaemons();

		// 2nd. starting things
		// starts the chronometer
		this.chronometer.start();

		// starts the dynamicity controller robots
		this.startDynamicityControllerRobots();

		// starts the mortality controller robots
		this.startMortalityControllerRobots();

		// starts the stamina controller robots
		this.startStaminaControllerRobots();
	}

	public void stopSimulation() throws IOException, InterruptedException {
		// super code execution
		super.stopSimulation();

		// stops and removes the dynamicity controller robots
		this.stopAndRemoveDynamicityControllerRobots();

		// stops and removes the mortality controller robots
		this.stopAndRemoveMortalityControllerRobots();

		// stops and removes the stamina controller robots
		this.stopAndRemoveStaminaControllerRobots();
	}

	public void startWorking() {
	}

	public void stopWorking() {
		// stops the simulator
		try {
			this.stopSimulation();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getElapsedTime() {
		return this.chronometer.getElapsedTime();
	}
}