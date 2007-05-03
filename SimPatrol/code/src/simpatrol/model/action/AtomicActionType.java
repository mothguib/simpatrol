package simpatrol.model.action;

/**
 * @model.uin <code>design:node:::1ap6if17ugxj1jmswgw</code>
 */

public class AtomicActionType extends ActionType {
	public static final AtomicActionType MOVE_ACTION = new AtomicActionType(
			"MOVE_ACTION");

	public static final AtomicActionType VISIT_ACTION = new AtomicActionType(
			"VISIT_ACTION");

	public static final AtomicActionType BROADCAST_ACTION = new AtomicActionType(
			"BROADCAST_ACTION");

	public static final AtomicActionType STIGMATIZE_ACTION = new AtomicActionType(
			"STIGMATIZE_ACTION");

	public static final AtomicActionType RECHARGE_ACTION = new AtomicActionType(
			"RECHARGE_ACTION");

	private AtomicActionType(java.lang.String value) {
		super(value);
	}
}
