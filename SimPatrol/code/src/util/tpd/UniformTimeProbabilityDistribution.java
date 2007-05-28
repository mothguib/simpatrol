/* UniformTimeProbabilityDistribution.java */

/* The package of this class. */
package util.tpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/** Implements the probability distributions based on time of simulation
 *  that are given uniformly by a single probability value. */
public class UniformTimeProbabilityDistribution extends
		TimeProbabilityDistribution {
	/* Attributes. */
	/** The probability value of happening an associated event.
	 *  Its value must belong to the interval [0,1].  */
	private double probability;
	
	/** A discrete empirical distributor.  */
	private EmpiricalWalker rn_distributor;
	
	/* Methods. */
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param probability The probability of happening an associated event. */
	public UniformTimeProbabilityDistribution(int seed, double probability) {
		super(seed);
		this.probability = probability;		
		double[] distribution = {Math.abs(1 - this.probability), Math.abs(this.probability)};
		this.rn_distributor = new EmpiricalWalker(distribution, Empirical.NO_INTERPOLATION, this.rn_generator);						
	}
	
	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code!
		this.next_bool_counter++;
		
		if(this.rn_distributor.nextInt() == 0) return false;
		else return true;
	}
}