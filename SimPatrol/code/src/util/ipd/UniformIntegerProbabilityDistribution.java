/* UniformIntegerProbabilityDistribution.java */

/* The package of this class. */
package util.ipd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/** Implements uniform probability distributions of obtaining integer values. */
public class UniformIntegerProbabilityDistribution extends IntegerProbabilityDistribution {
	/* Attributes. */
	/** The smallest value the distribution can obtain. */
	private int smallest_value;
	
	/** The biggest value the distribution can obtain. */
	private int biggest_value;
	
	/** The probability of obtaining the values.
	 *  Its value must belong to the interval [0,1].  */
	private double probability;
	
	/* Methods. */
	
	public UniformIntegerProbabilityDistribution(int seed, int smallest_value, int biggest_value, double probability) {
		super(seed);
		this.smallest_value = smallest_value;
		this.biggest_value = smallest_value;
		this.probability = probability;
		
		double[] rolet = new double[this.biggest_value - this.smallest_value + 1];
		for(int i = 0; i < rolet.length; i++)
			rolet[i] = this.probability;

		// never forget to instantiate this.rn_distributor!!!
		this.rn_distributor = new EmpiricalWalker(rolet, Empirical.NO_INTERPOLATION, this.rn_generator);
	}
	
	public int nextInt() {
		return super.nextInt() + this.smallest_value;
	}

	public String toXML(int identation) {		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// partially fills the buffer 
		buffer.append("<ipd id=\"" + this.getObjectId() + 
				      "\" seed=" + this.seed +
				      "\" type=" + IntegerProbabilityDistributionTypes.UNIFORM + 
				      "\">\n");
		
		// puts the smallest value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.smallest_value + "\"/>\n");		

		// puts the biggest value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.biggest_value + "\"/>\n");		
		
		// puts the probability value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.probability + "\"/>\n");
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		buffer.append("</ipd>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
}
