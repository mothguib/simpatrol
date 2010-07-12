/* Daemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;

import control.simulator.Simulator;
import util.Queue;
import view.connection.Connection;

/** Implements the daemons of SimPatrol.
 * 
 *  @developer Subclasses of this must use the attribute "stop_working" when implementing their run() method. */
public abstract class Daemon extends Thread {
	/* Attributes. */
	/** Registers that the daemon must stop working.
	 * 
	 *  @developer Subclasses of this must use the attribute "stop_working" when implementing their run() method. */
	protected boolean stop_working;
	
	/** The buffer shared by the daemon with the connection, for the
	 *  exchange of messages. */
	protected Queue<String> buffer;
	
	/** The connection used by the daemon to listen to
	 *  new messages, as well as attend them. */
	protected Connection connection;
	
	/** The simulator of the patrolling task, performed by SimPatrol.
	 *  Shared by all the daemons. */
	protected static Simulator simulator;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param thread_name The name of the thread of the daemon. */
	public Daemon(String thread_name) {
		super(thread_name);
		this.stop_working = false;
		this.buffer = new Queue<String>();
		this.connection = new Connection(thread_name + "'s connection", this.buffer);
	}
	
	/** Sets the simulator of the daemon.
	 * 
	 *  @param simpatrol_simulator The simulator of SimPatrol. */
	public static void setSimulator(Simulator simpatrol_simulator) {
		simulator = simpatrol_simulator;
	}
	
	/** Starts the work of the daemon.
	 * 
	 *  @param local_socket_number The number of the local UDP socket. 
	 *  @throws SocketException */
	public void start(int local_socket_number) throws SocketException {
		if(!this.connection.isAlive()) this.connection.start(local_socket_number);
		super.start();
	}
	
	/** Stops the work of the daemon. */
	public void stopWorking() {
		this.stop_working = true;
		this.connection.stopWorking();
	}
	
	/** Returns the number of the UDP socket connection.
	 * 
	 *  @return The number of the UDP socket connection. */
	public int getUDPSocketNumber() {
		return this.connection.getUDPSocketNumber();
	}
	
	/** Give preference to use this.start(int local_socket_number)
	 * 
	 *  @deprecated */
	public void start() {
		super.start();
	}
}