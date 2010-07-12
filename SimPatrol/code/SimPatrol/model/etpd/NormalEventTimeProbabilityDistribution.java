/* NormalEventTimeProbabilityDistribution.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Normal;

/**
 * Implements the probability distributions of happening an event based on the
 * time of simulation that are given by a normal function.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class NormalEventTimeProbabilityDistribution extends
		EventTimeProbabilityDistribution {
	/* Attributes. */
	/** The mean of the normal function. */
	private double mean;

	/** The standard deviation of the normal function. */
	private double standardDeviation;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @param mean
	 *            The mean of the normal function.
	 * @param standardDeviation
	 *            The standard deviation of the normal function.
	 */
	public NormalEventTimeProbabilityDistribution(int seed, double mean,
			double standardDeviation) {
		super(seed, mean, standardDeviation);
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	protected void initDistributor(double... values) {
		this.rnDistributor = new Normal(values[0], values[1], this.rnGenerator);

	}

	protected void initEventTimeProbabilityDistributionType() {
		this.etpdType = EventTimeProbabilityDistributionTypes.NORMAL;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// partially fills the buffer
		buffer.append("<etpd seed=\"" + this.seed + "\" sampling_interval=\""
				+ this.samplingTimeInterval + "\" next_bool_count=\""
				+ this.nextBoolCounter + "\" type=\"" + this.etpdType.getType()
				+ "\">");

		// puts the mean value
		buffer.append("<pd_parameter value=\"" + this.mean + "\"/>");

		// puts the standard deviation value
		buffer.append("<pd_parameter value=\"" + this.standardDeviation
				+ "\"/>");

		// finishes the buffer content
		buffer.append("</etpd>");

		// returns the buffer content
		return buffer.toString();
	}
}