/* MaxInstantaneousIdlenessMetric.java */

/* The package of this class. */
package model.metric;

import model.graph.DynamicVertex;
import model.graph.Vertex;

/** Implements the metric that collects the maximum instantaneous idleness
 *  of the graph of the simulation. */
public final class MaxInstantaneousIdlenessMetric extends Metric {
	/* Methods. */
	@Override
	public double getValue() {
		// holds the biggest of all the current idlenesses
		double max_idleness = -1;
		
		// obtains the vertexes of the graph of the simulation
		Vertex[] vertexes = simulator.getGraph().getVertexes();
		
		// for each vertex, checks if its idleness is the biggest one,
		// if the vertex is appearing
		for(int i = 0; i < vertexes.length; i++)
			if(!(vertexes[i] instanceof DynamicVertex) || ((DynamicVertex) vertexes[i]).isAppearing()) {
				int idleness = vertexes[i].getIdleness();
				if(idleness > max_idleness) max_idleness = idleness;
			}
		
		// returns the biggest idleness
		return max_idleness;
	}
	
	@Override
	public int getType() {
		return MetricTypes.MAX_INSTANTANEOUS_IDLENESS;
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the buffer
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<metric type=\"" + MetricTypes.MAX_INSTANTANEOUS_IDLENESS +
				      "\" value=\"" + this.getValue() +
				      "\"/>\n");
		
		// returns the answer
		return buffer.toString();
	}
}