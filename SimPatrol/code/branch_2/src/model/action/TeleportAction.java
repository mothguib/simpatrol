/* TeleportAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.graph.Edge;
import model.graph.Vertex;
import model.interfaces.Visible;
import model.limitation.DepthLimitation;

/** Implements the action of teleporting an agent.
 * 
 *  Its effect can be controlled by depth and stamina limitations. 
 *  @see DepthLimitation
 *  @see StaminaLimitation */
public class TeleportAction extends AtomicAction {
	/* Attributes. */
	/** The new vertex the agent is coming from, after the teleport. */
	private Vertex vertex;
	
	/** The edge the agent is, after the teleport. */
	private Edge edge;
	
	/** Registers where the agent is on the edge,
	 *  i.e. how much of the edge remains for
	 *  the agent to pass through it,
	 *  after the teleport. */
	private double elapsed_length;
	
	/** The set of objects that shall become visible as a result of
	 *  the teleport. */
	private Set<Visible> visible_objects;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param vertex The vertex th agent is coming from, after the teleport.
	 *  @param edge The edge the agent is, after the teleport.
	 *  @param elapsed_length How much of the edge remains for the agent to trespass it. */
	public TeleportAction(Vertex vertex, Edge edge, double elapsed_length) {
		this.vertex = vertex;
		this.edge = edge;
		this.elapsed_length = elapsed_length;
		this.visible_objects = null;
	}
	
	/** Constructor.
	 * 
	 *  @param vertex The vertex th agent is coming from, after the teleport.
	 *  @param edge The edge the agent is, after the telepor.
	 *  @param elapsed_length How much of the edge remains for the agent to trespass it.
	 *  @param visible_objects The objects that shall become visible as a result of the teleport. */
	public TeleportAction(Vertex vertex, Edge edge, double elapsed_length, Visible[] visible_objects) {
		this.vertex = vertex;
		this.edge = edge;
		this.elapsed_length = elapsed_length;
		
		this.visible_objects = new HashSet<Visible>();
		for(int i = 0; i < visible_objects.length; i++)
			this.visible_objects.add(visible_objects[i]);
	}
	
	/** Returns the goal vertex of the teleport.
	 * 
	 *  @return The goal vertex of the teleport. */
	public Vertex getVertex() {
		return this.vertex;
	}
	
	/** Returns the goal edge of the teleport.
	 * 
	 *  @return The goal edge of the teleport. */
	public Edge getEdge() {
		return this.edge;
	}
	
	/** Returns where the agent must be at the edge, after the teleport.
	 * 
	 *  @return The elapsed lenght of the goal edge. */
	public double getElapsed_length() {
		return this.elapsed_length;
	}
	
	/** Assures the visibility of the objects that
	 *  shall become visible as a result of the teleport. */
	public void assureTeleportVisibilityEffect() {
		// assures the visibility of the destiny of the teleport
		this.vertex.setVisibility(true);
		if(this.edge != null) this.edge.setVisibility(true);
		
		// assures the visibility of the other registered objects		
		if(this.visible_objects != null) {
			Object[] visible_objects_array = this.visible_objects.toArray();
			
			for(int i = 0; i < visible_objects_array.length; i++)
				((Visible) visible_objects_array[i]).setVisibility(true);
		}
	}
	
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer	
		buffer.append("<action type=\"" + ActionTypes.TELEPORT_ACTION +
					  "\" vertex_id=\"" + this.vertex.getObjectId() +
					  "\" edge_id=\"" + this.edge.getObjectId() +
				      "\" elapsed_length=\"" + this.elapsed_length +
				      "\"/>\n");
		
		// returns the answer
		return buffer.toString();
	}
}