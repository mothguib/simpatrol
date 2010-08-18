package tools.metrics;

/**
 * Keeps information about a single visit (made by an
 * agent to a node in a specific time).
 * 
 * @author Pablo A. Sampaio
 */
public class Visit {
	public final int time;
	public final int agent;
	public final int vertex;
	
	Visit(int t, int ag, int v) {
		time = t;
		agent = ag;
		vertex = v;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(time); builder.append(' ');
		builder.append(agent); builder.append(' ');
		builder.append(vertex);
		
		return builder.toString();
	}
	
}

