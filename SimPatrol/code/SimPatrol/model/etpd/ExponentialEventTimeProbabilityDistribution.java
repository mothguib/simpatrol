/* ExponentialEventTimeProbabilityDistribution.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Exponential;

/**
 * Implements the probability distributions of happening an event based on the
 * time of simulation that are given by an exponential function.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class ExponentialEventTimeProbabilityDistribution extends
		EventTimeProbabilityDistribution {
	/* Attributes. */
	/** The rate parameter (lambda value) of the exponential function. */
	private double lambda;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @param lamda
	 *            The lambda value of the exponential function.
	 */
	public ExponentialEventTimeProbabilityDistribution(int seed, double lambda) {
		super(seed, lambda);
		this.lambda = lambda;
	}

	protected void initDistributor(double... values) {
		this.rnDistributor = new Exponential(values[0], this.rnGenerator);
	}

	protected void initEventTimeProbabilityDistributionType() {
		this.etpdType = EventTimeProbabilityDistributionTypes.EXPONENTIAL;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// partially fills the buffer
		buffer.append("<etpd seed=\"" + this.seed + "\" sampling_interval=\""
				+ this.samplingTimeInterval + "\" next_bool_count=\""
				+ this.nextBoolCounter + "\" type=\"" + this.etpdType.getType()
				+ "\">");

		// puts the lambda value
		buffer.append("<pd_parameter value=\"" + this.lambda + "\"/>");

		// finishes the buffer content
		buffer.append("</etpd>");

		// returns the buffer content
		return buffer.toString();
	}
}