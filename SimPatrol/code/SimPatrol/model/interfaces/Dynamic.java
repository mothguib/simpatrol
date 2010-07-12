/* Dynamic.java (2.0) */
package br.org.simpatrol.server.model.interfaces;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;

/**
 * Lets the objects that implement it have dynamic behaviour, governed by event
 * time probability distributions (i.e. {@link EventTimeProbabilityDistribution}
 * objects).
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public interface Dynamic {
	/**
	 * Returns the event time probability distribution that governs the
	 * behaviour implemented by the method of which name is the given string.
	 * 
	 * @param methodName
	 *            The name of the method that implements the refereed dynamic
	 *            behaviour.
	 * 
	 * @return The {@link EventTimeProbabilityDistribution} object that governs
	 *         the refereed dynamic behaviour.
	 */
	EventTimeProbabilityDistribution getETPD(String methodName);
}
