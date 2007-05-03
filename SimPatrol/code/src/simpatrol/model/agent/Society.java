package simpatrol.model.agent;

import simpatrol.control.simulator.Simulator;
import simpatrol.model.interfaces.XMLable;

/**
 * @model.uin <code>design:node:::hyht6f17ujey8gc0qlv</code>
 */
public abstract class Society implements XMLable {

	/**
	 * @model.uin <code>design:node:::5fdg2f17vcpxt33cmgu</code>
	 */
	public Simulator simulator;

	/**
	 * @model.uin <code>design:node:::dpurnf17ujey8-gjzlva:hyht6f17ujey8gc0qlv</code>
	 */
	private String label;
}