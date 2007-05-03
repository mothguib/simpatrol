package control.requisition;

import java.util.List;
import util.Queue;
import view.connection.AgentConnection;

/**
 * @model.uin <code>design:node:::9wao5f17vcg2cqgz4qq</code>
 */
public class PerceptionRequisitionQueue extends Queue {

	/**
	 * @model.uin <code>design:node:::cmvoxf17vflv5-nneeo7</code>
	 */
	public AgentConnection agentConnection;

	/**
	 * @model.uin <code>design:node:::dm0n5f17vcg2c-joneso</code>
	 */
	private List<PerceptionRequisition> perceptionRequisition;

	/**
	 * @param requisition
	 * @model.uin <code>design:node:::chi0wf17vcg2cdqidz2:9wao5f17vcg2cqgz4qq</code>
	 */
	public void insert(PerceptionRequisition requisition) {
		/* default generated stub */;

	}

	/**
	 * @model.uin <code>design:node:::8lpczf17vcg2cw9sos6:9wao5f17vcg2cqgz4qq</code>
	 */
	public PerceptionRequisition remove() {
		/* default generated stub */;
		return null;
	}
}
