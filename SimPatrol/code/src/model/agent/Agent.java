package model.agent;

import model.graph.Edge;
import model.graph.Stigma;
import model.graph.Vertex;
import model.interfaces.XMLable;
import model.perception.Perception;
import control.daemon.ActionDaemon;
import control.daemon.PerceptionDaemon;

/**
 * @model.uin <code>design:node:::i172kf17ujey8agupu8</code>
 */
public abstract class Agent implements XMLable {

	/**
	 * @model.uin <code>design:node:::f1mcnf17vaioyh63h3f</code>
	 */
	public PerceptionDaemon perceptionDaemon;

	/**
	 * @model.uin <code>design:node:::a7glof17uk14ujzrw3k</code>
	 */
	public Edge edge;

	/**
	 * @model.uin <code>design:node:::32va9f17uk14u-45ywpq</code>
	 */
	public Stigma stigma;

	/**
	 * @model.uin <code>design:node:::7664yf17vaioyemex79</code>
	 */
	public ActionDaemon actionDaemon;

	/**
	 * @model.uin <code>design:node:::e2gmtf17uk14u-dyl3jm</code>
	 */
	public Vertex vertex;

	/**
	 * @model.uin <code>design:node:::j6ugpf17ujey8btcg67:i172kf17ujey8agupu8</code>
	 */
	private double stamina = 1.0;

	/**
	 * @param perception
	 * @model.uin <code>design:node:::4nanf17ujey8-fenk9p:i172kf17ujey8agupu8</code>
	 */
	public void setPerception(Perception perception) {
		/* default generated stub */;

	}

	/**
	 * @param requisition
	 * @model.uin <code>design:node:::dlwtnf17ujey8bzkqn9:i172kf17ujey8agupu8</code>
	 */
	public void requireBroadcastingPercetion(String requisition) {
		/* default generated stub */;

	}
}
