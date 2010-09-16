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
import util.time.Chronometer;
import util.time.Chronometerable;
import control.daemon.ActionDaemon;
import control.daemon.PerceptionDaemon;
import control.robot.DynamicityControllerRobot;
import control.robot.MortalityControllerRobot;
import control.robot.Robot;
import control.robot.StaminaControllerRobot;

/**
 * Implements a real time simulator of the patrolling task.
 * 
 * @modeler This class must have its behavior modeled.
 */
public final class RealTimeSimulator extends Simulator implements
		Chronometerable {
	/* Attributes. */
	/** The chronometer of the real time simulation. */
	private Chronometer chronometer;

	/**
	 * The robots that assure the dynamic objects the correct behavior. Its
	 * default value is NULL.
	 */
	private Set<DynamicityControllerRobot> dynamic_robots = null;

	/**
	 * The robots that assure the mortal objects the correct behavior. Its
	 * default value is NULL.
	 */
	private Set<MortalityControllerRobot> mortal_robots = null;

	/**
	 * The robots that assure the agents the correct stamina values. Its default
	 * value is NULL.
	 */
	private Set<StaminaControllerRobot> stamina_robots = null;

	/** Holds the time the simulation started. */
	private float start_time;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param local_socket_number
	 *            The number of the UDP socket of the main connection.
	 * @param update_time_rate
	 *            The time rate, in seconds, to update the internal model of the
	 *            simulation.
	 * @throws IOException
	 */
	public RealTimeSimulator(int local_socket_number, double update_time_rate)
			throws IOException {
		super(local_socket_number, update_time_rate);
		this.chronometer = null;
		Robot.setSimulator(this);
	}

	/**
	 * Obtains the dynamic objects and creates the respective dynamicity
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
				for (PerceptionDaemon perception_daemon : this.PERCEPTION_DAEMONS)
					if (perception_daemon.getAgent().equals(agents[i])) {
						perception_daemon.setStamina_robot(stamina_robot);
						break;
					}

				// sets the robot to the action daemon
				for (ActionDaemon action_daemon : this.ACTION_DAEMONS)
					if (action_daemon.getAgent().equals(agents[i])) {
						action_daemon.setStamina_robot(stamina_robot);
						break;
					}
			}
		} else {
			this.stamina_robots = null;
		}
	}

	/** Starts each one of the current dynamicity controller robots. */
	private void startDynamicityControllerRobots() {
		if (this.dynamic_robots != null)
			for (DynamicityControllerRobot robot : this.dynamic_robots)
				robot.start();
	}

	/** Starts each one of the current mortality controller robots. */
	private void startMortalityControllerRobots() {
		if (this.mortal_robots != null)
			for (MortalityControllerRobot robot : this.mortal_robots)
				robot.start();
	}

	/** Starts each one of the current stamina controller robots. */
	private void startStaminaControllerRobots() {
		if (this.stamina_robots != null)
			for (StaminaControllerRobot robot : this.stamina_robots)
				robot.start();
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
			if (agents[i].equals(agent)) {
				StaminaControllerRobot robot = new StaminaControllerRobot(agent
						.getObjectId()
						+ "'s stamina robot's clock", agent);

				// sets the robot to the perception daemon
				for (PerceptionDaemon daemon : this.PERCEPTION_DAEMONS)
					if (daemon.getAgent().equals(agent)) {
						daemon.setStamina_robot(robot);
						break;
					}

				// sets the robot to the action daemon
				for (ActionDaemon daemon : this.ACTION_DAEMONS)
					if (daemon.getAgent().equals(agent)) {
						daemon.setStamina_robot(robot);
						break;
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
			for (DynamicityControllerRobot robot : this.dynamic_robots)
				robot.stopActing();

			this.dynamic_robots.clear();
			this.dynamic_robots = null;
		}
	}

	/** Stops each one of the current mortality controller robots. */
	private void stopAndRemoveMortalityControllerRobots() {
		if (this.mortal_robots != null) {
			for (MortalityControllerRobot robot : this.mortal_robots)
				robot.stopActing();

			this.mortal_robots.clear();
			this.mortal_robots = null;
		}
	}

	/** Stops each one of the current stamina controller robots. */
	private void stopAndRemoveStaminaControllerRobots() {
		if (this.stamina_robots != null) {
			for (StaminaControllerRobot robot : this.stamina_robots)
				robot.stopActing();

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
		if (this.mortal_robots != null)
			for (MortalityControllerRobot robot : this.mortal_robots)
				if (robot.getObject().equals(object)) {
					robot.stopActing();
					this.mortal_robots.remove(robot);
					return;
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
		if (this.stamina_robots != null)
			for (StaminaControllerRobot robot : this.stamina_robots)
				if (robot.getAgent().equals(agent)) {
					robot.stopActing();
					this.stamina_robots.remove(robot);
					return;
				}
	}

	/** @modeler This method must be modeled. */
	public void startSimulation(double simulation_time) {
		// 0th. super code execution
		super.startSimulation(simulation_time);

		// 1st. creating things
		// creates the chronometer and sets it to the vertexes of the graph
		this.chronometer = new Chronometer("chronometer", this, simulation_time);
		Vertex.setTime_counter(this);

		// creates the dynamicity controller robots
		this.createDynamicityControllerRobots();

		// creates the mortality controller robots
		this.createMortalityControllerDaemons();

		// creates the stamina controller robots
		this.createStaminaControllerDaemons();

		// 2nd. starting things
		// updates the start time
		this.start_time = System.nanoTime();

		// starts the chronometer
		this.chronometer.start();

		// starts the dynamicity controller robots
		this.startDynamicityControllerRobots();

		// starts the mortality controller robots
		this.startMortalityControllerRobots();

		// starts the stamina controller robots
		this.startStaminaControllerRobots();
	}

	public void stopSimulation() throws IOException {
		// super code execution
		super.stopSimulation();

		// stops and removes the dynamicity controller robots
		this.stopAndRemoveDynamicityControllerRobots();

		// stops and removes the mortality controller robots
		this.stopAndRemoveMortalityControllerRobots();

		// stops and removes the stamina controller robots
		this.stopAndRemoveStaminaControllerRobots();
	}

	public void startActing() {
	}

	public void stopActing() {
		try {
			this.stopSimulation();
		} catch (IOException e) {
			e.printStackTrace(); // traced IO exception
		}
	}

	/** Returns the elapsed time of simulation, measured in seconds. */
	public double getElapsedTime() {
		return (System.nanoTime() - this.start_time) * Math.pow(10, -9);
	}
}