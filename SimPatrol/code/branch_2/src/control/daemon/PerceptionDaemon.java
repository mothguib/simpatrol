/* PerceptionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import util.Queue;
import model.agent.Agent;
import model.agent.AgentStates;
import model.agent.Society;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.perception.AgentsPerception;
import model.perception.BroadcastPerception;
import model.perception.GraphPerception;
import model.perception.Perception;
import model.perception.PerceptionTypes;
import model.perception.StigmasPerception;
import model.permission.PerceptionPermission;
import model.stigma.Stigma;

/** Implements the daemons of SimPatrol that produces
 *  an agent's perceptions.
 *  
 *  @developer New Perception classes must change this class.
 *  @developer New Limitation classes must change this class.
 *  @modeller This class must have its behaviour modelled. */
public final class PerceptionDaemon extends AgentDaemon {
	/* Attributes. */
	/** Queue of received messages sent by other agents. */
	private Queue<String> received_messages;
	
	/** The amount of stamina to be spent due to the perceptions. */
	private double spent_stamina;
	
	/* Methods. */
	/** Constructor.
	 * 
     *  Doesn't initiate its own connection, as it will be shared with an
	 *  ActionDaemon object. So the connection must be set by the
	 *  setConenction() method.
	 *  @see PerceptionDaemon
	 *  
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param agent The agent whose perceptions are produced. */
	public PerceptionDaemon(String thread_name, Agent agent) {
		super(thread_name, agent);
		this.received_messages = null;
		this.spent_stamina = 0;
		
		// configures the clock of the daemon
		this.clock.setUnity(Calendar.MILLISECOND);
		this.clock.setStep((int) (simulator.getAtualization_time_rate() * 1000));
	}
	
	/** Lets the agent of this daemon receive a message, if it has permission
	 *  to do it.
	 *  
	 *  @param message The message to be received by the agent of this daemon. */
	public void receiveMessage(String message) {
		// verifies if the agent has permission to receive broadcasted messages
		PerceptionPermission[] permissions = this.agent.getAllowedPerceptions();
		for(int i = 0; i < permissions.length; i++)
			if(permissions[i].getPerception_type() == PerceptionTypes.BROADCAST_PERCEPTION) {
				if(this.received_messages == null)
					this.received_messages = new Queue<String>();
				
				// adds the message to the received ones
				this.received_messages.insert(message);
				return;
			}
		
		// nullifies the received messages
		this.received_messages = null;					
	}
	
