package dummy_client;


/**
 * A simple agent that just goes back and forth between two nodes. 
 * This is the "threaded version": each agent is a thread. It is implemented
 * as a subclass of the "listener version" only to reuse code.
 *  
 * @author Pablo Sampaio
 */
public class DummyAgentThreaded extends DummyAgent implements Runnable {
	

	public DummyAgentThreaded(String agentId, TcpConnection con, String currentNode, String neighborNode) {
		super(agentId, con, currentNode, neighborNode);
	}
	
	
	@Override
	public void startWorking() {
		connection.start();	
		working = true;
		
		Thread thread = new Thread(this);
		thread.start();
	}	

	@Override
	public void stopWorking() {
		asyncPrint("Preparing to finish..");
		working = false;
		connection.stopWorking();
	}
	
	@Override
	public void start() {
		// not used
	}
	
	@Override
	public void update() {
		// not used
	}
	
	/**
	 * Main method of this agent, where his behavior is implemented.
	 */
	@Override
	public void run() {
	
		try {

			while (working) {
				visitAndGoToNextNode();
				waitForArrivalMessage();				
			}

		} catch (Exception exc) {
			asyncPrint("Error");
			working = false;
			exc.printStackTrace();
			
		}
		
		asyncPrint("Agent's thread finished");
	}
	
	
	/**
	 * Waits the agent to arrive at his destiny node. Only returns when
	 * the agent arrives. 
	 */
	private void waitForArrivalMessage() {
		boolean agentArrived = false;
		
		agentArrived = lookForArrivalMessage();
		
		while (!agentArrived && this.working) {
			Thread.yield();
			agentArrived = lookForArrivalMessage();
		}		
	}
	
}

