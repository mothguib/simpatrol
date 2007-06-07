/* ClosedSociety.java */

/* The package of this class. */
package model.agent;

/** Implements the closed societies of agents of SimPatrol. */
public class ClosedSociety extends Society {
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the closed society.
	 *  @param perpetual_agents The perpetual agents that compound the closed society. */
	public ClosedSociety(String label, PerpetualAgent[] perpetual_agents) {
		super(label, perpetual_agents);
	}
}