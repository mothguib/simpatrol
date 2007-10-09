package model.metric;

import model.graph.DynamicVertex;
import model.graph.Vertex;

public final class MaxIdlenessMetric extends IntegralMetric {
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param value The initial value of the metric. */
	public MaxIdlenessMetric(double value) {
		super("mean idleness", value);
	}
	
	@Override
	public double getValue() {
		return this.previous_value;
	}
	
	public void act(int time_gap) {
		// obtains the vertexes of the graph of the simulation
		Vertex[] vertexes = simulator.getEnvironment().getGraph().getVertexes();
		
		// for each vertex, checks if its idleness is the biggest one,
		// if the vertex is enabled
		for(int i = 0; i < vertexes.length; i++)
			if(!(vertexes[i] instanceof DynamicVertex) || ((DynamicVertex) vertexes[i]).isEnabled()) {
				int idleness = vertexes[i].getIdleness();
				if(idleness > this.previous_value) this.previous_value = idleness;
			}
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the buffer
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<metric type=\"" + MetricTypes.MAX_IDLENESS +
				      "\" value=\"" + this.getValue() +
				      "\"/>\n");
		
		// returns the answer
		return buffer.toString();
	}
}
