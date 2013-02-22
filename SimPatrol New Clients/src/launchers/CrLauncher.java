package launchers;

import java.io.IOException;
import java.util.HashSet;

import strategies.cr.CrAgent;
import agent_library.basic_agents.AbstractAgent;
import agent_library.connections.ClientConnection;
import agent_library.connections.IpcConnection;
import agent_library.connections.TcpConnection;
import agent_library.launcher.Launcher;


/**
 * This class starts the agents of the "Conscientious Reactive" (CR) strategy.
 * <br><br>
 * First, start the simulator, then run this class, giving a configuration file as parameter. 
 * 
 * @see Launcher
 * @author Pablo A. Sampaio
 */
public class CrLauncher extends Launcher {
	private static final String USAGE = Launcher.USAGE.replaceFirst("\\[CLIENT_CLASS\\]", CrLauncher.class.getSimpleName())
			+ "\twhere <agents' parameter> can be:\n"
			+ "\t\t -ipc         Agents use IPC connection (Default: TCP)\n"
			+ "\n";
	
	private boolean useIpc;

	
	/**
	 * See the comment of the main() method. 
	 */
	public CrLauncher(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	protected int processAgentSpecificCommand(String[] cmds, int index) throws Exception {
		if (cmds[index].equals("-ipc")) {
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
			
			agent = new CrAgent(agentIds[i], connection);
			
			this.agents.add(agent);
			agent.startWorking();
		}
	}

	/**
	 * This method requires the same parameters as in the agent_library.Launcher 
	 * class. Additionally, it accepts the following (optional) parameter to set 
	 * IPC communication (for Windows-only):
	 * <br><br>
	 * -ipc  
	 * <br><br>
	 * If this parameter is not given, the launcher assumes communication by TCP.
	 * 
	 * @see Launcher 
	 */
	public static void main(String[] args) {
		System.out.println("Conscientious Reactive Agents!");

		try {
			CrLauncher client = new CrLauncher(args);
			client.start();		
		} catch (Exception e) {
			System.out.println(USAGE);
			e.printStackTrace();
		}
		
	}
	

}
