package model.graph;

import control.event.EdgeEnablingEvent;
import control.event.NodeEnablingEvent;

public aspect Logger {

	/**
	 * Is Dynamic Node enabled?
	 */
	pointcut isDynamicNodeEnabled(DynamicNode node, boolean enabled) : 
		execution(* DynamicNode.setIsEnabled(..)) &&
		this(node) && args(enabled);

	before(DynamicNode node, boolean enabled) : isDynamicNodeEnabled(node, enabled) {
		NodeEnablingEvent event = new NodeEnablingEvent(node
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
