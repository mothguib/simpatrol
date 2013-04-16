package launchers;

import java.io.IOException;
import java.util.HashSet;

import strategies.grav.GravCoordinatorAgent;
import strategies.grav.GravityManager;
import strategies.grav.core.ForceCombination;
import strategies.grav.core.ForcePropagation;
import strategies.grav.core.MassGrowth;
import agent_library.basic_agents.AbstractAgent;
import agent_library.connections.ClientConnection;
import agent_library.connections.IpcConnection;
import agent_library.connections.TcpConnection;
import agent_library.coordinated_agents.CoordinatedAgent;
import agent_library.coordinated_agents.CoordinatedAgentC;
import agent_library.launcher.Launcher;


/**
 * This class starts the agents of variants of the "Gravitational" (GRAV) strategy.
 * <br><br>
 * First, start the simulator, then run this class, giving a configuration file as parameter. 
 * 
 * @see Launcher
 * @author Pablo A. Sampaio
 */
public class GravLauncher extends Launcher {
	private static final String USAGE = Launcher.USAGE.replaceFirst("\\[CLIENT_CLASS\\]", GravLauncher.class.getSimpleName())
			+ "\twhere <agents' parameter> can be:\n"
			+ "\t\t -grav (Edge|Node|Mixed) (Ar|Ge|No) <exponent> (max|sum)  The default is \"Edge Ar 1.0 Max\"\n"
			+ "\t\t -ipc                                               Agents use IPC connection (Default: TCP)\n"
			+ "\t\t -callback                                          Non-coordinator agents are not threads (Default: They are threads)\n"
			+ "\n";
	
	private boolean gravParametersSet;  //don't start with default values here!
	private ForcePropagation propagation;
	private MassGrowth growth;
	private double exponent;
	private ForceCombination combination;
	
	private boolean useIpc;
	private boolean useCallbackAgents;

	/**
	 * See the comment of the main() method. 
	 */
	public GravLauncher(String[] args) throws Exception {
		super(args);
		if (!gravParametersSet) {
			this.propagation = ForcePropagation.EDGE;
			this.growth = MassGrowth.ARITHMETIC;
			this.exponent = 1.0d;
			this.combination = ForceCombination.MAX;
		}
	}
	
	@Override
	protected int processAgentSpecificCommand(String[] cmds, int index) throws Exception {
		if (cmds[index].equals("-callback")) {
			this.useCallbackAgents = true;
			index++;
			
		} else if (cmds[index].equals("-ipc")) {
			this.useIpc = true;
			index++;
			
		} else if (cmds[index].equals("-grav")) {
			index++;
			
			String parameter = cmds[index];
			
			if (parameter.equalsIgnoreCase("Node")) {
				this.propagation = ForcePropagation.NODE;
			} else if (parameter.equalsIgnoreCase("Edge")) {
				this.propagation = ForcePropagation.EDGE;
			} else if (parameter.equalsIgnoreCase("Mixed")) {
				this.propagation = ForcePropagation.MIXED;
			} else {
				throw new Exception("Invalid gravitational parameter!");
			}
			
			index++;
			parameter = cmds[index];
			
			if (parameter.equalsIgnoreCase("Ar")) {
				this.growth = MassGrowth.ARITHMETIC;
			} else if (parameter.equalsIgnoreCase("Ge")) {
				this.growth = MassGrowth.GEOMETRIC;
			} else if (parameter.equalsIgnoreCase("No")) {
				this.growth = MassGrowth.NO_GROWTH;
			} else {
				throw new Exception("Invalid gravitational parameter!");
			}
			
			index++;
			exponent = Double.parseDouble(cmds[index]);
			
			index++;
			parameter = cmds[index];
			
			if (parameter.equalsIgnoreCase("max")) {
				this.combination = ForceCombination.MAX;
			} else if (parameter.equalsIgnoreCase("sum")) {
				this.combination = ForceCombination.SUM;
			} else {
				throw new Exception("Invalid gravitational parameter!");
			}
			
			gravParametersSet = true;
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
			AbstractAgent agent;
			GravityManager manager;

			if (useIpc) {
				connection = new IpcConnection(agentIds[i]);
			} else {
				connection = new TcpConnection(remoteAddress, portNumbers[i]);
			}
			
			if (agentIds[i].equals("coordinator")) {
				manager = new GravityManager(propagation, growth, exponent, combination);
				agent = new GravCoordinatorAgent(agentIds[i], connection, manager);

			} else {
				if (useCallbackAgents) {
					agent = new CoordinatedAgentC(agentIds[i], connection);
				} else {
					agent = new CoordinatedAgent(agentIds[i], connection);
				}
			}
			
			this.agents.add(agent);
			agent.startWorking();
		}
	}

	/**
	 * This method requires the same parameters as in the agent_library.Launcher class. Additionally, 
	 * it accepts three optional other parameters. One of them is the following (optional) parameter 
	 * to set the type of gravitational propagation used:
	 * <br><br>
	 * -grav (Edge|Node|Mixed) (Ar|Ge|No) [exponent] (max|sum) 
	 * <br><br>
	 * If this parameter is not given, the launcher assumes "-grav Edge Ar 1.0 Max"
	 * <br><br>
	 * It also accepts these two parameters:
	 * <br><br>
	 * -callback  To use the implementation of the (coordinated) agents without threads. <br>
	 * -ipc       To use ICP communication (through memory, for Windows only).
	 */
	public static void main(String[] args) {
		System.out.println("Gravitational coordinated agents!");

		try {
			GravLauncher client = new GravLauncher(args);
			client.start();
		
		} catch (Exception e) {
			System.out.println(USAGE);
			e.printStackTrace();
		}
	}
	
}
