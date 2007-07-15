/* PerceptionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import util.clock.Clock;
import util.clock.Clockable;
import model.agent.Agent;
import model.agent.Society;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.perception.AgentsPerception;
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
 *  @modelled This class must have its behaviour modelled. */
public final class PerceptionDaemon extends AgentDaemon implements Clockable {
	/* Attributes. */
	/** The clock that controls the daemon's work. */
	private Clock clock;
	
	/** Registers if the daemon can produce perceptions
	 *  at the moment. */
	private boolean can_produce_perceptions;
	
	/* Methods. */
	/** Constructor.
	 * 
	 * 	Doesn't initiate its own connection, as it will be shared with an
	 *  ActionDaemon object. So the connection must be set by the
	 *  setConenction() method.
	 *  @see PerceptionDaemon
	 *  
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param agent The agent whose perceptions are produced.
	 *  @param cycle_duration The duration, in milliseconds of a cycle of perceptions. */
	public PerceptionDaemon(String thread_name, Agent agent, int cycle_duration) {
		super(thread_name, agent);
		
		this.clock = new Clock(thread_name + "'s clock", this);
		this.clock.setUnity(Calendar.MILLISECOND);
		this.clock.setStep(cycle_duration);		
		
		this.can_produce_perceptions = true;
	}
	
	/** Produces all the perceptions an agent is allowed to have at the moment.
	 * 
	 *  @return The perceptions of the agent at the moment.
	 *  @developer New Perception classes must change this method. */
	private Perception[] producePerceptions() {
		// holds the eventually produced perceptions
		List<Perception> perceptions = new LinkedList<Perception>();
		
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
					
					// if the perception is valid, adds itto the produced perceptions
					if(perception != null) perceptions.add(perception);
				}
				
				// TODO completar com outras percepções
				// developer: new perceptions must add code here
			}
		}
		
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
		if(this.agent.getStamina() > stamina) {
			// decrements the agent's stamina
			this.agent.decStamina(stamina);
			
			// return the perceived subgraph
			return new GraphPerception(simulator.getGraph().getVisibleSubgraph(this.agent.getVertex(), depth));
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
		if(this.agent.getStamina() > stamina) {
			// decrements the agent's stamina
			this.agent.decStamina(stamina);
			
			// holds the perceived agents
			List<Agent> perceived_agents = new LinkedList<Agent>();
			
			// obtains the visible subgraph
			// with the given depth
			Graph subgraph = simulator.getGraph().getVisibleSubgraph(this.agent.getVertex(), depth);
			
			// obtains the societies of the simulation
			Society[] societies = simulator.getSocieties();
			
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
		if(this.agent.getStamina() > stamina) {
			// decrements the agent's stamina
			this.agent.decStamina(stamina);
			
			// obtains the visible subgraph
			// with the given depth
			Graph subgraph = simulator.getGraph().getVisibleSubgraph(this.agent.getVertex(), depth);
			
			// obtains the stigmas of the subgraph
			Stigma[] stigmas = subgraph.getStigmas();
			
			// returns the perceived stigmas			
			return new StigmasPerception(stigmas);
		}
		
		// default answer
		return null;		
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		// stops its connection
		this.connection.stopWorking();
		
		// stops its clock
		this.clock.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Stopped working.");
	}
	
	public void start(int local_socket_number) throws SocketException {
		super.start(local_socket_number);
		this.clock.start();
	}
	
	/** @modeller This method must be modelled. */		
	public void act(int time_gap) {
		// if the daemon can produce perceptions at the moment
		if(this.can_produce_perceptions) {
			// obtains all the perceptions the agent is supposed to have at the moment
			Perception[] perceptions = this.producePerceptions();
			
			// for each perception, sends it to the remote agent
			for(int i = 0; i < perceptions.length; i++)
				try { this.connection.send(perceptions[i].fullToXML(0)); }
				catch (IOException e) { e.printStackTrace(); }			
		}
	}
}