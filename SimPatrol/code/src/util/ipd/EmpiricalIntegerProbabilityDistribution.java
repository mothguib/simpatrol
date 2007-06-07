/* EmpiricalIntegerProbabilityDistribution.java */

/* The package of this class. */
package util.ipd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/** Implements empirical probability distributions of obtaining integer values. */
public class EmpiricalIntegerProbabilityDistribution extends IntegerProbabilityDistribution {
	/* Attributes. */
	/** The discrete empirical distribution.  */
	private double[] distribution;
	
	/** The smallest integer value the distribution can obtain. */
	private int smallest_value;
	
	/* Methods. */	
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param distribution The empirical distribution.
	 *  @param smallest_value The smallest integer value the distribution can obtain. */
	public EmpiricalIntegerProbabilityDistribution(int seed, double[] distribution, int smallest_value) {
		super(seed);
		this.distribution = distribution;
		this.smallest_value = smallest_value;

		// never forget to instantiate this.rn_distributor!!!
		this.rn_distributor = new EmpiricalWalker(this.distribution, Empirical.NO_INTERPOLATION, this.rn_generator);
	}
	
	public int nextInt() {
		return super.nextInt() + this.smallest_value;
	}

	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// partially fills the buffer 
		buffer.append("<ipd id=\"" + this.getObjectId() +
				      "\" seed=\"" + this.seed +
				      "\" type=\"" + IntegerProbabilityDistributionTypes.EMPIRICAL +
				      "\">\n");
		
		// puts the smallest value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.smallest_value + "\"/>\n");
		
		// completes the buffer content
		for(int i = 0; i < this.distribution.length; i++) {
			// applies the identation
			for(int j = 0; j < identation + 1; j++)
				buffer.append("\t");
			
			buffer.append("<pd_parameter value=\"" + this.distribution[i] + "\"/>\n");
		}
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		buffer.append("</ipd>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
}
