/* MeanIdlenessMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.graph.DynamicVertex;
import model.graph.Vertex;

/** Implements the metric that collects the current mean idleness
 *  of the graph of the simulation. */
public final class MeanIdlenessMetric extends IntegralMetric {
	/* Attributes. */
	/** Counts how many times the value of the metric is being calculated. */
	private int collectings_count;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param value The initial value of the metric. */
	public MeanIdlenessMetric(double value) {
		super("mean idleness", value);
		this.collectings_count = 1;
	}
	
	@Override
	public double getValue() {
		return this.previous_value * Math.pow(this.collectings_count, -1);
	}
	
	@Override
	public int getType() {
		return MetricTypes.MEAN_IDLENESS;
	}
	
	public void act(int time_gap) {
		// increases the collecting_count attribute
		this.collectings_count++;
		
		// holds the sum of all the current idlenesses
		double idlenesses_sum = 0;
		
		// holds how many vertexes are being considered in the mean
		int vertexes_count = 0;
		
		// obtains the vertexes of the graph of the simulation
		Vertex[] vertexes = simulator.getGraph().getVertexes();
		
		// for each appearing vertex, adds its idleness to the sum
		for(int i = 0; i < vertexes.length; i++)
			if(!(vertexes[i] instanceof DynamicVertex) || ((DynamicVertex) vertexes[i]).isAppearing()) {
				idlenesses_sum = idlenesses_sum + vertexes[i].getIdleness();
				vertexes_count++;
			}
		
		// adds the current mean idleness to the previous collected value
		this.previous_value = this.previous_value + idlenesses_sum * Math.pow(vertexes_count, -1);
	}

	public String fullToXML(int identation) {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the buffer
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<metric type=\"" + MetricTypes.MEAN_IDLENESS +
				      "\" value=\"" + this.getValue() +
				      "\"/>\n");
		
		// returns the answer
		return buffer.toString();
	}
}
