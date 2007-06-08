/* Agent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;
import model.graph.Vertex;
import model.graph.Edge;
import model.perception.Perception;

/** Implements the internal agents of SimPatrol. */
public abstract class Agent extends Thread implements XMLable {
	/* Attributes. */
	/** Registers if the agent shall stop working. */
	private boolean stop_working;
	
	/** The object id of the vertex.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The state of the agent. */
	private int state;
	
	/** The vertex that the agent comes from. */
	private Vertex vertex;

	/** The edge where the agent is. */
	private Edge edge;
	
	/** Registers where the agent is on the edge,
	 *  i. e. how much of the edge remains for
	 *  the agent to pass through it. */
	private double elapsed_length;
	
	/** The stamina of the agent.
	 *  Its default value is 1.0. */
	private double stamina = 1.0;
	
	/* Methods. */
	/** Constructor.
	 *  @param vertex The vertex that the agent comes from. */
	public Agent(Vertex vertex) {
		this.vertex = vertex;
		
		this.state = AgentStates.JUST_ACTED; // the agent is ready to perceive!
		this.edge = null;
		this.elapsed_length = 0;
		
		this.stop_working = false; // thread control
	}
	
	/**
	 * @param perception
	 * @model.uin <code>design:node:::4nanf17ujey8-fenk9p:i172kf17ujey8agupu8</code>
	 */
	public void setPerception(Perception perception) {
		// TODO implementar!!!
	}

	/**
	 * @param requisition
	 * @model.uin <code>design:node:::dlwtnf17ujey8bzkqn9:i172kf17ujey8agupu8</code>
	 */
	public void requireBroadcastingPercetion(String requisition) {
		// TODO implementar!!!
	}
		
	/** Returns the agent state.
	 *  @return The state of the agent. */
	public int getAgentState() {
		return this.state;
	}
	
	/** Indicates that the agent must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Configures the state of the agent.
	 *  @param state The state of the agent. 
	 *  @see AgentStates */	
	public void setState(int state) {
		this.state = state;
	}
	
	/** Configures the edge of the agent, as well as its position on it.
	 *  @param edge The edge of the agent.
	 *  @param elapsed_length Where the agent os on the edge */	
	public void setEdge(Edge edge, int elapsed_length) {
		this.edge = edge;
		this.elapsed_length = elapsed_length;
	}
	
	/** Configures the stamina of the agent.
	 *  @param stamina The stamina of the agent. */	
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}
	
	public void run() {
		while(!this.stop_working) {
			// TODO implementar... o que está abaixo é temporário!!!
			int temp_perc = (int)(Math.pow(Math.random() * 10000, 2));			
			for(int i = 0; i < temp_perc; i++);			
			this.state = AgentStates.JUST_ACTED;
			int temp_act = (int)(Math.pow(Math.random() * 10000, 2));
			for(int i = 0; i < temp_act; i++);
			this.state = AgentStates.JUST_ACTED;
		}
	}
		
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<agent id=\"" + this.id +
				      "\" state=\"" + this.state +
				      "\" vertex_id=\"" + this.vertex.getObjectId() +
				      "\" edge_id=\"");
		
		if(this.edge != null) buffer.append(this.edge.getObjectId());
		else buffer.append(this.edge);
		
		buffer.append("\" elapsed_length=\"" + this.elapsed_length +
				      "\" stamina=\"" + this.stamina +
				      "\"/>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}	
}