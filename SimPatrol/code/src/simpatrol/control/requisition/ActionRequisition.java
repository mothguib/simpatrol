package simpatrol.control.requisition;

import simpatrol.control.parser.ActionIntentionParser;
import simpatrol.model.action.AtomicActionType;
import simpatrol.util.TimeProbabilityDistribution;

/**
 * @model.uin <code>design:node:::gtuu7f17vcg2c-779mfa</code>
 */
public class ActionRequisition extends Requisition {

	/**
	 * @model.uin <code>design:node:::nrlff17vgl5z6xtpx3</code>
	 */
	public TimeProbabilityDistribution timeProbabilityDistribution;

	/**
	 * @model.uin <code>design:node:::7fxyuf17vbztqy5hrpa</code>
	 */
	public ActionIntentionParser actionIntentionParser;

	/**
	 * @model.uin <code>design:node:::4gdw3f17vcg2c3hi59r</code>
	 */
	public ActionRequisitionQueue actionRequisitionQueue;

	/**
	 * @model.uin <code>design:node:::4tznxf17vcg2c7vqq0a:gtuu7f17vcg2c-779mfa</code>
	 */
	private AtomicActionType action_type;
}
