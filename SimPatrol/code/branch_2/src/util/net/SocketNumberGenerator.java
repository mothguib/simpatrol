/* SocketNumberGenerator.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

/** Implements a generator of numbers for sockets. */
public final class SocketNumberGenerator {
	/* Attributes. */
	/** The generator of random numbers. */
	private MersenneTwister rn_generator;
	
	/** The distribuitor of random numbers. */
	private Normal rn_distribuitor;
	
	/** The standard deviation for the generator. */
	private static final int STD_DEVIATION = 1000;  
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param basis The value taken as basis for the socket number generation. */
	public SocketNumberGenerator(int basis) {
		this.rn_generator = new MersenneTwister((int) System.currentTimeMillis());
		this.rn_distribuitor = new Normal(basis, STD_DEVIATION, this.rn_generator);
	}
	
	/** Generates a number for the socket of a connection.
	 * 
	 *  @return The number for the socket. */
	public int generateSocketNumber() {
		return Math.abs(this.rn_distribuitor.nextInt());
	}
}