/* GoToAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.environment.Environment;
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.interfaces.Visible;
import br.org.simpatrol.server.model.limitation.AccelerationLimitation;
import br.org.simpatrol.server.model.limitation.DepthLimitation;
import br.org.simpatrol.server.model.limitation.Limitation;
import br.org.simpatrol.server.model.limitation.SpeedLimitation;

/**
 * Implements the action of moving an agent from its current position to a new
 * given one, on the graph of the simulation.
 * 
 * The path used to move the agent is obtained by the Dijkstra's algorithm.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class GoToAction extends CompoundAction {
	/* Attributes */
	/**
	 * The initial speed of the movement. Measured in
	 * "depth unities per second". Its default value is 1.0.
	 */
	private double initialSpeed = 1.0;

	/**
	 * The acceleration of the movement. Measured in "depth unities/sec^2". Its
	 * default value is ZERO.
	 */
	private double acceleration = 0.0;

	/** The vertex to where the agent shall go. */
	private Vertex goalVertex;

	/* Methods */
	/**
	 * Constructor.
	 * 
	 * @param goalVertex
	 *            The vertex to where the agent shall go.
	 */
	public GoToAction(Vertex goalVertex) {
		super();
		this.goalVertex = goalVertex;
	}

	/**
	 * Configures the initial speed of the movement.
	 * 
	 * @param initialSpeed
	 *            The initial speed of the movement. Measured in
	 *            "depth unities per second".
	 */
	public void setInitialSpeed(double initialSpeed) {
		this.initialSpeed = initialSpeed;
	}

	/**
	 * Configures the acceleration of the movement.
	 * 
	 * @param acceleration
	 *            The acceleration of the movement. Measured in
	 *            "depth unities/sec^2".
	 */

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	protected void initActionType() {
		this.actionType = ActionTypes.GOTO;
	}

	public List<List<? extends AtomicAction>> parse(Agent agent,
			Environment environment, double timeRate, Limitation... limitations) {
		// holds an eventual acc limitation to be applied to the goto action
		AccelerationLimitation accelerationLimitation = null;

		// holds an eventual speed limitation to be applied to the goto action
		SpeedLimitation speedLimitation = null;

		// holds an eventual depth limitation to be applied to the goto action
		DepthLimitation depthLimitation = null;

		// tries to find such limitation among the given ones
		for (Limitation limitation : limitations) {
			if (limitation instanceof AccelerationLimitation)
				accelerationLimitation = (AccelerationLimitation) limitation;
			else if (limitation instanceof SpeedLimitation)
				speedLimitation = (SpeedLimitation) limitation;
			else if (limitation instanceof DepthLimitation)
				depthLimitation = (DepthLimitation) limitation;

			if (accelerationLimitation != null && speedLimitation != null
					&& depthLimitation != null)
				break;
		}

		// holds the acceleration of the goto action
		double acceleration = this.acceleration;
		if (accelerationLimitation != null
				&& acceleration > accelerationLimitation.getAcceleration())
			acceleration = accelerationLimitation.getAcceleration();

		// holds the initial speed of the goto action
		double currentInitialSpeed = this.initialSpeed;
		if (speedLimitation != null
				&& currentInitialSpeed > speedLimitation.getSpeed())
			currentInitialSpeed = speedLimitation.getSpeed();

		// holds the depth of the goto action
		double depth = -1;
		if (depthLimitation != null)
			depth = depthLimitation.getDepth();

		// obtains the path used in the goto action
		Graph path = environment.getGraph().getEnabledDijkstraPath(
				agent.getVertex(), this.goalVertex);

		// current displacement
		double currentDisplacement = 0;

		// current vertex positions
		Vertex currentGraphVertex = agent.getVertex();
		Vertex currentPathVertex = null;

		Set<Vertex> pathVertexes = path.getVertexes();
		for (Vertex pathVertex : pathVertexes)
			if (pathVertex.equals(currentGraphVertex)) {
				currentPathVertex = pathVertex;
				break;
			}

		// current edge positions
		Edge currentGraphEdge = agent.getEdge();
		Edge currentPathEdge = null;

		if (currentPathVertex.getEdges().size() > 0)
			currentPathEdge = currentPathVertex.getEdges().iterator().next();
		else
			return null;

		if (currentGraphEdge == null) {
			Set<Edge> graphEdges = currentGraphVertex.getEdges();

			for (Edge graphEdge : graphEdges)
				if (graphEdge.equals(currentPathEdge)) {
					currentGraphEdge = graphEdge;
					break;
				}
		}

		// current elapsed length
		double currentElapsedLength = agent.getElapsedLength();

		// the obtained teleport actions
		LinkedList<TeleportAction> teleportActions = new LinkedList<TeleportAction>();

		// parses the compound action
		while (true) {
			// calculates the current displacement
			// d = v0 * t + a * t^2 / 2
			currentDisplacement = currentInitialSpeed * timeRate + acceleration
					* Math.pow(timeRate, 2) * 0.5;

			// if the current displacement is 0 (no speed neither acceleration),
			// returns an empty plan
			if (currentDisplacement == 0)
				return null;

			// calculates the next position of the agent on the graph
			double remainedLength = currentPathEdge.getLength()
					- currentElapsedLength;

			if (remainedLength > currentDisplacement) {
				currentElapsedLength = currentElapsedLength
						+ currentDisplacement;

				// adds the next teleport...
				teleportActions.add(new TeleportAction(currentGraphVertex,
						currentGraphEdge, currentElapsedLength));
			} else {
				// the objects that shall become visible during the teleport
				Set<Visible> visibleObjects = new HashSet<Visible>();

				while (currentDisplacement >= remainedLength) {
					currentDisplacement = currentDisplacement - remainedLength;
					currentPathVertex = currentPathEdge
							.getOtherVertex(currentPathVertex);
					currentGraphVertex = currentGraphEdge
							.getOtherVertex(currentGraphVertex);

					visibleObjects.add(currentGraphVertex);

					// decreases the depth to be reached
					depth--;

					// creates the adequate teleport
					Set<Edge> edgesCurrentPathVertex = currentPathVertex
							.getEdges();
					if (edgesCurrentPathVertex.size() <= 1 || depth == 0) {
						// adds the next teleport and returns the answer
						teleportActions.add(new TeleportAction(
								currentGraphVertex, null, 0, visibleObjects));

						List<List<? extends AtomicAction>> answer = new LinkedList<List<? extends AtomicAction>>();
						for (TeleportAction teleportAction : teleportActions) {
							List<TeleportAction> teleportActionList = new LinkedList<TeleportAction>();
							teleportActionList.add(teleportAction);

							answer.add(teleportActionList);
						}
						return answer;
					}

					// keep on adding the objects that shall become visible
					currentPathEdge = edgesCurrentPathVertex.iterator().next();
					if (currentPathEdge.equals(currentGraphEdge))
						currentPathEdge = edgesCurrentPathVertex.iterator()
								.next();

					Set<Edge> graphEdges = currentGraphVertex.getEdges();
					for (Edge graphEdge : graphEdges)
						if (graphEdge.equals(currentPathEdge)) {
							currentGraphEdge = graphEdge;
							break;
						}

					visibleObjects.add(currentGraphEdge);

					remainedLength = currentPathEdge.getLength();
				}

				// creates the adequate teleport action
				if (currentDisplacement > 0) {
					currentElapsedLength = currentDisplacement;

					teleportActions.add(new TeleportAction(currentGraphVertex,
							currentGraphEdge, currentElapsedLength));
				} else
					currentElapsedLength = 0;
			}

			// updates the current speed
			// v = v0 + a*t
			currentInitialSpeed = currentInitialSpeed + acceleration * timeRate;
			if (currentInitialSpeed > speedLimitation.getSpeed())
				currentInitialSpeed = speedLimitation.getSpeed();
		}
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType()
				+ "\" initial_speed=\"" + this.initialSpeed
				+ "\" acceleration=\"" + this.acceleration + "\" vertex_id=\""
				+ this.goalVertex.getId() + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}