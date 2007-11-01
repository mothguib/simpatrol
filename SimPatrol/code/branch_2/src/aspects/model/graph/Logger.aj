package model.graph;

public aspect Logger {

	/**
	 * Is Dynamic Vertex enabled?
	 */
	pointcut isDynamicVertexEnabled(DynamicVertex vertex, boolean enabled) : 
		execution(* DynamicVertex.setIsEnabled(..)) &&
		this(vertex) && args(enabled);

	before(DynamicVertex vertex, boolean enabled) : isDynamicVertexEnabled(vertex, enabled) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event] " + vertex.getObjectId() + " enabled "
						+ enabled + ".");
	}

	/**
	 * Is enabled?
	 */
	pointcut isEdgeEnabled(Edge edge, boolean enabled) :
		execution(* Edge.setIsEnabled(..)) && this(edge) && args(enabled);

	after(Edge edge, boolean enabled) : isEdgeEnabled(edge, enabled) {
		logger.Logger.getInstance().log(
				"[SimPatrol.Event] " + edge.getObjectId() + " enabled "
						+ enabled + ".");
	}
}
