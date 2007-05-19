package control.daemon;

import java.util.Set;
import java.util.Collection;
import control.requisition.PerceptionRequisitionQueue;
import model.agent.Agent;

/**
 * @model.uin <code>design:node:::f1mcnf17vaioyh63h3f</code>
 */
public class PerceptionDaemon extends Daemon {

	/**
	 * @model.uin <code>design:node:::brd6kf17vchyi-vfij57</code>
	 */
	public Set allowed_perceptions;

	/**
	 * @model.uin <code>design:node:::i172kf17ujey8agupu8</code>
	 */
	public Agent agent;

	/**
	 * @model.uin <code>design:node:::9gi2yf17uk7386paoue</code>
	 */
	public Collection perception;

	/**
	 * @model.uin <code>design:node:::9wao5f17vcg2cqgz4qq</code>
	 */
	public PerceptionRequisitionQueue perceptionRequisitionQueue;

	/**
	 * @model.uin <code>design:node:::3hgasf17vaioy-fsar0r:f1mcnf17vaioyh63h3f</code>
	 */
	private void grantPerceptionRequisition() {
		/* default generated stub */;

	}
}
