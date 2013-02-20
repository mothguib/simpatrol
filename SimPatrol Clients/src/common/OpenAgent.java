package common;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.AgentPosition;
import util.StringAndDouble;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.net.UDPClientConnection;

/**
 * This class is the basic agent structure for the patrolling problem with an open-system setting.
 * It provides all perception and action related functions, and indicates how to code the behavior 
 * of the agent during the various phases of its existence.
 * 
 * It can enter or leave only once during the life cycle. If you need agents coming and going, describe them as 
 * several agents each entering then leaving once.
 * 
 * the agent is described in the environment file as 
 * 
 *<agent id="a1@" label="a1" state="1" node_id="v1" stamina="1.0" max_stamina="1.0" activating_time="1200" society_to_join="your_society_id" deactivating_time="3400" >
 *    <allowed_perception type="-1"/>
 *    <allowed_perception type="0"/>
 *    <allowed_perception type="1"/>
 *    <allowed_perception type="3"/>
 *    <allowed_perception type="4"/>
 *    <allowed_action type="1"/>
 *    <allowed_action type="2"/>
 *    <allowed_action type="3"/>
 *    <allowed_action type="8"/>
 *</agent>
 * 
 * See Simpatrol description for the list of perception and action
 * 
 * IF :
 *   	- the agent must be active at start :
 *   remove < activating_time="1200" society_to_join="your_society_id" > and put the agent in the active society :
 *   <society id="your_society_id" label="your_society_id" is_closed="false"> 
 *   	agent description
 *   </society>
 *   
 *   	- the agent is inactive at start :
 *   put it in the inactive society after the active one :
 *   <society id="InactiveSociety" label="InactiveSociety" is_closed="false">
 * 		inactive agent description
 * 	 </society>
 * 				IMPORTANT : the inactive society MUST be IDed "InactiveSociety"
 * 
 * 		- the agent must not deactivate :
 * 	 remove < deactivating_time="3400" >
 * 
 * See ressources for example
 * 
 * 
 * @author pouletc
 * @since 26/10/2011
 *
 */
public abstract class OpenAgent extends Agent {

	protected String agent_id;
	
	protected String Society_id;
	
	/* inner representations of the environment */
	protected double time = -1;
	protected Graph graph;
	protected LinkedList<AgentPosition> AGENTS_POSITIONS;
	protected LinkedList<String> received_messages;
	protected StringAndDouble current_position;
	
	protected boolean inactive = false;
	
	/** 
	 * The quitting_time MUST BE > to the entering time
	 * if you want the agent active at the beginning of the simulation, set entering_time = -1
	 * if you don't want the agent to deactivate, set quitting_time = -1 
	 */
	protected double entering_time = -1;
	protected double quitting_time = -1;
	
	protected boolean engaging_in = false;
	protected boolean engaging_out = false;
	
	protected final static int NETWORK_QUALITY = 5;
	
	
	public OpenAgent(String id, double entering_time, double quitting_time, String Society) {
		super();
		
		this.agent_id = id;
		
		this.entering_time = entering_time;
		this.quitting_time = quitting_time;
		this.Society_id = Society;
		
		if(this.entering_time != -1)
			this.inactive = true;
	}
	
	public OpenAgent(String id) {
		super();

		this.agent_id = id;
	}
	
	public OpenAgent() {
		super();
	}
	

	/****************************************************************************
	 ****************************************************************************
	 * The following functions deal with perceptions coming from the simulator  *
	 ****************************************************************************
	 ****************************************************************************/
	
