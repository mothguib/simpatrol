/* SpecificEventTimeProbabilityDistribution.java */

/* The package of this class. */
package util.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/** Implements the probability distributions of happening
 *  an event based on a specific time of the simulation. */
public class SpecificEventTimeProbabilityDistribution extends EventTimeProbabilityDistribution {
	/* Attributes. */
	/** The probability value of happening an associated event.
	 *  Its value must belong to the interval [0,1].  */
	private double probability;
	
	/** The specific time when the dices must be thrown. */
	private int time;
	
	/* Methods. */
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param probability The probability of happening an associated event.
	 *  @param time The specific time when the dices must be thrown. */
	public SpecificEventTimeProbabilityDistribution(int seed, double probability, int time) {
		super(seed);		
		this.seed = seed;
		this.probability = probability;
		this.time = time;
		double[] distribution = {Math.abs(1 - this.probability), Math.abs(this.probability)};
		
		// never forget to instantiate this.rn_distributor!!!
		this.rn_distributor = new EmpiricalWalker(distribution, Empirical.NO_INTERPOLATION, this.rn_generator);						
	}
		
	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code!
		this.next_bool_counter++;
		
		int random_value = this.rn_distributor.nextInt();
		if(this.next_bool_counter == this.time)
			if(random_value == 1)
				return true;

		return false;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// partially fills the buffer 
		buffer.append("<etpd id=\"" + this.getObjectId() + 
				      "\" seed=" + this.seed +
				      "\" next_bool_count=" + this.next_bool_counter +
				      "\" type=" + EventTimeProbabilityDistributionTypes.SPECIFIC +
				      "\">\n");
		
		// puts the probability value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");		
		buffer.append("<pd_parameter value=\"" + this.probability + "\"/>\n");
		
		// puts the time value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");		
		buffer.append("<pd_parameter value=\"" + this.time + "\"/>\n");
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		buffer.append("</etpd>\n");
		
		// returns the buffer content
		return buffer.toString();		
	}
}
