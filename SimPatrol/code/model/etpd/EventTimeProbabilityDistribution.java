/* EventTimeProbabilityDistribution.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.interfaces.XMLable;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.engine.MersenneTwister;

/**
 * Implements the probability distributions of happening an event, based on the
 * time of simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class EventTimeProbabilityDistribution implements XMLable {
	/* Attributes. */
	/** The seed for the random number generator. */
	protected int seed;

	/** The generator of random numbers. */
	protected MersenneTwister rnGenerator;

	/** The random numbers distributor. */
	protected AbstractDistribution rnDistributor;

	/**
	 * Counts how many times the dices were thrown (i.e. the
	 * {@link #nextBoolean()} method was called).
	 */
	protected int nextBoolCounter;

	/**
	 * Holds the time interval that shall pass before the dices are thrown (i.e.
	 * the time interval that shall pass between two consecutive invocations of
	 * the {@link #nextBoolean()} method). Measured in seconds. Its default
	 * value is 1 sec.
	 */
	protected double samplingTimeInterval = 1.0;

	/** Holds the type of the etpd. */
	protected EventTimeProbabilityDistributionTypes etpdType;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @param rnValues
	 *            The values used to initiate the random numbers distributor.
	 */
	public EventTimeProbabilityDistribution(int seed, double... rnValues) {
		this.seed = seed;
		this.rnGenerator = new MersenneTwister(this.seed);
		this.nextBoolCounter = -1;

		// initiates the random numbers distributor
		this.initDistributor(rnValues);

		// initiates the etpd type
		this.initEventTimeProbabilityDistributionType();
	}

	/**
	 * Initializes the random numbers distributor. {@link #rnDistributor}
	 * 
	 * @param values
	 *            The values used to initiate the random numbers distributor.
	 */
	protected abstract void initDistributor(double... values);

	/**
	 * Initializes the type of the etpd.
	 * 
	 * {@link #etpdType}
	 */
	protected abstract void initEventTimeProbabilityDistributionType();

	/**
	 * Configures the time interval that shall pass before the dices are thrown
	 * (i.e. the time interval that shall pass between two consecutive
	 * invocations of the {@link #nextBoolean()} method).
	 * 
	 * @param samplingTimeInterval
	 *            The time interval that shall pass before the dices are thrown,
	 *            measured in seconds.
	 */
	public void setSamplingTimeInterval(double samplingTimeInterval) {
		this.samplingTimeInterval = samplingTimeInterval;
	}

	/**
	 * Returns the time interval that shall pass before the dices are thrown
	 * (i.e. the time interval that shall pass between two consecutive
	 * invocations of the {@link #nextBoolean()} method). Measured in seconds.
	 * 
	 * @return The time interval that shall pass before the dices are thrown,
	 *         measured in seconds.
	 */
	public double getSamplingTimeInterval() {
		return this.samplingTimeInterval;
	}

	/**
	 * Returns if an associated event shall happen, according to the time
	 * probability distribution. The {@link #nextBoolCounter} attribute must be
	 * increased, if this method is being overwritten, before any code.
	 * 
	 * @return TRUE if the event must happen, FALSE if not.
	 */
	public boolean nextBoolean() {
		// never forget to increase nextBoolCounter, before any code
		this.nextBoolCounter++;

		if (rnDistributor.nextInt() == nextBoolCounter)
			return true;
		else
			return false;
	}

	/**
	 * Configures the nextBoolCounter attribute, and assures the distributor
	 * consistency.
	 */
	public void setNextBoolCounter(int nextBoolCounter) {
		this.nextBoolCounter = nextBoolCounter;

		for (int i = 0; i < this.nextBoolCounter + 1; i++)
			this.rnDistributor.nextInt();
	}

	public String reducedToXML() {
		// an ETPD doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// an etpd doesn't need an id
		return null;
	}
}