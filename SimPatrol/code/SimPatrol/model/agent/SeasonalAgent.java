/* SeasonalAgent.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.org.simpatrol.server.control.robot.Bot;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.interfaces.Dynamic;

/**
 * Implements the agents that compound open societies in SimPatrol.
 * 
 * @see OpenSociety
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class SeasonalAgent extends Agent implements Dynamic {
	/* Attributes. */
	/**
	 * Enumerates the dynamic methods of this class (i.e. the methods that are
	 * called automatically by a {@link Bot} object).
	 */
	public static enum DYN_METHODS {
		DIE("die");

		private final String METHOD_NAME;

		private DYN_METHODS(String methodName) {
			this.METHOD_NAME = methodName;
		}

		public String getMethodName() {
			return this.METHOD_NAME;
		}
	}

	/**
	 * Maps the name of the dynamic methods (i.e. the methods that are called
	 * automatically by a {@link Bot} object) with their
	 * {@link EventTimeProbabilityDistribution} objects.
	 */
	private Map<String, EventTimeProbabilityDistribution> dynMap;

	/** The society of the agent. */
	private OpenSociety society;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the agent.
	 * @param label
	 *            The label of the agent.
	 * @param vertex
	 *            The vertex from where the agent comes.
	 * @param deathTPD
	 *            The probability distribution for the death time of the agent,
	 *            or NULL if it does not die.
	 * @param perceptionAbilities
	 *            The abilities related to the perceptions of the agent.
	 * @param actionAbilities
	 *            The abilities related to the actions of the agent.
	 */
	public SeasonalAgent(String id, String label, Vertex vertex,
			EventTimeProbabilityDistribution deathTPD,
			Set<PerceptionAbility> perceptionAbilities,
			Set<ActionAbility> actionAbilities) {
		super(id, label, vertex, perceptionAbilities, actionAbilities);

		if (deathTPD != null) {
			this.dynMap = new HashMap<String, EventTimeProbabilityDistribution>();
			this.dynMap
					.put(SeasonalAgent.DYN_METHODS.DIE.METHOD_NAME, deathTPD);
		} else
			this.dynMap = null;

		this.society = null;
	}

	/**
	 * Configures the society of the agent.
	 * 
	 * @param society
	 *            The society of the agent.
	 */
	public void setSociety(OpenSociety society) {
		this.society = society;
	}

	/** Kills the agent, removing it from its society. */
	public void die() {
		this.society.removeAgent(this);
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML());

		// updates the answer, if necessary
		if (this.dynMap != null) {
			// deletes the closing tag
			if ((this.perceptionAbilities == null || this.perceptionAbilities
					.size() == 0)
					&& (this.actionAbilities == null || this.actionAbilities
							.size() == 0)) {
				int lastValidIndex = buffer.lastIndexOf("/>");
				buffer.delete(lastValidIndex, buffer.length());
				buffer.append(">");
			} else {
				int lastValidIndex = buffer.lastIndexOf("</agent>");
				buffer.delete(lastValidIndex, buffer.length());
			}

			// writes the death tpd
			buffer.append(this.dynMap.get(
					SeasonalAgent.DYN_METHODS.DIE.METHOD_NAME).fullToXML());

			// closes the main tag
			buffer.append("</agent>");
		}

		// returns the answer
		return buffer.toString();
	}

	public EventTimeProbabilityDistribution getETPD(String methodName) {
		if (this.dynMap != null)
			return this.dynMap.get(SeasonalAgent.DYN_METHODS.DIE.METHOD_NAME);

		return null;
	}
}