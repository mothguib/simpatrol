/* Daemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import control.simulator.Simulator;
import util.Queue;

/** Implements the daemons of SimPatrol.
 * 
 *  @developer Subclasses of this one must use the attribute "stop_working" when implementing their run() method. */
public abstract class Daemon extends Thread {
	/* Attributes. */
	/** Registers that the daemon must stop working.
	 * 
	 *  @developer Subclasses must use the attribute "stop_working" when implementing their run() method. */
	protected boolean stop_working;
	
	/** The buffer shared by the daemon with the connection, for the
	 *  exchange of messages. */
	protected Queue<String> buffer;
	
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
	}
	
	/** Sets the simulator of the daemon.
	 * 
	 *  @param simpatrol_simulator The simulator of SimPatrol. */
	public static void setSimulator(Simulator simpatrol_simulator) throws IOException {
		simulator = simpatrol_simulator;
	}
	
	/** Starts the work of the daemon.
	 * 
	 *  @param local_socket_number The number of the local UDP socket. 
	 *  @throws IOException */
	public abstract void start(int local_socket_number) throws IOException;
	
	/** Stops the work of the daemon. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Give preference to use this.start(int local_socket_number)
	 * 
	 *  @deprecated */
	public void start() {
		super.start();
	}
}