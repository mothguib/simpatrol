package simpatrol.view.connection;

import simpatrol.control.parser.ActionIntentionParser;
import simpatrol.control.requisition.PerceptionRequisitionQueue;
import simpatrol.model.perception.Perception;

/**
 * @model.uin <code>design:node:::cmvoxf17vflv5-nneeo7</code>
 */
public class AgentConnection extends Connection {

	/**
	 * @model.uin <code>design:node:::9wao5f17vcg2cqgz4qq</code>
	 */
	public PerceptionRequisitionQueue perceptionRequisitionQueue;

	/**
	 * @model.uin <code>design:node:::7fxyuf17vbztqy5hrpa</code>
	 */
	public ActionIntentionParser actionIntentionParser;

	/**
	 * @param requisition
	 * @model.uin <code>design:node:::9i2dgf17vflv56yn6e6:cmvoxf17vflv5-nneeo7</code>
	 */
	public void requirePerception(String requisition) {
		/* default generated stub */;

	}

	/**
	 * @param perception
	 * @model.uin <code>design:node:::a6xbqf17vflv5-5eb1h4:cmvoxf17vflv5-nneeo7</code>
	 */
	public void setGrantedPerception(Perception perception) {
		/* default generated stub */;

	}

	/**
	 * @param intention
	 * @model.uin <code>design:node:::f087pf17vflv5-suaw9n:cmvoxf17vflv5-nneeo7</code>
	 */
	public void intendAction(String intention) {
		/* default generated stub */;

	}
}
