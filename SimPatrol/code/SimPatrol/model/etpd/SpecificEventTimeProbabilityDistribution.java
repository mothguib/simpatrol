/* SpecificEventTimeProbabilityDistribution.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;

/**
 * Implements the probability distributions of happening an event based on a
 * specific time of the simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class SpecificEventTimeProbabilityDistribution extends
		EventTimeProbabilityDistribution {
	/* Attributes. */
	/**
	 * The probability value of happening an associated event. Its value must
	 * belong to the interval [0,1].
	 */
	private double probability;

	/**
	 * The specific time when the dices must be thrown. Measured in seconds of
	 * simulation.
	 */
	private int time;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param seed
	 *            The seed for the random number generation.
	 * @param probability
	 *            The probability of happening an associated event.
	 * @param time
	 *            The specific time when "the dices must be thrown". Measured in
	 *            seconds of simulation.
	 */
	public SpecificEventTimeProbabilityDistribution(int seed,
			double probability, int time) {
		super(seed, probability);
		this.probability = probability;
		this.time = time;
	}

	protected void initDistributor(double... values) {
		double[] distribution = { Math.abs(1 - values[0]), Math.abs(values[0]) };

		this.rnDistributor = new EmpiricalWalker(distribution,
				Empirical.NO_INTERPOLATION, this.rnGenerator);
	}

	protected void initEventTimeProbabilityDistributionType() {
		this.etpdType = EventTimeProbabilityDistributionTypes.SPECIFIC;
	}

	public boolean nextBoolean() {
		// never forget to increase nextBoolCounter, before any code
		this.nextBoolCounter++;

		int randomValue = this.rnDistributor.nextInt();
		if (this.nextBoolCounter == this.time)
			if (randomValue == 1)
				return true;

		return false;
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

		// puts the time value
		buffer.append("<pd_parameter value=\"" + this.time + "\"/>");

		// finishes the buffer content
		buffer.append("</etpd>");

		// returns the buffer content
		return buffer.toString();
	}
}
