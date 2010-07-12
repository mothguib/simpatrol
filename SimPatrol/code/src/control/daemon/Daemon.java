/* Daemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import control.simulator.Simulator;
import util.data_structures.Queue;
import view.connection.Connection;

/**
 * Implements the daemons of SimPatrol.
 * 
 * @developer Subclasses of this one must use the attribute "is_active" when
 *            implementing their run() method.
 */
public abstract class Daemon  extends Thread implements IMessageObserver {
	/* Attributes. */
	/**
	 * Registers if the daemon is active.
	 * 
	 * @developer Subclasses must use this attribute when implementing their
	 *            run() method.
	 */
	protected boolean is_active;

	/** The connection used by the daemon to listen to and to send messages. */
	protected Connection connection;

	/** The buffer shared with the connection, for the exchange of messages. */
	protected final Queue<String> BUFFER;

	/**
	 * The simulator of the patrolling task performed by SimPatrol. Shared among
	 * all the daemons.
	 */
	protected static Simulator simulator;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 */
	public Daemon(String thread_name) {
		super(thread_name);
		this.is_active = true;
		this.BUFFER = new Queue<String>();
	}

	/**
	 * Sets the simulator of the daemon.
	 * 
	 * @param simpatrol_simulator
	 *            The simulator of the SimPatrol application.
	 */
	public static void setSimulator(Simulator simpatrol_simulator) {
		simulator = simpatrol_simulator;
	}

	/**
	 * Returns the number of the socket connection of the daemon.
	 * 
	 * @return The number of the socket connection.
	 */
	public int getConnectionSocketNumber() {
		return this.connection.getSocketNumber();
	}

	/**
	 * Starts the work of the daemon.
	 * 
	 * @param local_socket_number
	 *            The number of the local socket.
	 * @throws IOException
	 */
	public void start(int local_socket_number) throws IOException {
		if (!this.connection.isAlive()){			
			this.connection.start(local_socket_number);
		}
		this.connection.addObserver(this);
		if(! ((this instanceof MainDaemon)||(this instanceof  PerceptionDaemon)) )super.start();
	}

	/** Stops the work of the daemon. */
	public void stopActing() {
		this.is_active = false;

		// stops the connection of the daemon
		this.connection.stopActing();
	}

	/**
	 * Give preference to use this.start(int local_socket_number)
	 * 
	 * @deprecated
	 */
	public void start() {
		super.start();
	}
}