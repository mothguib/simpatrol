package model.agent;

import java.util.Set;

import model.interfaces.XMLable;

/**
 * @model.uin <code>design:node:::hyht6f17ujey8gc0qlv</code>
 */
public abstract class Society implements XMLable {

	/**
	 * @model.uin <code>design:node:::dpurnf17ujey8-gjzlva:hyht6f17ujey8gc0qlv</code>
	 */
	private String label;
	
	public abstract Agent[] getAgents();
}
