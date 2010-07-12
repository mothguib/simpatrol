/* DepthLimitation.java (2.0) */
package br.org.simpatrol.server.model.limitation;

import br.org.simpatrol.server.model.action.BroadcastAction;
import br.org.simpatrol.server.model.action.GoToAction;
import br.org.simpatrol.server.model.action.TeleportAction;
import br.org.simpatrol.server.model.perception.AgentsPerception;
import br.org.simpatrol.server.model.perception.BroadcastPerception;
import br.org.simpatrol.server.model.perception.GraphPerception;
import br.org.simpatrol.server.model.perception.StigmasPerception;

/**
 * Implements the limitations that control the depth of an ability of an agent,
 * in SimPatrol.
 * 
 * Such abilities are related to the sense of the {@link AgentsPerception},
 * {@link BroadcastPerception}, {@link GraphPerception} and
 * {@link StigmasPerception} perceptions, as well as they are related to the
 * execution of the {@link BroadcastAction}, {@link GoToAction} and
 * {@link TeleportAction} actions.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class DepthLimitation extends Limitation {
	/* Attributes. */
	/**
	 * The depth limit imposed to the actions or perceptions, measured in
	 * unities of adjacency degree from the current vertex of the agent.
	 */
	private int depth;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param depth
	 *            The depth limit imposed to the actions or perceptions,
	 *            measured in unities of adjacency degree from the current
	 *            vertex of the agent.
	 */
	public DepthLimitation(int depth) {
		super();
		this.depth = depth;
	}

	/**
	 * Returns the depth of this limitation.
	 * 
	 * @return The depth of this limitation, measured in unities of adjacency
	 *         degree from the current vertex of the agent.
	 */
	public int getDepth() {
		return this.depth;
	}

	protected void initActionType() {
		this.limitationType = LimitationTypes.DEPTH;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<limitation type=\"" + this.limitationType.getType()
				+ "\">");

		// puts the parameters of the limitation
		buffer.append("<lmt_parameter value=\"" + this.depth + "\"/>");

		// closes the main tag
		buffer.append("</limitation>");

		// returns the answer
		return buffer.toString();
	}
}