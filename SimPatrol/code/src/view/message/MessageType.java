package view.message;

/**
 * @model.uin <code>design:node:::3agb2f17vfk84-ja3s07</code>
 */

public class MessageType {
	private java.lang.String val;

	public static final MessageType PERCEPTION_REQUISITION = new MessageType(
			"PERCEPTION_REQUISITION");

	public static final MessageType ACTION_INTENTION = new MessageType(
			"ACTION_INTENTION");

	public static final MessageType PERCEPTION = new MessageType("PERCEPTION");

	private MessageType(java.lang.String value) {
		val = value;
	}

	public java.lang.String toString() {
		return val;
	}
}
