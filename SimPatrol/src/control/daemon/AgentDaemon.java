/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import control.coordinator.Coordinator;
import control.robot.StaminaControllerRobot;
import control.simulator.CycledSimulator;
import control.simulator.RealTimeSimulator;
import util.time.Clock;
import util.time.Clockable;
import view.connection.Connection;
import model.agent.Agent;

/**
 * Implements the daemons of SimPatrol that attend an agent's feelings of
 * perceptions, or intentions of actions.
 */
public abstract class AgentDaemon extends Daemon implements Clockable {
	/* Attributes. */
	/**
	 * Registers if the daemon can work at the current moment. Used to
	 * synchronize the action and perception daemons when the simulation is a
	 * cycled one.
	 */
	protected boolean is_blocked;

	/**
	 * The agent whose perceptions are produced and whose intentions are
	 * attended.
	 */
	protected final Agent AGENT;

	/** The clock that controls the daemon's work. */
	protected Clock clock;

	/**
	 * The robot that assures the correct spending of stamina to the agent of
	 * this daemon.
	 * 
	 * Used only when the simulator is a real time one.
	 * 
	 * @see RealTimeSimulator
	 */
	protected StaminaControllerRobot stamina_robot;

	/**
	 * The coordinator that assures the correct spending of stamina to the agent
	 * of this daemon, as well as the correct counting of time.
	 * 
	 * Used only when the simulator is a cycled one.
	 * 
	 * @see Coordinator
	 */
	protected static Coordinator coordinator;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * Doesn't initiate its own connection, as its subclasses (PerceptionDaemon
	 * and ActionDaemon) will share one. So the connection must be set by the
	 * setConnection() method.
	 * 
	 * @see PerceptionDaemon
	 * @see ActionDaemon
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 * @param agent
	 *            The agent whose perceptions are produced and intentions are
	 *            attended.
	 */
	public AgentDaemon(String thread_name, Agent agent) {
		super(thread_name);
		this.AGENT = agent;
		this.clock = new Clock(thread_name + "'s clock", this);
		this.is_blocked = true; // the daemon cannot work in the beginning
		this.stamina_robot = null;

		this.connection = null;
	}

	/**
	 * Sets if this daemon can or cannot work.
	 * 
	 * @param permission
	 *            TRUE if the daemon is blocked, FALSE if not.
	 */
	public void setIs_blocked(boolean is_blocked) {
		this.is_blocked = is_blocked;
	}

	/**
	 * Returns the agent of the daemon
	 * 
	 * @return The agent served by the daemon.
	 */
	public Agent getAgent() {
		return this.AGENT;
	}

	/**
	 * Configures the connection of the daemon.
	 * 
	 * @param connection
	 *            The connection of the daemon.
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Configures the stamina robot of this daemon.
	 * 
	 * Used only when the simulator is a real time one.
	 * 
	 * @param stamina_robot
	 *            The robot that assures the correct spending of stamina to the
	 *            agent of this daemon.
	 * @see RealTimeSimulator
	 */
	public void setStamina_robot(StaminaControllerRobot stamina_robot) {
		this.stamina_robot = stamina_robot;
		coordinator = null;
	}

	/**
	 * Configures the stamina coordinator of this daemon.
	 * 
	 * Used only when the simulator is a cycled one.
	 * 
	 * @see CycledSimulator
	 */
	public static void setStamina_coordinator(Coordinator passed_coordinator) {
		coordinator = passed_coordinator;
	}

	public void start(int local_socket_number) throws IOException {
		super.start(local_socket_number);

		if (this.clock != null)
			this.clock.start();
	}

	public void stopActing() {
		super.stopActing();

		if (this.clock != null)
			this.clock.stopActing();
	}
}