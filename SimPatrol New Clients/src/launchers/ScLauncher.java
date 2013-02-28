package launchers;


import java.io.IOException;
import java.util.HashSet;

import strategies.sc.ScAgent;
import strategies.sc.ScCoordinator;
import agent_library.basic_agents.AbstractAgent;
import agent_library.connections.ClientConnection;
import agent_library.connections.IpcConnection;
import agent_library.connections.TcpConnection;
import agent_library.launcher.Launcher;


/**
 * This class creates and starts the agents of the "Single Cycle" (SC) strategy..
 * <br><br>
 * First, start the simulator, then run this class, giving a configuration file as parameter. 
 * 
 * @see Launcher
 * @author Pablo A. Sampaio
 */
public class ScLauncher extends Launcher {
	private static final String USAGE = Launcher.USAGE.replaceFirst("\\[CLIENT_CLASS\\]", ScLauncher.class.getSimpleName())
			+ "\twhere <agents' parameter> can be:\n"
			+ "\t\t -ipc         Agents use IPC connection (Default: TCP)\n"
			+ "\t\t -callback    Non-coordinator agents are not threads (Default: They are threads)\n"
			+ "\n";
	
	private boolean useIpc;
	private boolean useCallbackAgents;

	
	/**
	 * See the comment of the main() method. 
	 */
	public ScLauncher(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	protected int processAgentSpecificCommand(String[] cmds, int index) throws Exception {
		if (cmds[index].equals("-callback")) {
			this.useCallbackAgents = true;
			index++;			
		} else if (cmds[index].equals("-ipc")) {
			this.useIpc = true;
			index++;			
		}		
		return index;		
	}

	protected void createAndStartAgents(String[] agentIds, int[] portNumbers)
			throws IOException {
		this.agents = new HashSet<AbstractAgent>();
		
		String remoteAddress = this.CONNECTION.getRemoteAddress();
		ClientConnection connection = null;

		for (int i = 0; i < agentIds.length; i++) {
			AbstractAgent agent = null;

			if (useIpc) {
				connection = new IpcConnection(agentIds[i]);
			} else {
				connection = new TcpConnection(remoteAddress, portNumbers[i]);
			}
			
			if (agentIds[i].equals("coordinator")) {
				agent = new ScCoordinator(agentIds[i], connection);
			
			} else {
				if (useCallbackAgents) {
					//agent = new ScAgentCallback(Ids[i], connection);
					throw new RuntimeException("SC in callback mode not implemented yet!");
				} else {
					agent = new ScAgent(agentIds[i], connection);
				}
			}
			
			this.agents.add(agent);
			agent.startWorking();
		}
	}

	/**
	 * This method requires the same parameters as in the agent_library.Launcher class. 
	 * Additionally, it accepts the following (optional) parameters:
	 * <br><br>
	 * -ipc         to set IPC communication (for Windows-only)  
	 * <br>
	 * -callback    to use non-coordinated agents implemented without threads
	 * <br><br>
	 * The default is use TCP communication and to use each agent running as a thread. 
	 * @see Launcher
	 */
	public static void main(String[] args) {
		System.out.println("Single Cycle Agents!");

		try {
			ScLauncher client = new ScLauncher(args);
			client.start();		
		} catch (Exception e) {
			System.out.println(USAGE);
			e.printStackTrace();
		}
		
	}
	

}
