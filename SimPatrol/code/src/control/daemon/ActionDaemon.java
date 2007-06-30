/* ActionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's intentions of actions. */
public final class ActionDaemon extends AgentDaemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/* Methods. */
	/** Constructor.
	 * 
	 * 	Doesn't initiate its own connection, as it will be shared with a
	 *  PerceptionDaemon object. So the connection must be set by the
	 *  setConenction() method.
	 *  @see PerceptionDaemon
	 * 
	 *  @param agent The agent whose intentions are attended. */
	public ActionDaemon(Agent agent) {
		super(agent);
		this.stop_working = false;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// stops its connection
		this.connection.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.ActionDaemon(" + this.agent.getObjectId() + ")]: Stopped working.");		
	}
	
	public void run() {
		// screen message
		System.out.println("[SimPatrol.ActionDaemon(" + this.agent.getObjectId() + ")]: Listening to some intention...");
		
		while(!this.stop_working);
		// TODO implementar!!!
	}
}