package simpatrol.model.perception;

/**
 * @model.uin <code>design:node:::yt62f17uk738pdmhmb</code>
 */

public class ReactivePerceptionType extends PerceptionType {
	public static final ReactivePerceptionType AGENT_PERCEPTION = new ReactivePerceptionType(
			"AGENT_PERCEPTION");

	public static final ReactivePerceptionType GRAPH_PERCEPTION = new ReactivePerceptionType(
			"GRAPH_PERCEPTION");

	public static final ReactivePerceptionType STIGMA_PERCEPTION = new ReactivePerceptionType(
			"STIGMA_PERCEPTION");

	private ReactivePerceptionType(java.lang.String value) {
		super(value);
	}
}
