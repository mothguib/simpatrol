package agent_library.basic_agents;

import java.util.ArrayList;

import util.agents.SocietyImage;
import agent_library.connections.ClientConnection;
import agent_library.perceptions.AgentInformation;


/**
 * Class that should be extended to create agents that works as a thread (each). If offers the following new
 * methods to access the perceptions:
 * <ul>
 * <li>Methods named <b>waitNew[Perception]</b>: Wait until: (1) a new perception of that kind arrive, or (2) until a timeout occurs, or (3) until the agent is stopped.
 *		<ul>
 * 		<li>To differentiate the first two (normal) cases, you should call hasNew[Perception].</li> 
 * 		<li>The perception itself can be accessed by a method perceive[Perception]. </li>
 *      <li>The third case throws an AgentStoppedException.</li>
 * 		</ul>
 * </li>
 * 
 * <li>Methods named <b>perceive[Perception]Blocking</b>: Like the method wait[Perception], but it 
 * returns the perception object itself, if it arrives before timeout.
 * 		<ul>
 * 		<li> If timeout occurs, no perception is returned -- it returns null, in general, and an empty list in the case of broadcasts.
 * 		<li> Attention: Methods to perceive broadcasts use slightly different name: retrieveBroadcastsBlocking.</li>
 * 		</ul>  
 * </ul>
 *  
 * @author Pablo A. Sampaio
 */
public abstract class ThreadAgent extends AbstractAgent implements Runnable {
	private static long DEFAULT_TIME_OUT = 20000;  //20 seconds 
	private static long MAX_SLEEP_TIME   =    30;  //0.03 second
	

	protected ThreadAgent(String id, ClientConnection connection, boolean useGraph2) {
		super(id, connection, useGraph2);
	}
	
	@Override
	public void startWorking() {
		super.startWorking();
		new Thread(this).start();
	}

	protected void agentSleep(long sleepTime) {
		if (sleepTime > MAX_SLEEP_TIME) {
			sleepTime = MAX_SLEEP_TIME;
		}
		try {
			synchronized (this) {
				this.wait(sleepTime); //can be awaken with notify()
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// 1. TURN perception 
	
	protected void waitNewTurn(int turnToPerceive, long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (super.getCurrentTurn() < turnToPerceive) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				break;
			}
			agentSleep(timeOut);
		}
	}
	
	protected int perceiveTurnBlocking(int turnToPerceive, long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (super.getCurrentTurn() < turnToPerceive) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				return -1;
			}
			agentSleep(timeOut);
		}
		return super.getCurrentTurn();
	}
	
	protected void waitNewTurn(int turnToPerceive) throws AgentStoppedException {
		waitNewTurn(turnToPerceive, DEFAULT_TIME_OUT);
	}

	protected int perceiveTurnBlocking(int turnToPerceive) throws AgentStoppedException {
		return perceiveTurnBlocking(turnToPerceive, DEFAULT_TIME_OUT);
	}

	// 2. SELF perception
	
	protected void waitNewSelfPerception(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewSelfInfo) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				break;
			}
			agentSleep(timeOut);
		}
	}	
	
	protected AgentInformation perceiveSelfBlocking(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewSelfInfo) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				return null;
			}
			agentSleep(timeOut);
		}
		return perceiveSelf();
	}
	
	protected void waitNewSelfPerception() throws AgentStoppedException {
		waitNewSelfPerception(DEFAULT_TIME_OUT);
	}

	protected AgentInformation perceiveSelfBlocking() throws AgentStoppedException {
		return perceiveSelfBlocking(DEFAULT_TIME_OUT);
	}

	// 3. SOCIETY perception
	
	protected void waitNewSocietyPerception(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewSocietyInfo) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				break;
			}
			agentSleep(timeOut);
		}
	}
	
	protected SocietyImage perceiveSocietyBlocking(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewSocietyInfo) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				return null;
			}
			agentSleep(timeOut);
		}
		return perceiveSociety();
	}
	
	protected void waitNewSocietyPerception() throws AgentStoppedException {
		waitNewSocietyPerception(DEFAULT_TIME_OUT);
	}

	protected SocietyImage perceiveSocietyBlocking() throws AgentStoppedException {
		return perceiveSocietyBlocking(DEFAULT_TIME_OUT);
	}

	// 4. GRAPH perception
	
	protected void waitNewGraphPerception(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewGraph) {
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				break;
			}
			agentSleep(timeOut);
		}
	}
	protected void waitNewGraphPerception() throws AgentStoppedException {
		waitNewGraphPerception(DEFAULT_TIME_OUT);
	}
	
	protected util.graph.Graph perceiveGraphBlocking(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewGraph) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				return null;
			}
			agentSleep(timeOut);
		}
		return perceiveGraph();
	}

	protected util.graph2.Graph perceiveGraph2Blocking(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.receivedNewGraph) { 
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				return null;
			}
			agentSleep(timeOut);
		}
		return perceiveGraph2();
	}

	protected util.graph.Graph perceiveGraphBlocking() throws AgentStoppedException {
		return perceiveGraphBlocking(DEFAULT_TIME_OUT);
	}

	protected util.graph2.Graph perceiveGraph2Blocking() throws AgentStoppedException {
		return perceiveGraph2Blocking(DEFAULT_TIME_OUT);
	}
	
	// 5. BROADCASTS perception
	
	protected void waitNewBroadcasts(long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.hasNewBroadcasts()) {
			if (this.stopRequested) {
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				break;
			}
			agentSleep(timeOut);
		}
	}
	
	protected void retrieveBroadcastsBlocking(ArrayList<String> bufferMsgs, long timeOut) throws AgentStoppedException {
		long startTime = System.currentTimeMillis();
		while (!this.hasNewBroadcasts()) {
			if (this.stopRequested) {
				bufferMsgs.clear();
				throw new AgentStoppedException(this.identifier);				
			} else if ((System.currentTimeMillis() - startTime) > timeOut) {
				bufferMsgs.clear();
				return;
			}
			agentSleep(timeOut);
		}
		this.retrieveBroadcasts(bufferMsgs);
	}
	
	protected void waitNewBroadcasts() throws AgentStoppedException {
		waitNewBroadcasts(DEFAULT_TIME_OUT);
	}

	protected void retrieveBroadcastsBlocking(ArrayList<String> bufferMsgs) throws AgentStoppedException {
		retrieveBroadcastsBlocking(bufferMsgs, DEFAULT_TIME_OUT);
	}

	
	// se eu criar uma resposta do servidor para cada ação, posso criar metodos assim:
	// actGoToNodeBlocking(), etc, para aguardar a resposta (até certo TIMEOUT..)
	
	// para outros casos, teria uma ação actionConfirme ou getActionConfirmation
	
}
