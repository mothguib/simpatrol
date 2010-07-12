/* SocketNumberGenerator.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

/** Implements a generator of numbers for net sockets. */
public final class SocketNumberGenerator {
	/* Attributes. */
	/** The generator of random numbers. */
	private final MersenneTwister RN_GENERATOR;

	/** The distributor of random numbers. */
	private final Normal RN_DISTRIBUTOR;

	/** The standard deviation of the generator. */
	private static final int STD_DEVIATION = 1000;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param basis
	 *            The value taken as basis for the socket number generation.
	 */
	public SocketNumberGenerator(int basis) {
		this.RN_GENERATOR = new MersenneTwister((int) System
				.currentTimeMillis());
		this.RN_DISTRIBUTOR = new Normal(basis, STD_DEVIATION,
				this.RN_GENERATOR);
	}

	/**
	 * Generates a number for the socket.
	 * 
	 * @return The generated number for the socket.
	 */
	public int generateSocketNumber() {
		return Math.abs(this.RN_DISTRIBUTOR.nextInt());
	}
}