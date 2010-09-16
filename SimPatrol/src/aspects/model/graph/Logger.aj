package model.graph;

import control.event.EdgeEnablingEvent;
import control.event.VertexEnablingEvent;

public aspect Logger {

	/**
	 * Is Dynamic Vertex enabled?
	 */
	pointcut isDynamicVertexEnabled(DynamicVertex vertex, boolean enabled) : 
		execution(* DynamicVertex.setIsEnabled(..)) &&
		this(vertex) && args(enabled);

	before(DynamicVertex vertex, boolean enabled) : isDynamicVertexEnabled(vertex, enabled) {
		VertexEnablingEvent event = new VertexEnablingEvent(vertex
				.getObjectId());
		control.event.Logger.send(event);
	}

	/**
	 * Is enabled?
	 */
	pointcut isEdgeEnabled(Edge edge, boolean enabled) :
		execution(* Edge.setIsEnabled(..)) && this(edge) && args(enabled);

	after(Edge edge, boolean enabled) : isEdgeEnabled(edge, enabled) {
		EdgeEnablingEvent event = new EdgeEnablingEvent(edge.getObjectId());
		control.event.Logger.send(event);
	}
}
