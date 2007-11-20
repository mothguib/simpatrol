/* EmpiricalEventTimeProbabilityDistribution.java */

/* The package of this class. */
package model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/**
 * Implements the probability distributions of happening an event based on the
 * time of simulation that are given by an empirical method.
 */
public final class EmpiricalEventTimeProbabilityDistribution extends
		EventTimeProbabilityDistribution {
	/* Attributes. */
	/** The discrete empirical distribution. */
	private double[] distribution;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @param distribution
	 *            The empirical distribution.
	 */
	public EmpiricalEventTimeProbabilityDistribution(int seed,
			double[] distribution) {
		super(seed);
		this.distribution = distribution;

		// never forget to instantiate this.rn_distributor
		this.rn_distributor = new EmpiricalWalker(this.distribution,
				Empirical.NO_INTERPOLATION, this.rn_generator);
	}

	/**
	 * Returns the discrete empirical distribution.
	 * 
	 * @return The discrete empirical distribution.
	 */
	public double[] getDistribution() {
		return this.distribution;
	}

	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code
		this.next_bool_counter++;

		if (rn_distributor.nextInt() == next_bool_counter)
			return true;
		else
			return false;
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// partially fills the buffer
		buffer.append("<etpd seed=\"" + this.seed + "\" next_bool_count=\""
				+ this.next_bool_counter + "\" type=\""
				+ EventTimeProbabilityDistributionTypes.EMPIRICAL + "\">\n");

		// completes the buffer content
		for (int i = 0; i < this.distribution.length; i++) {
			// applies the identation
			for (int j = 0; j < identation + 1; j++)
				buffer.append("\t");

			// inserts the parameters
			buffer.append("<pd_parameter value=\"" + this.distribution[i]
					+ "\"/>\n");
		}

		// finishes the buffer content
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</etpd>\n");

		// returns the buffer content
		return buffer.toString();
	}
}