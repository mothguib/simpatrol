/* EventTimeProbabilityDistribution.java */

/* The package of this class. */
package model.etpd;

/* Imported classes and/or interfaces. */
import view.XMLable;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.engine.MersenneTwister;

/**
 * Implements the probability distributions of happening an event, based on the
 * time of simulation.
 * 
 * @developer New ETPD subclasses should override some methods of this class.
 */
public abstract class EventTimeProbabilityDistribution implements XMLable {
	/* Attributes. */
	/** The seed for the random number generator. */
	protected int seed;

	/** The generator of random numbers. */
	protected MersenneTwister rn_generator;

	/** The random numbers distributor. */
	protected AbstractDistribution rn_distributor;

	/**
	 * Counts how many times the method nextBoolean() was called.
	 */
	protected int next_bool_counter;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @developer This method must be overridden, in order to the rn_distributor
	 *            attribute be instantiated.
	 */
	public EventTimeProbabilityDistribution(int seed) {
		this.seed = seed;
		this.rn_generator = new MersenneTwister(this.seed);
		this.next_bool_counter = -1;

		// developer: never forget to instantiate this.rn_distributor
	}

	/**
	 * Returns the seed of the random number generator.
	 * 
	 * @return The seed of the random number generator.
	 */
	public int getSeed() {
		return this.seed;
	}

	/**
	 * Returns if an associated event shall happen, according to the time
	 * probability distribution.
	 * 
	 * @return TRUE if the event must happen, FALSE if not.
	 * @developer The next_bool_counter attribute must be increased, when this
	 *            method is implemented, before any code.
	 */
	public abstract boolean nextBoolean();

	/**
	 * Configures the next_bool_counter attribute, and assures the distributor
	 * consistency.
	 */
	public void setNext_bool_counter(int next_bool_counter) {
		this.next_bool_counter = next_bool_counter;

		for (int i = 0; i < this.next_bool_counter + 1; i++)
			this.rn_distributor.nextInt();
	}

	public String reducedToXML(int identation) {
		// an ETPD doesn't have a lighter version
		return this.fullToXML(identation);
	}

	public String getObjectId() {
		// an etpd doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an etpd doesn't need an id
		// so, do nothing
	}
}