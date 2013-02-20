package SC;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.StringAndDouble;
import util.graph.Graph;
import util.net.TCPClientConnection;

import common.OpenAgent;

public class HS_OpenCycledAgent extends OpenAgent {

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;
	/** Registers the current step of the path planned by the agent. */
	private int current_plan_step;
	
	
	/** Registers the agents this one have already perceived. */
	private HashSet<String> PERCEIVED_AGENTS;
	/**
	 * Registers how many agents this one must let pass before start walking on
	 * the graph.
	 */
	private boolean oriented = false;
	private int let_pass;
	private int time_multiplier = 1;
	private int nb_agents = 0;

	/**
	 * Registers how much time this agent must wait before start walking on the
	 * graph, after the last agent passed it.
	 */
	private double wait_time;
	
	private boolean start_moving = false;
	/**
	 * Registers the time the agent started counting up until the moment to
	 * start walking on the graph.
	 */
	private double start_time;
	
	private boolean activation_sent = false;
	private boolean enter_message_sent = false;

	
	/** Constructor. */
	public HS_OpenCycledAgent(String id) {
		super(id);
		this.PLAN = new LinkedList<String>();
		this.let_pass = 0;
		this.wait_time = -1;
		this.start_time = -1;
		this.current_plan_step = -1;
	}
	
