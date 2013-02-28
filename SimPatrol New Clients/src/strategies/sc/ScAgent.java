package strategies.sc;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;
import agent_library.connections.IpcConnection;

import util.Keyboard;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import view.connection.IPCConnection;
import common.Agent;


public class ScAgent extends ThreadAgent {

	// the plan of walking cyclically through the graph, which is a TSP solution
	private final LinkedList<String> PLAN;

	// registers how many agents this one must let pass before start walking on the graph.
	private int let_pass;

	// registers how much time this agent must wait before start walking on the
	// graph, after the last agent passed it
	private double wait_time;
	
	// registers the time the agent started counting up until the moment to
	// start walking on the graph.
	private double start_time;

	/// registers the agents this one have already perceived
	private HashSet<String> PERCEIVED_AGENTS;

	/// register the current position of the agent
	private String current_node_id;

	// registers the current step of the path planned by the agent
	private int current_plan_step;

	// lets the agent count up how many cycles it has experienced (if it is a
	// turn-based simulation).
	private int cycles_count;
	
	// registers if this agent received orientations from the coordinator
	boolean received_orientation = false;

	// holds the currently perceived graph
	Graph current_graph = null;

	public ScAgent(String id, ClientConnection conn) {
		super(id, conn, false);
		this.PLAN = new LinkedList<String>();
		this.let_pass = 0;
		this.wait_time = 0;
		this.start_time = -1;
		this.PERCEIVED_AGENTS = new HashSet<String>();
		this.current_node_id = null;
		this.current_plan_step = -1;
		this.cycles_count = 0;
	}

	/**
	 * Lets the agent perceive the orientation sent by the coordinator.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent perceived the orientation, FALSE if not.
	 */
	private boolean perceiveOrientation(String perception) {
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			// obtains the message broadcasted
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String message = perception.substring(0, perception.indexOf("\""));

			// obtains the id of the agent to whom the message was broadcasted
			String agent_id = message.substring(0, perception.indexOf("###"));
			
			// if the message is to this agent
			if (agent_id.equals(this.identifier)) {
				// obtains the plan of walking through the graph				
				message = message.substring(message.indexOf("###") + 3);

				String str_plan = message.substring(0, message.indexOf("###"));
				StringTokenizer tokenizer = new StringTokenizer(str_plan, ",");

				System.err.print("Plan:");
				while (tokenizer.hasMoreTokens()) {
					String next_step = tokenizer.nextToken();
					this.PLAN.add(next_step);
					System.err.print(" " + next_step);
				}
				System.err.println();

				// updates the current plan step, if possible and necessary
				if (this.current_node_id != null
						&& this.current_plan_step == -1) {
					this.current_plan_step = this.PLAN
							.indexOf(this.current_node_id);
					System.err.println("Started on step "
							+ this.current_plan_step + ", node "
							+ this.current_node_id);
				}

				// obtains the number of agents that this one must let pass
				message = message.substring(message.indexOf("###") + 3);
				this.let_pass = Integer.parseInt(message.substring(0, message
						.indexOf(";")));
				System.err.println("Let pass " + this.let_pass + " agents.");

				// obtains the time this agent must wait, after the last agent
				// passed it, to start walking
				this.wait_time = Double.parseDouble(message.substring(message
						.indexOf(";") + 1));
				System.err.println("Must wait " + this.wait_time
						+ " cycles/seconds.");

				// returns the success of the perception
				return true;
			}
		}

