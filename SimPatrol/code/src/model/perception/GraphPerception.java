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
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identationand opens the "perception" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<perception>\n");
		
		// puts the graph
		buffer.append(this.graph.toXML(identation + 1));
		
		// applies the identationand opens the "perception" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</perception>\n");
		
		// returns the answer
		return buffer.toString();
	}	
}
