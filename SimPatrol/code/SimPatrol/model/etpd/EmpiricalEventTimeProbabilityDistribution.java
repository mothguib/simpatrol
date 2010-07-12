/* EmpiricalEventTimeProbabilityDistribution.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/**
 * Implements the probability distributions of happening an event based on the
 * time of simulation that are given by an empirical method.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class EmpiricalEventTimeProbabilityDistribution extends
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
		super(seed, distribution);
		this.distribution = distribution;
	}

	protected void initDistributor(double... values) {
		this.rnDistributor = new EmpiricalWalker(values,
				Empirical.NO_INTERPOLATION, this.rnGenerator);
	}

	protected void initEventTimeProbabilityDistributionType() {
		this.etpdType = EventTimeProbabilityDistributionTypes.EMPIRICAL;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// partially fills the buffer
		buffer.append("<etpd seed=\"" + this.seed + "\" sampling_interval=\""
				+ this.samplingTimeInterval + "\" next_bool_count=\""
				+ this.nextBoolCounter + "\" type=\"" + this.etpdType.getType()
				+ "\">");

		// completes the buffer content
		for (int i = 0; i < this.distribution.length; i++)
			// inserts the parameters
			buffer.append("<pd_parameter value=\"" + this.distribution[i]
					+ "\"/>");

		// finishes the buffer content
		buffer.append("</etpd>");

		// returns the buffer content
		return buffer.toString();
	}
}