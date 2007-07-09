/* Agent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.interfaces.XMLable;
import model.graph.Vertex;
import model.graph.Edge;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/** Implements the internal agents of SimPatrol. */
public abstract class Agent implements XMLable {
	/* Attributes. */
	/** The object id of the agent.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The label of the agent. */
	protected String label;
	
	/** The state of the agent.
	 * 
	 *  @see AgentStates */
	private int state;
	
	/** The vertex that the agent comes from. */
	protected Vertex vertex;

	/** The edge where the agent is. */
	private Edge edge;
	
	/** Registers where the agent is on the edge,
	 *  i.e. how much of the edge remains for
	 *  the agent to pass through it. */
	private double elapsed_length;
	
	/** The stamina of the agent.
	 *  Its default value is 1.0. */
	private double stamina = 1.0;
	
	/** The set of allowed perceptions. */
	protected Set<PerceptionPermission> allowed_perceptions;
	
	/** The set of allowed actions. */
	protected Set<ActionPermission> allowed_actions;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param label The label of the agent.
	 *  @param vertex The vertex that the agent comes from.
	 *  @param allowed_perceptions The allowed perceptions to the agent.
	 *  @param allowed_actions The allowed actions to the agent. */
	public Agent(String label, Vertex vertex, PerceptionPermission[] allowed_perceptions, ActionPermission[] allowed_actions) {
		this.label = label;
		this.vertex = vertex;
		
		this.state = AgentStates.JUST_ACTED; // the agent is ready to perceive!
		this.edge = null;
		this.elapsed_length = 0;
		
		if(allowed_perceptions != null && allowed_perceptions.length > 0) {
			this.allowed_perceptions = new HashSet<PerceptionPermission>();
			for(int i = 0; i < allowed_perceptions.length; i++)
				this.allowed_perceptions.add(allowed_perceptions[i]);
		}
		else this.allowed_perceptions = null;
		
		if(allowed_actions != null && allowed_actions.length > 0) {
			this.allowed_actions = new HashSet<ActionPermission>();
			for(int i = 0; i < allowed_actions.length; i++)
				this.allowed_actions.add(allowed_actions[i]);
		}
		else this.allowed_actions = null;
	}
	
	/** Returns the state of the agent.
	 * 
	 *  @return The state of the agent.
	 *  @see AgentStates */
	public int getAgentState() {
		return this.state;
	}
	
	/** Configures the state of the agent.
	 * 
	 *  @param state The state of the agent.
	 *  @see AgentStates */
	public void setState(int state) {
		this.state = state;
	}
	
	/** Configures the edge of the agent, as well as its position on it.
	 * 
	 *  @param edge The edge of the agent.
	 *  @param elapsed_length Where the agent is on the edge. */	
	public void setEdge(Edge edge, int elapsed_length) {
		this.edge = edge;
		this.elapsed_length = elapsed_length;
	}
	
	/** Configures the stamina of the agent.
	 * 
	 *  @param stamina The stamina of the agent. */	
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}
	
	/** Returns the stamina of the agent.
	 * 
	 *  @return The stamina of the agent. */
	public double getStamina() {
		return this.stamina;
	}
	
	/** Decrements the stamina of the agent by the given factor.
	 * 
	 *  @param factor The factor to be decremented from the value of stamina. */
	public void decStamina(double factor) {
		this.stamina = this.stamina - factor;
	}
	
	/** Returns the vertex that the agent comes from.
	 * 
	 *  @return The vertex that agent comes from. */
	public Vertex getVertex() {
		return this.vertex;
	}
	
	/** Returns the allowes perceptions for the agent.
	 * 
	 *  @return The permissions of perceptions. */
	public PerceptionPermission[] getAllowedPerceptions() {
		PerceptionPermission[] answer = new PerceptionPermission[0];
		
		if(this.allowed_perceptions != null) {
			Object[] allowed_perceptions_array = this.allowed_perceptions.toArray();
			answer = new PerceptionPermission[allowed_perceptions_array.length];
			for(int i = 0; i < answer.length; i++)
				answer[i] = (PerceptionPermission) allowed_perceptions_array[i];
		}
		
		return answer;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<agent id=\"" + this.id +
					  "\" label=\"" + this.label +
				      "\" state=\"" + this.state +
				      "\" vertex_id=\"" + this.vertex.getObjectId());
		
		if(this.edge != null) {
			buffer.append("\" edge_id=\"" + this.edge.getObjectId() +
					      "\" elapsed_length=\"" + this.elapsed_length);
		}

		buffer.append("\" stamina=\"" + this.stamina);
		
		// puts the eventual allowed perceptions
		if(this.allowed_perceptions != null) {
			buffer.append(">\n");
			
			Object[] allowed_perceptions_array = this.allowed_perceptions.toArray();
			for(int i = 0; i < allowed_perceptions_array.length; i++)
				buffer.append(((PerceptionPermission) allowed_perceptions_array[i]).toXML(identation + 1));
		}
		
		// puts the eventual allowed actions
		if(this.allowed_actions != null) {
			if(allowed_perceptions == null) buffer.append(">\n");
			
			Object[] allowed_actions_array = this.allowed_actions.toArray();
			for(int i = 0; i < allowed_actions_array.length; i++)
				buffer.append(((ActionPermission) allowed_actions_array[i]).toXML(identation + 1));
		}
		
		// closes the main tag
		if(this.allowed_perceptions == null && this.allowed_actions == null)
			buffer.append("\"/>\n");
		else {
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</agent>\n");
		}
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public boolean equals(Object object) {
		if(object instanceof XMLable)
			return this.id.equals(((XMLable) object).getObjectId());
		else return super.equals(object);
	}
	
	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}