/* IdlenessControlleRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import model.graph.Vertex;
import control.simulator.RealTimeSimulator;

/** Implements the robots that assure the correct idleness measurement
 *  for the vertexes.
 *  Used by real time simulators.
 *  @see RealTimeSimulator */
public final class IdlenessControllerRobot extends Robot {
	/* Attributes. */
	/** The vertex whose idleness is controlled by this robot. */
	private Vertex vertex;
	
	/* Methods. */
	/** Constructor.
	 *  @param vertex The vertex monitored by the robot. */
	public IdlenessControllerRobot(Vertex vertex) {
		this.vertex = vertex;
	}

	public void act() {
		// increments the idleness of the vertex
		this.vertex.incIdleness();
	}

}
