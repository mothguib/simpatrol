package util.graph2;


//TODO: rever nomes, etc.

/**
 * Represents the directed edge "source -> target".
 *   
 * @author Pablo
 */
public class Edge {
	private String identifier;
	
	private int sourceIndex;
	private int targetIndex;
	private double length;
	
	
	public Edge(String ident, int source, int target, double weight) {
		identifier = ident;
		sourceIndex = source;
		targetIndex = target;
		length = weight;
	}
	
	public Edge(int source, int target) {
		identifier  = "";
		sourceIndex = source;
		targetIndex = target;
		length      = 1.0d;
	}

	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public int getSourceIndex() {
		return sourceIndex;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public double getLength() {
		return length;
	}
	
	public boolean equalsIgnoreDirection(Edge e) {
		return ((this.sourceIndex == e.sourceIndex && this.targetIndex == e.targetIndex) 
					|| (this.sourceIndex == e.targetIndex && this.targetIndex == e.sourceIndex));
	}

	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		
		Edge e = (Edge)o;
		
		return (this.sourceIndex == e.sourceIndex
				&& this.targetIndex == e.targetIndex);
	}
	
	public String toString() {
		//return "(" + this.head + "," + this.tail + ")";
		return "(" + this.sourceIndex + "," + this.targetIndex + ") c=" + this.length;
	}
	
}
