/* UniformEventTimeProbabilityDistribution.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/**
 * Implements the probability distributions of happening an event based on the
 * time of simulation that are given uniformly by a single probability value.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class UniformEventTimeProbabilityDistribution extends
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
		super(seed, probability);
		this.probability = probability;
	}

	protected void initDistributor(double... values) {
		double[] distribution = { Math.abs(1 - values[0]), Math.abs(values[0]) };

		// never forget to instantiate this.rnDistributor
		this.rnDistributor = new EmpiricalWalker(distribution,
				Empirical.NO_INTERPOLATION, this.rnGenerator);
	}

	protected void initEventTimeProbabilityDistributionType() {
		this.etpdType = EventTimeProbabilityDistributionTypes.UNIFORM;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// partially fills the buffer
		buffer.append("<etpd seed=\"" + this.seed + "\" sampling_interval=\""
				+ this.samplingTimeInterval + "\" next_bool_count=\""
				+ this.nextBoolCounter + "\" type=\"" + this.etpdType.getType()
				+ "\">");

		// puts the probability value
		buffer.append("<pd_parameter value=\"" + this.probability + "\"/>");

		// finishes the buffer content
		buffer.append("</etpd>");

		// returns the buffer content
		return buffer.toString();
	}
}