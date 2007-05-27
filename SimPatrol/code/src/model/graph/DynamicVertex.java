/* DynamicVertex.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import util.TimeProbabilityDistribution;

/** Implements dynamic vertexes of a Graph object,
 *  that can appear and disappear with a specific time
 *  probability distribution. */
public class DynamicVertex extends Vertex {
	/* Attributes. */
	/** The time probability distribution for the vertex appearing. */
	private TimeProbabilityDistribution appearing_pd;

	/** The time probability distribution for the vertex disappearing. */
	private TimeProbabilityDistribution disappearing_pd;
	
	/* Methods. */
	/** Constructor.
	 * @param label The label of the vertex.
	 * @param appearing_pd The time probability distribution for the vertex appearing.
	 * @param diappearing_pd The time probability distribution for the vertex disappearing. */	
	public DynamicVertex(String label, TimeProbabilityDistribution appearing_pd, TimeProbabilityDistribution disappearing_pd) {
		super(label);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = disappearing_pd;
	}
}