/* PerceptionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's requisitions for perceptions. */
public final class PerceptionDaemon extends AgentDaemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/* Methods. */
	/** Constructor.
	 * 
	 * 	Doesn't initiate its own connection, as it will be shared with an
	 *  ActionDaemon object. So the connection must be set by the
	 *  setConenction() method.
	 *  @see ActionDaemon
	 *  
	 *  @param agent The agent whose intentions are attended. */
	public PerceptionDaemon(Agent agent) {
		super(agent);
		this.stop_working = false;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// stops its connection
		this.connection.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Stopped working.");
	}
	
	public void run() {
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Listening to some requisition...");
		
		while(!this.stop_working);
		// TODO implementar!!!
	}
}