	public HS_OpenCycledAgent(String id, double entering_time, double quitting_time, String society_id) {
		super(id, entering_time, quitting_time, society_id);

		this.PLAN = new LinkedList<String>();
		this.let_pass = 0;
		this.wait_time = -1;
		this.start_time = -1;
		this.current_plan_step = -1;
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
			
			// if this agent is concerned by the message
			if (message.contains(this.agent_id)){
				
				// orientation message
				if(message.indexOf("###") > -1){
				
					// get plan
					String str_plan = message.substring(0, message.indexOf("###"));
					StringTokenizer tokenizer = new StringTokenizer(str_plan, ",");
	
					System.err.print(this.agent_id + ": Plan:");
					while (tokenizer.hasMoreTokens()) {
						String next_step = tokenizer.nextToken();
						this.PLAN.add(next_step);
						System.err.print(" " + next_step);
					}
					System.err.println();
					
					// updates the current plan step, if possible and necessary
					if (this.current_position != null
							&& this.current_plan_step == -1) {
						this.current_plan_step = this.PLAN
								.indexOf(this.current_position.STRING);
						System.err.println("Started on step "
								+ this.current_plan_step + ", node "
								+ this.current_position.STRING);
					}
					
					
					// obtains the number of agents that this one must let pass
					message = message.substring(message.indexOf(this.agent_id) + this.agent_id.length() + 1);
					this.let_pass = Integer.parseInt(message.substring(0, message.indexOf(";")));
					System.err.println(this.agent_id + ": Let pass " + this.let_pass + " agents.");
					
					// obtains the time this agent must wait, after the last agent
					// passed it, to start walking
					message = message.substring(message.indexOf(";") + 1);
					this.time_multiplier = Integer.parseInt(message.substring(0, message.indexOf(";")));
					
					message = message.substring(message.indexOf(";") + 1);
					if(message.indexOf("###") < 0)
						this.wait_time = this.time_multiplier * Double.parseDouble(message);
					else
						this.wait_time = this.time_multiplier * Double.parseDouble(message.substring(0, message.indexOf("###")));
					System.err.println(this.agent_id + ": Must wait " + this.wait_time
							+ " cycles/seconds.");
					
					if(this.wait_time == 0)
						this.start_moving = true;
					// returns the success of the perception
					return true;
				
				}
				
				// reorientation message
				else if(message.indexOf("#!#") > -1){
					//if it's a STOP message
					if (message.contains("#!#STOP")){
						//must_wait = true;
						return true;
					}
					
					System.err.println(this.agent_id + "Reorienting... cycle" + this.time);
					// obtains the number of agents that this one must let pass
					message = message.substring(message.indexOf(this.agent_id) + this.agent_id.length() + 1);
					
					/*
					this.let_pass = Integer.parseInt(message.substring(0, message.indexOf(";")));
					PERCEIVED_AGENTS = null;
					System.err.println(this.agent_id + ": Let pass " + this.let_pass + " agents.");
					*/
					
					// obtains the time this agent must wait, after the last agent
					// passed it, to start walking
					//message = message.substring(message.indexOf(";") + 1);
					if(this.let_pass > 0)
						this.wait_time = this.time_multiplier * Double.parseDouble(message.substring(0, message.indexOf(";")));
					else {
						this.wait_time += Double.parseDouble(message.substring(0, message.indexOf(";")));
						System.err.println(this.agent_id + ": Must wait " + this.wait_time + " cycles/seconds.");
					}
					
					// due to the way the coordinator recalculates the positions, we need to reset the current step plan
					//if(let_pass !=0)
					//	this.current_plan_step = this.PLAN.indexOf(this.current_position.STRING);
					
					// returns the success of the perception
					//must_wait = false;
					//this.cycles_count = 0;
					//this.start_time = -1;
					return true;
					
				}
			}
			
		
			return false;
		}
		// default answer
		return false;
	}
	
	
	private void manageMessages(String perception){
		if(perception.indexOf("waiting_time?") > -1){
			int index = perception.indexOf("waiting_time?") + 14;
			String agent_id = perception.substring(index);
			agent_id = agent_id.substring(0, agent_id.indexOf("\""));
			this.SendMessage("waiting_time!;" + this.wait_time, agent_id);
		}
		if(perception.indexOf("waiting_time!") > -1){
			int index = perception.indexOf("waiting_time!") + 14;
			String time = perception.substring(index);
			time = time.substring(0, time.indexOf("\""));
			this.wait_time += Math.max(0, Double.valueOf(time));
		}
	}
	
	
	
	/**
	 * Lets the agent perceive and count the passing agents.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agententer_message_sent perceived the passing agents, FALSE if not.
	 */
	private boolean perceivePassingAgents(String perception) {
		// lets the agent perceive some agent
		if (perception.indexOf("<perception type=\"1\"") > -1) {
			// holds the perceived agents
			String current_perceived_agent = "";
			String current_agent_position = "";

			// at the beginning, if the other agents are on the same node, they are considered as passing by,
			// but we wait more time before starting
			if(this.PERCEIVED_AGENTS == null){
				this.PERCEIVED_AGENTS = new HashSet<String>();
				int agent_index = perception.indexOf("<agent id=\"");
				while (agent_index > -1) {
					perception = perception.substring(agent_index + 11);
					current_perceived_agent = perception.substring(0, perception.indexOf("\""));
					agent_index = perception.indexOf("node_id=\"");
					perception = perception.substring(agent_index + 9);
					current_agent_position = perception.substring(0, perception.indexOf("\""));
					
					String edge = null;
					double length = 0;
					if(perception.indexOf("edge_id") > -1 && (perception.indexOf("<agent id=\"") == -1 ||
							perception.indexOf("edge_id") < perception.indexOf("<agent id=\""))){
						int edge_index = perception.indexOf("edge_id=\"");
						perception = perception.substring(edge_index + 9);
						edge = perception.substring(0, perception.indexOf("\""));
						
						int length_index = perception.indexOf("elapsed_length=\"");
						perception = perception.substring(length_index + 16);
						String length_str = perception.substring(0, perception.indexOf("\""));
						length = Double.valueOf(length_str);
					}
					
					nb_agents++;
					
					if (!current_perceived_agent.equals("coordinator")
							&& !this.PERCEIVED_AGENTS.contains(current_perceived_agent)
							&& this.current_position.STRING.equals(current_agent_position)
							&& length == 0) {
						// adds it to the already perceived agents
						this.PERCEIVED_AGENTS.add(current_perceived_agent);

						System.err.println(this.agent_id + ": Agent " + current_perceived_agent + " is in the same spot as me.");
					}		
					agent_index = perception.indexOf("<agent id=\"");
				}
				
				if(nb_agents==2 && this.PERCEIVED_AGENTS.size() == 1)
					this.let_pass--;
				
			}
			
			else
			{
			// perceives the agents
				int agent_index = perception.indexOf("<agent id=\"");
				while (agent_index > -1) {
					perception = perception.substring(agent_index + 11);
					current_perceived_agent = perception.substring(0, perception.indexOf("\""));
					agent_index = perception.indexOf("node_id=\"");
					perception = perception.substring(agent_index + 9);
					current_agent_position = perception.substring(0, perception.indexOf("\""));
					
					String edge = null;
					double length = 0;
					if(perception.indexOf("edge_id") > -1 && (perception.indexOf("<agent id=\"") == -1 ||
							perception.indexOf("edge_id") < perception.indexOf("<agent id=\""))){
						int edge_index = perception.indexOf("edge_id=\"");
						perception = perception.substring(edge_index + 9);
						edge = perception.substring(0, perception.indexOf("\""));
						
						int length_index = perception.indexOf("elapsed_length=\"");
						perception = perception.substring(length_index + 16);
						String length_str = perception.substring(0, perception.indexOf("\""));
						length = Double.valueOf(length_str);
					}
					
					if (!current_perceived_agent.equals("coordinator")
							&& !this.PERCEIVED_AGENTS.contains(current_perceived_agent)
							&& this.current_position.STRING.equals(current_agent_position)
							&& length == 0) {
						// adds it to the already perceived agents
						this.PERCEIVED_AGENTS.add(current_perceived_agent);
						
						if(this.let_pass == 1)
							this.SendMessage("waiting_time?;"+this.agent_id, current_perceived_agent);
						
						// decreases the number of agents to let pass
						this.let_pass--;
						System.err.println(this.agent_id + ": Agent " + current_perceived_agent + " passed me.");
					}		
					agent_index = perception.indexOf("<agent id=\"");
					
				}
			}


			// if the number of agents to let pass is smaller than one, sets the
			// "start time" attribute, if necessary
			if (this.let_pass < 1 && this.start_time < 0) {
				if (this.connection instanceof TCPClientConnection)
					this.start_time = this.time;
				else
					this.start_time = System.nanoTime();

				System.err.println(this.agent_id + ": Started counting down.");
			}

			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}


	private void sendQuitMessage() throws IOException {
			this.SendMessage(this.agent_id
					+ "###QUIT", "coordinator");
	}
	
	/**
	 * Lets the agent visit the current position and go to the next step (i.e.
	 * node) of its plan.
	 * 
	 * @throws IOException
	 */
	private void visitAndGoToNextStep() throws IOException {
		// lets the agent visit the current position
		this.visitCurrentNode(this.current_position.STRING);

		// lets the agent go to the next position
		if(this.current_plan_step == this.PLAN.size() - 1)
			this.current_plan_step = -1;
		this.current_plan_step = (this.current_plan_step + 1);

		String next_node_id = this.PLAN.get(this.current_plan_step);
		this.GoTo(next_node_id);

		//System.err.println(this.agent_id +": Gone to step " + this.current_plan_step
		//		+ ", node " + next_node_id + ", cycle " + this.time);
	}	
	
	@Override
	protected void inactive_run() {
		boolean time_actualized = false;
		String[] perceptions;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
	}	
	
	@Override
	protected void activating_run() {
		if(!this.activation_sent){
			this.Activate(this.Society_id);
			this.activation_sent = true;
		}
		
		boolean updated_time = false;

		while(!updated_time){
			// obtains the perceptions sent by SimPatrol server
			String[] perceptions = this.connection.getBufferAndFlush();
	
			// for each perception, starting from the most recent one
			for (int i = perceptions.length - 1; i >= 0; i--) {
					
				updated_time |= perceiveTime(perceptions[i]);
				
				if(perceiveOrientation(perceptions[i]))
					this.oriented = true;
				
				// tries to obtain the current position
				StringAndDouble sent_position = null;
				sent_position= this.perceiveCurrentPosition(perceptions[i]);
				
				if(sent_position != null )
					this.current_position = sent_position;
	
				
				Graph sentgraph = null;
				if(sent_position == null){
					try {
						sentgraph = this.perceiveGraph(perceptions[i]);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(sentgraph != null && sentgraph != this.graph)
						this.graph = sentgraph;
				}
			}
		}
		
		if(!this.enter_message_sent && this.current_position != null){
			this.SendMessage(this.agent_id + "###ENTER," + this.current_position.STRING, "coordinator");
			this.enter_message_sent = true;
		}
		
		if(this.oriented){
			this.inactive = false;
			this.PERCEIVED_AGENTS = new HashSet<String>();
		}
		
		this.Wait();
		
	}

	@Override
	protected void active_run() {
		
		// registers if this agent received orientations from the coordinator
		boolean received_orientation = false;
		boolean updated_time = false;
		boolean received_position = false;
		double previous_time = this.time;
		
		boolean acted = false;
		
		int tries = 0;
		
		while(!acted){
			String[] perceptions = this.connection.getBufferAndFlush();

			// tries to perceive the current position of the agent
			for (int i = perceptions.length - 1; i >= 0; i--)
				if(updated_time |= perceiveTime(perceptions[i]))
					break;
				
			for (int i = perceptions.length - 1; i >= 0; i--){
				// tries to obtain the current position
				StringAndDouble sent_position = null;
				sent_position= this.perceiveCurrentPosition(perceptions[i]);
				
				if(sent_position != null && !sent_position.equals(this.current_position)){
					this.current_position = sent_position;
					received_position = true;
					break;
				}
			}
			
			for (int i = perceptions.length - 1; i >= 0; i--){
				// tries to perceive the current graph
				Graph next_graph = null;
	
				try {
					next_graph = this.perceiveGraph(perceptions[i]);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if obtained a graph, quits the loop
				if (next_graph != null){
					this.graph = next_graph;
					break;
				}
			}
			
			// tries to perceive the coordinator's orientation, if necessary
			for (int i = perceptions.length - 1; i >= 0; i--){
				received_orientation = this.perceiveOrientation(perceptions[i]);
				if (received_orientation){
					this.oriented = true;
				}
			}
			
			if(this.oriented){
				for (int i = 0; i < perceptions.length; i++){
					this.perceivePassingAgents(perceptions[i]);
					this.manageMessages(perceptions[i]);
				}
	
				
				if(this.current_position.double_value == 0){
					// verifies if it is time to start walking
					if(this.let_pass > 0 && updated_time){
						this.Wait();
						acted = true;
					}
						
					else if (this.wait_time >= 0 && updated_time) {
						this.Wait();
						this.wait_time -= (this.time - previous_time);
						//his.wait_time -= 1;
						if(this.wait_time <= 0){
							this.start_moving = true;
							this.time_multiplier = 1;
						}
						acted = true;
					}
					
					else if(this.start_moving || (updated_time & received_position) || (received_position && tries > 10000)){
						try {
							this.visitAndGoToNextStep();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						if(this.start_moving)
							this.start_moving = false;
						acted = true;
					}
					
					tries++;
				}
				
				else if(received_position && this.current_position.double_value != 0){
					acted = true;
				}
			}
			/*
			 else
				if(updated_time){
					this.Wait();
					acted = true;
				}
			*/
		}
				


		
	}

	@Override
	protected void deactivating_run() {
		try {
			this.sendQuitMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.Deactivate();
		this.inactive = true;
		this.engaging_out = false;

	}



}
