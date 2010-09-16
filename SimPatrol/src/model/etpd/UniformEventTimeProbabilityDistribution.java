/* UniformEventTimeProbabilityDistribution.java */

/* The package of this class. */
package model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/**
 * Implements the probability distributions of happening an event based on the
 * time of simulation that are given uniformly by a single probability value.
 */
public final class UniformEventTimeProbabilityDistribution extends
		EventTimeProbabilityDistribution {
	/* Attributes. */
	/**
	 * The probability value of happening an associated event. Its value must
	 * belong to the interval [0,1].
	 */
	private double probability;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @param probability
	 *            The probability of happening an associated event.
	 */
	public UniformEventTimeProbabilityDistribution(int seed, double probability) {
		super(seed);
		this.probability = probability;
		double[] distribution = { Math.abs(1 - this.probability),
				Math.abs(this.probability) };

		// never forget to instantiate this.rn_distributor
		this.rn_distributor = new EmpiricalWalker(distribution,
				Empirical.NO_INTERPOLATION, this.rn_generator);
	}

	/**
	 * Returns the probability.
	 * 
	 * @return The probability.
	 */
	public double getProbability() {
		return this.probability;
	}

	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code
		this.next_bool_counter++;

		if (this.rn_distributor.nextInt() == 0)
			return false;
		else
			return true;
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// partially fills the buffer
		buffer.append("<etpd seed=" + this.seed + "\" next_bool_count=\""
				+ this.next_bool_counter + "\" type=\""
				+ EventTimeProbabilityDistributionTypes.UNIFORM + "\">\n");

		// puts the probability value
		for (int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		buffer.append("<pd_parameter value=\"" + this.probability + "\"/>\n");

		// finishes the buffer content
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</etpd>\n");

		// returns the buffer content
		return buffer.toString();
	}
}