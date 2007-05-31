/* UniformTimeProbabilityDistribution.java */

/* The package of this class. */
package util.tpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/** Implements the probability distributions based on time of simulation
 *  that are given uniformly by a single probability value. */
public class UniformTimeProbabilityDistribution extends TimeProbabilityDistribution {
	/* Attributes. */
	/** The probability value of happening an associated event.
	 *  Its value must belong to the interval [0,1].  */
	private double probability;
	
	/* Methods. */
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param probability The probability of happening an associated event. */
	public UniformTimeProbabilityDistribution(int seed, double probability) {
		super(seed);
		this.probability = probability;		
		double[] distribution = {Math.abs(1 - this.probability), Math.abs(this.probability)};
		
		// never forget to instantiate this.rn_distributor!!!
		this.rn_distributor = new EmpiricalWalker(distribution, Empirical.NO_INTERPOLATION, this.rn_generator);						
	}
	
	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code!
		this.next_bool_counter++;
		
		if(this.rn_distributor.nextInt() == 0) return false;
		else return true;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// partially fills the buffer 
		buffer.append("<tpd id=\"" + this.getObjectId() + 
				      "\" seed=" + this.seed +
				      "\" next_bool_count=" + this.next_bool_counter +
				      "\" type=" + TimeProbabilityDistributionTypes.UNIFORM +
				      "\">\n");
		
		// puts the probability value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<tpd_parameter value=\"" + this.probability + "\"/>\n");
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		buffer.append("</tpd>\n");
		
		// returns the buffer content
		return buffer.toString();		
	}
}