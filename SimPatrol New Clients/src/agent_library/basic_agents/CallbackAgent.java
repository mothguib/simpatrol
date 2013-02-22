package agent_library.basic_agents;

import agent_library.connections.ClientConnection;


/**
 * Extend this class to create agents that works by receiving notifications of updates by 
 * means of callback methods. Subclasses should override the callbacks for specific kinds of 
 * perceptions and/or the generic callback onPerception(), called for any new perception.  
 * <br><br>
 * The operation is as follows: for each new perception, the specific callback of the perception 
 * is called. Then, after all specific notifications, the generic onPerception() is called.
 * <br><br>  
 * If a new perception is (identified and) read inside any callback method, the specific 
 * callback of that perception won't be fired. Example: if in onGraphPerception(), the agent 
 * reads new broadcasts, it won't be notified about theses broadcasts anymore).   
 * <br><br>
 * All subclasses of CallbackAgent are run in a single thread, therefore it is more efficient
 * than ThreadAgents. However, the callbacks can't do any blocking operation or too long 
 * computations, otherwise all CallbackAgents will be in deadlock or starvation.
 * 
 * @author Pablo A. Sampaio
 */
public abstract class CallbackAgent extends AbstractAgent {
	private long noPerceptionTimeout = 500;
	private long nextNoPerceptionTimeout;

	
	protected CallbackAgent(String id, ClientConnection conn, boolean useGraph2) {
		super(id, conn, useGraph2);
	}
	
	public void startWorking() {	
		super.startWorking();
		this.nextNoPerceptionTimeout = System.currentTimeMillis() + noPerceptionTimeout;
		CallbackAgentManager.getInstance().addAgent(this);
	}
	
	public void stopWorking() {
		CallbackAgentManager.getInstance().removeAgent(this);
		super.stopWorking();
	}
	
	
	//TODO: criar onNewTurn
	
	/**
	 * Must be overridden by subclasses. This is the generic callback, called for any 
	 * new perception received.
	 * <br><br>
	 * To be notified of specific types of perceptions, a subclass can optionally override 
	 * any of other methods named "on[TypeOfPerception]()". 
	 */
	protected abstract void onPerception() throws AgentStoppedException;

	/**
	 * Must be overridden by subclasses. It is called when no perceptions where received
	 * for, at least, the time set by setNoPerceptionTimeout(). 
	 */
	protected abstract void onNoPerceptionTimeout() throws AgentStoppedException;
	
	/**
	 * Must be overridden by subclasses. It is called when the agent is stopped.
	 * After receiving this callback, no other callback (for perceptions) happens anymore.
	 */
	protected abstract void onStop();


	/**
	 * Sets the timeout for calling the "onNoPerception()". Default value is 300 (0.3 sec).
	 */
	protected void setNoPerceptionTimeout(long timeInMillis) {
		this.nextNoPerceptionTimeout += (timeInMillis - this.nextNoPerceptionTimeout);
		this.noPerceptionTimeout = timeInMillis;
	}	

	/**
	 * Can optionally be overridden by subclasses to be notified about new self perception, 
	 * that can be read with perceiveSelf(). 
	 */
	protected void onSelfPerception() throws AgentStoppedException {
	}
	
	/**
	 * Can optionally be overridden by subclasses to be notified about new society perception, 
	 * that can be read with perceiveSociety(). 
	 */
	protected void onSocietyPerception() throws AgentStoppedException {		
	}

	/**
	 * Can optionally be overridden by subclasses to be notified about new graph perception, 
	 * that can be read with perceiveGraph(). 
	 */
	protected void onGraphPerception() throws AgentStoppedException {
	}
	
	/**
	 * Can optionally be overridden by subclasses to be notified about new broadcasts received, 
	 * that can be read with retrieveBroadcasts(). 
	 */
	protected void onBroadcasts() throws AgentStoppedException {		
	}

	//used only by the manager
	final void updateNoPerceptionTimeout(boolean perceived) throws AgentStoppedException {
		if (perceived) {
			this.nextNoPerceptionTimeout = System.currentTimeMillis() + this.noPerceptionTimeout;
		
		} else {
			if (System.currentTimeMillis() >= this.nextNoPerceptionTimeout) {
				this.onNoPerceptionTimeout();
				this.nextNoPerceptionTimeout = System.currentTimeMillis() + this.noPerceptionTimeout;
			}
		}
		
	}

}