		// default answer
		return false;
	}

	/**
	 * Lets the agent perceive and count the passing agents.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent perceived the passing agents, FALSE if not.
	 */
	private boolean perceivePassingAgents(String perception) {
		// lets the agent perceive some agent
		if (perception.indexOf("<perception type=\"1\"") > -1) {
			// holds the perceived agents
			HashSet<String> current_perceived_agents = new HashSet<String>();

			// perceives the agents
			int agent_index = perception.indexOf("<agent id=\"");
			while (agent_index > -1) {
				perception = perception.substring(agent_index + 11);
				current_perceived_agents.add(perception.substring(0, perception
						.indexOf("\"")));
				agent_index = perception.indexOf("<agent id=\"");
			}

			// for each currently perceived agent
			for (String agent : current_perceived_agents)
				// if such agent was not perceived before...
				if (!this.PERCEIVED_AGENTS.contains(agent)) {
					// adds it to the already perceived agents
					this.PERCEIVED_AGENTS.add(agent);

					// decreases the number of agents to let pass
					this.let_pass--;

					System.err.println("Agent " + agent + " passed me.");
				}

			// if the number of agents to let pass is smaller than one, sets the
			// "start time" attribute, if necessary
			if (this.let_pass < 1 && this.start_time < 0) {
				//if (this.connection instanceof TCPClientConnection || this.connection instanceof IPCConnection)
					this.start_time = this.cycles_count;
				//else
					//this.start_time = System.nanoTime();

				System.err.println("Started counting down.");
			}

			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}

	/**
	 * Lets the agent perceive its current position, given the string of a
	 * perception.
	 * 
	 * @param perception
	 *            A string representing the current perception of the agent.
	 * @return TRUE if the agent perceived a new position, FALSE if not.
	 */
	private boolean perceivePosition(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the current node
			int node_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(node_id_index + 9);
			String node_id = perception
					.substring(0, perception.indexOf("\""));

			// if such id is different from the previous id
			if (!node_id.equals(this.current_node_id)) {
				// updates it
				this.current_node_id = node_id;

				// updates the current plan step if necessary and possible
				if (!this.PLAN.isEmpty() && this.current_plan_step == -1) {
					this.current_plan_step = this.PLAN
							.indexOf(this.current_node_id);
					System.err.println("Started on step "
							+ this.current_plan_step + ", node "
							+ this.current_node_id);
				}

				// signalizes that the current position was updated
				return true;
			}
		}

		return false;
	}

	/**
	 * Lets the agent visit the current position and go to the next step (i.e.
	 * node) of its plan.
	 * 
	 * @throws IOException
	 */
	private void visitAndGoToNextStep() throws IOException {
		// lets the agent visit the current position
		
		String messages = "<action type=\"2\"/>";

		// lets the agent go to the next position
		this.current_plan_step = (this.current_plan_step + 1)
				% this.PLAN.size();

		String next_node_id = this.PLAN.get(this.current_plan_step);
		messages += "<action type=\"1\" node_id=\"" + next_node_id + "\"/>";
		this.connection.send(messages);

		System.err.println("Gone to step " + this.current_plan_step
				+ ", node " + next_node_id);
	}	
	
	public void run() {
		// starts its connection
		try {
			this.connection.open();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// registers if this agent received orientations from the coordinator
		boolean received_orientation = false;

		// holds the currently perceived graph
		Graph current_graph = null;

		// while the agent is supposed to work
		while (!this.stopRequested) {
			// obtains the current perceptions of the agent
			String[] perceptions = this.connection.getBufferAndFlush();
			if( perceptions.length == 0)
				try {
					synchronized (this){
						this.wait(30);
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			// tries to perceive the coordinator's orientation, if necessary
			if (!received_orientation)
				for (int i = 0; i < perceptions.length; i++) {
					received_orientation = this
							.perceiveOrientation(perceptions[i]);

					if (received_orientation)
						break;
				}

			// tries to perceive the current position of the agent
			for (int i = 0; i < perceptions.length; i++)
				if (this.perceivePosition(perceptions[i]))
					break;

			// if the agent perceived the coordinator's orientation
			if (received_orientation)
				// tries to perceive the other agents
				for (int i = 0; i < perceptions.length; i++)
					if (this.perceivePassingAgents(perceptions[i]))
						break;

			// verifies if it is time to start walking
			
			if (this.start_time > -1) {				
				//if (this.connection instanceof TCPClientConnection || this.connection instanceof IPCConnection) {
					if (this.cycles_count - this.start_time > this.wait_time) {
						
						// lets the agent walk and quits this loop
						try {
							this.visitAndGoToNextStep();
							System.err.println("Here I go: " + this.identifier + " On Cycle "+this.cycles_count);
						} catch (IOException e) {
							e.printStackTrace();
						}

						break;
					}
				/*} else {
					// obtains the current time of the system
					double current_time = System.nanoTime();

					// if enough time has passed
					if (current_time - this.start_time > (this.wait_time * Math
							.pow(10, 9))) {
						// lets the agent walk and quits this loop
						try {
							this.visitAndGoToNextStep();
						} catch (IOException e) {
							e.printStackTrace();
						}

						break;
					}
				}*/

			}

			// tries to perceive the current graph
			Graph[] next_graph = new Graph[0];
			for (int i = 0; i < perceptions.length; i++) {
				try {
					next_graph = GraphTranslator.getGraphs(GraphTranslator
							.parseString(perceptions[i]));
				} catch (SAXException e) {
					System.out.println("\n\nMensagem problematica: "+perceptions[i]+"\n\n");
					e.printStackTrace();
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// if obtained a graph, quits the loop
				if (next_graph.length > 0)
					break;
			}

			// if such graph is different from the current one
			if (next_graph.length > 0 && !next_graph[0].equals(current_graph)) {
				// updates the current graph
				current_graph = next_graph[0];

				// increments the cycles count
				this.cycles_count++;

				// do nothing
				//try {
					this.connection.send("<action type=\"-1\"/>");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			} else if (perceptions.length > 0) {
				//try {
					this.connection.send("<action type=\"-1\"/>");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
			Thread.yield();
		}
		
		// while the agent is supposed to work
		while (!this.stopRequested) {
			
			// obtains the current perceptions
			String[] perceptions = this.connection.getBufferAndFlush();
			if( perceptions.length == 0)
			try {
				synchronized (this){
					wait(30);
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// if the agent changed its position
			for (int i = 0; i < perceptions.length; i++)
				if (this.perceivePosition(perceptions[i])) {
					// lets the agent visit the current position and execute the
					// next step of it plan
					try {
						this.visitAndGoToNextStep();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}

	}

}
