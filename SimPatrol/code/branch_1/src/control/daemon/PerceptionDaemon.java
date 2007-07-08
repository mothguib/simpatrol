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
import model.graph.Graph;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.perception.GraphPerception;
import model.perception.Perception;
import model.perception.PerceptionTypes;
import model.permission.PerceptionPermission;

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
	
	/** The graph of the simulation. */
	private Graph graph;
	
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
	 *  @param graph The graph of the simulation.
	 *  @param cycle_duration The duration, in milliseconds of a cycle of perceptions. */
	public PerceptionDaemon(String thread_name, Agent agent, Graph graph, int cycle_duration) {
		super(thread_name, agent);
		
		this.clock = new Clock(thread_name + "'s clock", this);
		this.clock.setUnity(Calendar.MILLISECOND);
		this.clock.setStep(cycle_duration);		
		
		this.can_produce_perceptions = true;
		this.graph = graph;
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
			return new GraphPerception(this.graph.getVisibleSubgraph(this.agent.getVertex(), depth));
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
			
			// for each perception, send it to the remote agent
			for(int i = 0; i < perceptions.length; i++)
				try { this.connection.send(perceptions[i].toXML(0)); }
				catch (IOException e) { e.printStackTrace(); }			
		}
	}
}