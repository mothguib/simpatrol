package control.daemon;

import java.util.Set;
import java.util.Collection;
import control.requisition.ActionRequisitionQueue;
import model.agent.Agent;

/**
 * @model.uin <code>design:node:::7664yf17vaioyemex79</code>
 */
public class ActionDaemon extends Daemon {

	/**
	 * @model.uin <code>design:node:::3st2bf17vchyi-yxwo2t</code>
	 */
	public Set allowed_actions;

	/**
	 * @model.uin <code>design:node:::4gdw3f17vcg2c3hi59r</code>
	 */
	public ActionRequisitionQueue actionRequisitionQueue;

	/**
	 * @model.uin <code>design:node:::i172kf17ujey8agupu8</code>
	 */
	public Agent agent;

	/**
	 * @model.uin <code>design:node:::gjlolf17ugxj1-5az2i2</code>
	 */
	public Collection atomicAction;

	/**
	 * @model.uin <code>design:node:::16izyf17vaioy-bhjnc7:7664yf17vaioyemex79</code>
	 */
	private void grantActionRequisition() {
		/* default generated stub */;

	}
}
