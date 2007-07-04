/* GraphPerception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import model.graph.Graph;

/** Implements The perceptions of the graph of the simulation. */
public class GraphPerception extends ReactivePerception {
	/* Attributes. */
	/** The perceived graph. */
	private Graph graph;
	
	/* Methods. */
	/* Constructor.
	 * @param graph The perceived graph. */
	public GraphPerception(Graph graph) {
		this.graph = graph;
	}
	
	/** Obtains the XML version of this perception at the current moment.
	 *  @param identation The identation to organize the XML. 
	 *  @param current_time The current time, measured in cycles or in seconds.
	 *  @return The XML version of this perception at the current moment. */	
	public String toXML(int identation, int current_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identationand opens the "perception" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<perception>\n");
		
		// puts the graph
		buffer.append(this.graph.toXML(identation + 1, current_time));
		
		// applies the identationand opens the "perception" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</perception>\n");
		
		// returns the answer
		return buffer.toString();
	}
	
	/** Give preference to use this.toXML(int identation, int current_time) 
	 * @deprecated */
	public String toXML(int identation) {
		return this.toXML(identation, 0);
	}	
}
