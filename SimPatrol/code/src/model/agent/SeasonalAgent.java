/* SeasonalAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import model.etpd.EventTimeProbabilityDistribution;
import model.graph.Node;
import model.interfaces.Mortal;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/**
 * Implements the agents that compound the open societies of SimPatrol.
 */
public final class SeasonalAgent extends Agent implements Mortal {
	/* Attributes. */
	/** The probability distribution for the death time of the agent. */
	private EventTimeProbabilityDistribution death_tpd;

	/** The society of the agent. */
	private OpenSociety society;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the agent.
	 * @param node
	 *            The node that the agent comes from.
	 * @param death_tpd
	 *            The probability distribution for the death time of the agent.
	 * @param allowed_perceptions
	 *            The allowed perceptions to the agent.
	 * @param allowed_actions
	 *            The allowed actions to the agent.
	 */
	public SeasonalAgent(String label, Node node,
			PerceptionPermission[] allowed_perceptions,
			ActionPermission[] allowed_actions,
			EventTimeProbabilityDistribution death_tpd) {
		super(label, node, allowed_perceptions, allowed_actions);
		this.death_tpd = death_tpd;
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

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML(identation));

		// updates the answer, if necessary
		if (this.death_tpd != null) {
			// deletes the closing tag
			if (this.allowed_perceptions == null
					&& this.allowed_actions == null) {
				int last_valid_index = buffer.lastIndexOf("/>");
				buffer.delete(last_valid_index, buffer.length());
				buffer.append(">\n");
			} else {
				StringBuffer closing_tag = new StringBuffer();
				for (int i = 0; i < identation; i++)
					closing_tag.append("\t");
				closing_tag.append("</agent>");

				int last_valid_index = buffer.lastIndexOf(closing_tag
						.toString());
				buffer.delete(last_valid_index, buffer.length());
			}

			// writes the death tpd
			buffer.append(this.death_tpd.fullToXML(identation + 1));

			// closes the main tag
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</agent>\n");
		}

		// returns the answer
		return buffer.toString();
	}

	public void die() {
		// removes the agent from its society
		this.society.removeAgent(this);
	}

	public EventTimeProbabilityDistribution getDeathTPD() {
		return this.death_tpd;
	}
}