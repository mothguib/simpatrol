/* QlearningConfiguration.java */

/* The package of this class. */
package gray_box_learner.q_learning_engine;

/** Holds the configuration parameters of the q-learning engine. */
public class QLearningConfiguration {
	/* Attributes. */
	/**
	 * The probability of an agent choose an exploration action.
	 */
	private final double E;

	/** The rate of the decaying of the alpha value in the q-learning algorithm. */
	private final double ALFA_DECAY_RATE;

	/** The discount factor in the q-learning algorithm. */
	private final double GAMA;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param e
	 *            The probability of an agent choose an exploration action.
	 * @param alfa_decay_rate
	 *            The rate of the decaying of the alpha value in the q-learning
	 *            algorithm.
	 * @param gama
	 *            The discount factor in the q-learning algorithm.
	 */
	public QLearningConfiguration(double e, double alfa_decay_rate, double gama) {
		this.E = e;
		this.ALFA_DECAY_RATE = alfa_decay_rate;
		this.GAMA = gama;
	}

	/**
	 * Returns the probability of an agent choose an exploration action.
	 * 
	 * @return The probability of an agent choose an exploration action.
	 */
	public double getE() {
		return this.E;
	}

	/**
	 * Returns the rate of the decaying of the alpha value in the q-learning
	 * algorithm.
	 * 
	 * @return The rate of the decaying of the alpha value in the q-learning
	 *         algorithm.
	 */
	public double getAlfa_decay_rate() {
		return this.ALFA_DECAY_RATE;
	}

	/**
	 * Returns the discount factor in the q-learning algorithm.
	 * 
	 * @return The discount factor in the q-learning algorithm.
	 */
	public double getGama() {
		return this.GAMA;
	}
}
