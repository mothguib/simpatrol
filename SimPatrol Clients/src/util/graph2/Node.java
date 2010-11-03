package util.graph2;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


/**
 * Nodes (or vertices) of a graph.
 */
public final class Node {
	private int index;

	private String identifier;
	//private String label;

	private double idleness;

	/**
	 * The priority to visit this node. Its default value is ONE.
	 */
	private double priority = 1;

	/**
	 * Expresses if this node is a point of recharging the energy of the
	 * patrollers. Its default value is FALSE.
	 */
	private boolean fuel = false;


	Node(int index, String identifier) {
		this.index = index;
		this.identifier = identifier;
		//this.label = label;
		this.idleness = 0;
	}

	public int getIndex() {
		return index;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
//	public String getLabel() {
//		return this.label;
//	}
	
	public double getPriority() {
		return this.priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public boolean isFuel() {
		return this.fuel;
	}

	public void setFuel(boolean fuel) {
		this.fuel = fuel;
	}

	public void setIdleness(double idleness) {
		this.idleness = idleness;
	}

	public double getIdleness() {
		return this.idleness;
	}

	public boolean equals(Object object) {
		if (this.identifier != null && object instanceof Node) {
			return this.identifier.equals(((Node) object).getIdentifier());
		} else {
			return super.equals(object);
		}
	}
	
	public String toString() {
		return identifier;
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<node id=\"" + this.identifier + "\"" 
						+ " label=\"" + this.identifier + "\"" 
						+ " priority=\"" + this.priority + "\"" 
						+ " visibility=\"true\""
						+ " idleness=\"0\"" 
						+ " fuel=\"" + this.fuel + "\"" 
						+ " is_enabled=\"true\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}
	
}