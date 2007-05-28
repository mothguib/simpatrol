/* EmpiricalTimeProbabilityDistribution.java */

/* The package of this class. */
package util.tpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/** Implements the probability distributions based on time of simulation
 *  that are given by an empirical method. */
public class EmpiricalTimeProbabilityDistribution extends
		TimeProbabilityDistribution {	
	/* Attribute. */
	/** The discrete empirical distribution.  */
	private double[] distribution;
	
	/** The discrete empirical distributor.  */
	private EmpiricalWalker rn_distributor;
	
	/* Methods. */
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param distribution The empirical distribution. */
	public EmpiricalTimeProbabilityDistribution(int seed, double[] distribution) {
		super(seed);
		this.distribution = distribution;
		this.rn_distributor = new EmpiricalWalker(this.distribution, Empirical.NO_INTERPOLATION, this.rn_generator);
	}
	
	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code!
		this.next_bool_counter++;
		
		if(rn_distributor.nextInt() == next_bool_counter) return true;
		else return false;
	}
}