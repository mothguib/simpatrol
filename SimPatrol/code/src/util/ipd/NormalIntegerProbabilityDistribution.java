/* NormalIntegerProbabilityDistribution.java */

/* The package of this class. */
package util.ipd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Normal;

/** Implements normal probability distributions of obtaining integer values. */
public class NormalIntegerProbabilityDistribution extends IntegerProbabilityDistribution {
	/* Attributes. */
	/** The mean of the normal function. */
	private int mean;
	
	/** The standard deviation of the normal function. */
	private double standard_deviation;
	
	/* Methods. */	
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param mean The mean of the normal function.
	 *  @param standard_deviation The standard deviation of the normal function. */
	public NormalIntegerProbabilityDistribution(int seed, int mean, double standard_deviation) {
		super(seed);
		this.mean = mean;
		this.standard_deviation = standard_deviation;

		// never forget to instantiate this.rn_distributor!!!
		this.rn_distributor = new Normal(this.mean, this.standard_deviation, this.rn_generator); 
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
				      "\" type=\"" + IntegerProbabilityDistributionTypes.NORMAL +
				      "\">\n");
		
		// puts the mean value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.mean + "\"/>\n");
		
		// puts the standard deviation value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.standard_deviation + "\"/>\n");
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		buffer.append("<pd>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
}