	/**
	 * Lets the agent perceive the internal time of the agent.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the time counter has been updated.
	 */
	protected boolean perceiveTime(String perception){
		// tries to obtain the most recent self perception of the agent
		if (perception.indexOf("<perception type=\"-1\"") > -1) {
			// obtains the id of the current node
			int time_index = perception.indexOf("time=\"");
			// CP : changed starting index
			perception = perception.substring(time_index + 6);
			String time_str = perception.substring(0, perception.indexOf("\""));
			double mytime = Double.valueOf(time_str);
			if(this.time < mytime){
				this.time = mytime;
				return true;
			}
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * Lets the agent perceive the position of the other agents.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent perceived the other agents and updates AGENTS_POSITIONS, FALSE if not.
	 */
	protected boolean perceiveAgentsPositions(String perception) {
		if (perception.indexOf("<perception type=\"1\"") > -1) {
			
			int agent_id_index = perception.indexOf("<agent id=\"");
			while (agent_id_index > -1) {
				perception = perception.substring(agent_id_index + 11);
				String agent_id = perception.substring(0, perception
						.indexOf("\""));

				int agent_node_id_index = perception.indexOf("node_id=\"");
				perception = perception.substring(agent_node_id_index + 9);
				String agent_node_id = perception.substring(0, perception.indexOf("\""));
				
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

				this.AGENTS_POSITIONS.add(new AgentPosition(agent_id, agent_node_id, edge, length));
				
				agent_id_index = perception.indexOf("<agent id=\"");
			}
			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}
	
	/** TODO : add the perception for stigmas **/
	
	/**
	 * Lets the agent perceive the graph to be patrolled
	 * 
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The perceived graph.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	protected Graph perceiveGraph(String perception) throws ParserConfigurationException, SAXException, IOException {
		Graph[] parsed_perception = GraphTranslator.getGraphs(GraphTranslator.parseString(perception));
		if (parsed_perception.length > 0)
			return parsed_perception[0];

		return null;
	}
	
	/**
	 * Lets the agent perceive the messages sent by other agents
	 * 
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return TRUE if a message was perceived and added in received_messages.
	 */
	protected boolean perceiveMessages(String perception){
		if(perception.indexOf("<perception type=\"3\"") > -1){
			received_messages.add(perception);
			return true;
		}
		return false;
	}
	
	/**
	 * Lets the agent perceive its current position.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The current position of the agent, as a pair "current vertex id -
	 *         elapsed length on the current edge".
	 */
	protected StringAndDouble perceiveCurrentPosition(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the current vertex
			int vertex_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(vertex_id_index + 9);
			String vertex_id = perception.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			double elapsed_length = 0;
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			if(elapsed_length_index != -1){
				perception = perception.substring(elapsed_length_index + 16);
				elapsed_length = Double.parseDouble(perception.substring(0, perception.indexOf("\"")));
			}
			// returns the answer of the method
			return new StringAndDouble(vertex_id, elapsed_length);
		}

		// default answer
		return null;
	}
	
	
	
	/****************************************************************************
	 ****************************************************************************
	 * The following functions deal with actions                                *
	 ****************************************************************************
	 ****************************************************************************/
	
	/**
	 * Lets the agent wait of a cycle
	 * 
	 */
	protected void Wait(){
		String message_to_sim = "<action type=\"-1\"/>\n";
		try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Lets the agent go to the next node
	 * 
	 * @param next_vertex_id
	 *            The next node the agent is supposed to go to.
	 * @throws IOException
	 */
	protected void GoTo(String next_vertex_id) throws IOException {
		String message = "<action type=\"1\" node_id=\"" + next_vertex_id + "\"/>";
		this.connection.send(message);
	}
	
	/**
	 * Lets the agent visit the current node
	 * 
	 * @throws IOException
	 */
	protected void visitCurrentNode(String node_name) throws IOException {
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
		
		if(this.agent_id != null)
			System.out.println("Agent " + this.agent_id + " visiting " + node_name + " at time " + this.time);
		
	}
	
	/**
     * broadcasts the given message to all active agents
     * 
     * @param message : the message to send
     */
    protected void BroadcastMessage(String message){
    	String message_to_sim = "<action type=\"3\" message=\"" + message + "\"/>";
		try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// if the simulation is a real time one, sends the message more 4
		// times
		if (this.connection instanceof UDPClientConnection)
			for (int j = 0; j < OpenAgent.NETWORK_QUALITY; j++) {
	
				// sends a message with the orientation
				try {
					this.connection.send(message_to_sim);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    }
	
    /** TODO : add the actions for stigma and recharge **/
    
	/**
     * broadcasts the given message to all the agents of the same society
     * 
     * @param message : the message to send
     */
    protected void BroadcastMessageToSociety(String message){
    	String message_to_sim = "<action type=\"7\" message=\"" + message + "\"/>";
		try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// if the simulation is a real time one, sends the message more 4
		// times
		if (this.connection instanceof UDPClientConnection)
			for (int j = 0; j < OpenAgent.NETWORK_QUALITY; j++) {
	
				// sends a message with the orientation
				try {
					this.connection.send(message_to_sim);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    }
 
    /**
     * Sends the given message to the target agent
     * 
     * @param message : the SpeechAct to send
     * @throws IOException
     */
    protected void SendMessage(String message, String target_id){
    	String message_to_sim = "<action type=\"8\" target_agent=\"" + 
    							target_id + "\" message=\"" + message + "\"/>";
    	try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
		// if the simulation is a real time one, sends the message more 4
		// times
		if (this.connection instanceof UDPClientConnection)
			for (int j = 0; j < OpenAgent.NETWORK_QUALITY; j++) {

				// sends a message with the orientation
				try {
					this.connection.send(message_to_sim);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
    }
    
    /**
     * Lets the agent activate in a given society
     * 
     * @param target_society_id
     * 					the id of the society to activate in
     */
    protected void Activate(String target_society_id){
    	String message_to_sim = "<action type=\"10\" society_id=\"" + target_society_id + "\"/>\n";
		try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Lets the agent deactivate
     * 
     */
	protected void Deactivate(){
		String message_to_sim = "<action type=\"11\"/>\n";
		try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * Lets the agent change society
     * 
     * @param target_society_id
     * 					the id of the society to go to
     */
    protected void ChangeSociety(String target_society_id){
    	String message_to_sim = "<action type=\"12\" society_id=\"" + target_society_id + "\"/>\n";
		try {
			this.connection.send(message_to_sim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    
    /****************************************************************************
	 ****************************************************************************
	 * The following functions deal with how the agent works                    *
	 ****************************************************************************
	 ****************************************************************************/
    
    /**
     * this function is the behaviour of the agent on a cycle if it is inactive.
     * Mainly, it must update the time variable
     */
    protected abstract void inactive_run();
    
    /**
     * this function is the behaviour of the agent on a cycle if it is activating
     * It must send an "activate" message and deal with eventual activation protocols
     * 
     * In the end, it must set this.engaging_in and this.inactive to false
     */
    protected abstract void activating_run();
    
    /**
     * this function is the behaviour of the agent on a cycle if it is active.
     * Mainly, it must manage perceptions then act
     */
    protected abstract void active_run();
    
    /**
     * this function is the behaviour of the agent on a cycle if it is deactivating
     * It must send an "deactivate" message and deal with eventual deactivation protocols
     * 
     * In the end, it must set this.engaging_out to false and this.inactive to true
     */
    protected abstract void deactivating_run();
    
    
    public void run(){
		// starts its connection
		this.connection.start();

		// while the agent is supposed to work
		while (!this.stop_working) {
			if(this.inactive && (this.time < this.entering_time || (this.quitting_time > -1 && this.time > this.quitting_time)))
				this.inactive_run();
			
			else if(this.inactive && (this.time >= this.entering_time) && ((this.time < this.quitting_time) || (this.quitting_time == -1))){
				this.engaging_in = true;
				this.activating_run();
			}
			
			else if(!this.inactive && (this.time >= this.entering_time) && ((this.time < this.quitting_time) || (this.quitting_time == -1)))
				this.active_run();	
			
			else if(!this.inactive && this.time >= this.quitting_time){
				this.engaging_out = true;
				this.deactivating_run();
			}
			
			Thread.yield();
		}
		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}



	