	/** Produces all the perceptions an agent is allowed to have at the moment.
	 * 
	 *  @return The perceptions of the agent at the moment.
	 *  @developer New Perception classes must change this method. */
	private Perception[] producePerceptions() {
		// holds the eventually produced perceptions
		List<Perception> perceptions = new LinkedList<Perception>();
		
		// resets the spent stamina with perceptions
		this.spent_stamina = 0;
		
		//  obtains the allowed perceptions for the agent
		PerceptionPermission[] allowed_perceptions = this.agent.getAllowedPerceptions();
		
		// for each allowed perception
		for(int i = 0; i < allowed_perceptions.length; i++) {
			// depending on the type of the current permission
			switch(allowed_perceptions[i].getPerception_type()) {
				// if it's a permission for "graph perceptions"
				case(PerceptionTypes.GRAPH_PERCEPTION): {
					// obtains the perception of the graph
					GraphPerception perception = this.produceGraphPerception(allowed_perceptions[i].getLimitations());
					
					// if the perception is valid, adds it to the produced perceptions
					if(perception != null) perceptions.add(perception);
					
					break;
				}
				
				// if it's a permission for "agents perceptions"
				case(PerceptionTypes.AGENTS_PERCEPTION): {
					// obtains the perception of the agents
					AgentsPerception perception = this.produceAgentsPerception(allowed_perceptions[i].getLimitations());
					
					// if the perception is valid, adds it to the produced perceptions
					if(perception != null) perceptions.add(perception);
					
					break;
				}
				
				// if it's a permission for "stigmas perceptions"
				case(PerceptionTypes.STIGMAS_PERCEPTION): {
					// obtains the perceptions of the stigmas
					StigmasPerception perception = this.produceStigmasPerception(allowed_perceptions[i].getLimitations());
					
					// if the perception is valid, adds it to the produced perceptions
					if(perception != null) perceptions.add(perception);
					
					break;
				}
				
				// if it's a permission for "broadcasted messages perceptions"
				case(PerceptionTypes.BROADCAST_PERCEPTION): {
					// obtains the perceptions of the messages
					BroadcastPerception[] broadcast_perceptions = this.produceBroadcastPerceptions(allowed_perceptions[i].getLimitations());
					
					// adds each one to the produced perceptions
					for(int j = 0; j < broadcast_perceptions.length; j++)
						perceptions.add(broadcast_perceptions[j]);
						
					break;					
				}
				
				// developer: new perceptions must add code here
			}			
		}
		
		// configures the amount of stamina to be spent with perceptions
		if(this.stamina_robot != null)
			this.stamina_robot.setPerceptions_spent_stamina(this.spent_stamina);
		else if(stamina_coordinator != null)
			stamina_coordinator.setPerceptionsSpentStamina(this.agent, this.spent_stamina);
		
		// mounts and returns the answer
		Perception[] answer = new Perception[perceptions.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = perceptions.get(i);
		return answer;
	}
	
	/** Obtains the graph perception for the agent, given the eventual limitations.
	 * 
	 *  @param limitations The limitations to the perception of the graph.
	 *  @return The perception of the graph.
	 *  @developer New Limitation classes must change this method. */
	private GraphPerception produceGraphPerception(Limitation[] limitations) {
		// holds an eventual depth limitation
		int depth = -1;
		
		// holds an eventual stamina limitation
		double stamina = 0;
		
		// for each limitation, tries to set the depth and stamina limitations
		for(int i = 0; i < limitations.length; i++) {
			if(limitations[i] instanceof DepthLimitation)
				depth = ((DepthLimitation) limitations[i]).getDepth();
			else if(limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}
		
		// if there's enough stamina to perceive
		if(this.agent.getStamina() > this.spent_stamina + stamina) {
			// atualizes the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;
			
			// returns the perceived subgraph
			return new GraphPerception(simulator.getEnvironment().getGraph().getVisibleEnabledSubgraph(this.agent.getVertex(), depth));
		}
		
		// default answer
		return null;	
	}
	
	/** Obtains the perception of other agents for the agent,
	 *  given the eventual limitations.
	 * 
	 *  @param limitations The limitations to the perception of agents.
	 *  @return The perception of the other agents of the simulation.
	 *  @developer New Limitation classes must change this method. */
	private AgentsPerception produceAgentsPerception(Limitation[] limitations) {
		// holds an eventual depth limitation
		int depth = -1;
		
		// holds an eventual stamina limitation
		double stamina = 0;
		
		// for each limitation, tries to set the depth and stamina limitations
		for(int i = 0; i < limitations.length; i++) {
			if(limitations[i] instanceof DepthLimitation)
				depth = ((DepthLimitation) limitations[i]).getDepth();
			else if(limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}
		
		// if there's enough stamina to perceive
		if(this.agent.getStamina() > this.spent_stamina + stamina) {
			// atualizes the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;
			
			// holds the perceived agents
			List<Agent> perceived_agents = new LinkedList<Agent>();
			
			// obtains the visible subgraph
			// with the given depth
			Graph subgraph = simulator.getEnvironment().getGraph().getVisibleEnabledSubgraph(this.agent.getVertex(), depth);
			
			// obtains the societies of the simulation
			Society[] societies = simulator.getEnvironment().getSocieties();
			
			// for each society
			for(int i = 0; i < societies.length; i++) {
				// obtains its agents
				Agent[] agents = societies[i].getAgents();
				
				// for each agent
				for(int j = 0; j < agents.length; j++)
					// if the current agent is not the one that's perceiving
					if(!this.agent.equals(agents[j])) {
						// obtains the vertex that the current agent comes from
						Vertex vertex = agents[j].getVertex();
						
						// obtains the edge where the agent is
						Edge edge = agents[j].getEdge();
						
						// if the obtained vertex and edge are part of the subgraph
						if(subgraph.hasVertex(vertex) && (edge == null || subgraph.hasEdge(edge)))
							// adds the current agent to the perceived ones
							perceived_agents.add(agents[j]);
					}
			}
			
			// return the perceived agents
			Agent[] perceived_agents_array = new Agent[perceived_agents.size()];
			for(int i = 0; i < perceived_agents_array.length; i++)
				perceived_agents_array[i] = perceived_agents.get(i);			
			return new AgentsPerception(perceived_agents_array);
		}
		
		// default answer
		return null;		
	}
	
	/** Obtains the perception of the stigmas deposited on
	 *  the graph of the simulation, given the eventual limitations.
	 * 
	 *  @param limitations The limitations to the perception of stigmas.
	 *  @return The perception of the stigmas deposited on the graph.
	 *  @developer New Limitation classes must change this method. */
	private StigmasPerception produceStigmasPerception(Limitation[] limitations) {
		// holds an eventual depth limitation
		int depth = -1;
		
		// holds an eventual stamina limitation
		double stamina = 0;
		
		// for each limitation, tries to set the depth and stamina limitations
		for(int i = 0; i < limitations.length; i++) {
			if(limitations[i] instanceof DepthLimitation)
				depth = ((DepthLimitation) limitations[i]).getDepth();
			else if(limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}
		
		// if there's enough stamina to perceive
		if(this.agent.getStamina() > this.spent_stamina + stamina) {
			// atualizes the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;
			
			// obtains the visible subgraph
			// with the given depth
			Graph subgraph = simulator.getEnvironment().getGraph().getVisibleEnabledSubgraph(this.agent.getVertex(), depth);
			
			// obtains the stigmas of the subgraph
			Stigma[] stigmas = subgraph.getStigmas();
			
			// returns the perceived stigmas			
			return new StigmasPerception(stigmas);
		}
		
		// default answer
		return null;		
	}
	
	/** Obtains the perception of broadcasted messages,
	 *  given the eventual limitations.
	 * 
	 *  @param limitations The limitations to the perception of broadcasted messages.
	 *  @return The perception of broadcasted messages.
	 *  @developer New Limitation classes must change this method. */
	private BroadcastPerception[] produceBroadcastPerceptions(Limitation[] limitations) {
		// holds the produced broadcast perceptions
		LinkedList<BroadcastPerception> broadcast_perceptions = new LinkedList<BroadcastPerception>();
		
		// holds an eventual stamina limitation
		double stamina = 0;
		
		// for each limitation, tries to set the stamina limitation
		for(int j = 0; j < limitations.length; j++)
			if(limitations[j] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[j]).getCost();
			// developer: new limitations can change the code here
		
		// for each received message
		if(this.received_messages != null)
			while(this.received_messages.getSize() > 0) {
				// obtains the current message
				String message = this.received_messages.remove();
				
				// if the agent has enough stamina to perceive
				if(this.agent.getStamina() > this.spent_stamina + stamina) {
					// atualizes the spent stamina
					this.spent_stamina = this.spent_stamina + stamina;
					
					// adds a new broadcast perception to the produced ones
					broadcast_perceptions.add(new BroadcastPerception(message));
				}
			}
		
		// mounts the answer of the method
		BroadcastPerception[] answer = new BroadcastPerception[broadcast_perceptions.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = broadcast_perceptions.get(i);
		
		// returns the answer
		return answer;
	}
	
	public void start(int local_socket_number) throws SocketException {
		super.start(local_socket_number);
		
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Started working.");
	}
	
	public void stopWorking() {
		super.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Stopped working.");
	}
	
	/** @modeller This method must be modelled. */		
	public void act(int time_gap) {
		// if the daemon can produce perceptions at the moment
		if(this.can_work) {
			// registers if some perception was succesfully sent
			boolean sent_succesfully = false;			
			
			// obtains all the perceptions the agent is supposed to have at the moment
			Perception[] perceptions = this.producePerceptions();
			
			// for each perception, sends it to the remote agent
			for(int i = 0; i < perceptions.length; i++)
				try {
					sent_succesfully = this.connection.send(perceptions[i].fullToXML(0));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			
			// if the perceptions were succesfully sent,
			// changes the agent's state to JUST_PERCEIVED
			if(sent_succesfully)
				this.agent.setState(AgentStates.JUST_PERCEIVED);
		}
	}
}