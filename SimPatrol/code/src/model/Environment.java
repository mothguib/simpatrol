/* Environment.java */

/* The package of this class. */
package model;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.agent.Society;
import model.graph.Graph;
import model.interfaces.XMLable;

/** Implements the environment where the patrolling task is executed. */
public final class Environment implements XMLable {
	/* Atributes. */
	/** The graph of the simulation. */
	private Graph graph;
	
	/** The set of societies of agents involved with the simulation. */
	private Set<Society> societies;
	
	/* Methods. */
	/** Constructor.
	 *  @param graph The graph to be pattroled.
	 *  @param societies The societies of patrollers of the simulation. */
	public Environment(Graph graph, Society[] societies) {
		this.graph = graph;
		
		this.societies = new HashSet<Society>();
		for(int i = 0; i < societies.length; i++)
			this.societies.add(societies[i]);
	}
	
	/** Returns the societies of the environment.
	 *  @return The societies. */
	public Society[] getSocieties() {
		Object[] societies_array = this.societies.toArray();		
		Society[] answer = new Society[societies_array.length];
		
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Society) societies_array[i];
		
		return answer;
	}
	
	/** Returns the graph of the environment.
	 *  @return The graph. */
	public Graph getGraph() {
		return this.graph;
	}
	
	/** Obtains the XML version of this environment at the current moment.
	 *  @param identation The identation to organize the XML. 
	 *  @param current_time The current time, measured in cycles or in seconds.
	 *  @return The XML version of this environment at the current moment. */	
	public String toXML(int identation, int current_time) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and opens the "environment" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<environment>\n");
		
		// fills the buffer with the graph
		buffer.append(this.graph.toXML(identation + 1, current_time));
		
		// fills the buffer with the societies
		Object[] societies_array = this.societies.toArray();
		for(int i = 0; i < societies_array.length; i++)
			buffer.append(((Society) societies_array[i]).toXML(identation + 1));
		
		// applies the identation and closes the "environment" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</environment>\n");
		
		// returns the buffer content
		return buffer.toString();		
	}
	
	/** Give preference to use this.toXML(int identation, int current_time) 
	 * @deprecated */
	public String toXML(int identation) {
		return this.toXML(identation, (int) (System.currentTimeMillis() / 1000));
	}
	
	public String getObjectId() {
		// an environment doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an environment doesn't need an id
		// so, do nothing
	}
}