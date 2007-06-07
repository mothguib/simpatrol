/* IntegerProbabilityDistribution.java */

/* The package of this class. */
package util.ipd;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.engine.MersenneTwister;

/** Implements probability distributions of obtaining integer values. */
public abstract class IntegerProbabilityDistribution implements XMLable {
	/* Attributes. */
	/** The id of the object.
	 *  Not part of the patrol problem modelling. */
	private String id;	
	
	/** The seed for the random number generator. */
	protected int seed;
	
	/** The generator of random numbers. */
	protected MersenneTwister rn_generator;
	
	/** The random numbers distributor.  */
	protected AbstractDistribution rn_distributor;
	
	/* Methods. */
	/** Constructor.
	 *  @param seed The seed for the random number generator. */
	public IntegerProbabilityDistribution(int seed) {
		this.seed = seed;
		this.rn_generator = new MersenneTwister(this.seed);
		
		// never forget to instantiate this.rn_distributor!!!
	}
	
	/** Returns the next integer value, according to the
	 *  random number distribution.
	 *  @return The next random integer value. */
	public int nextInt() {
		return this.rn_distributor.nextInt();
	}
	
	public String getObjectId() {
		return this.id;
	}
	
	public void setObjectId(String object_id) {
		this.id = object_id;
	}	
}