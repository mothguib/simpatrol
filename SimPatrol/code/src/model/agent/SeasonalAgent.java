/* SeasonalAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import model.graph.Vertex;

/** Implements the agents that compound the 
 *  open societies of SimPatrol. */
public class SeasonalAgent extends Agent {
	/* Attributes. */
	/** Registers how much time (or how many cycles)
	 *  remain to the agent exist in the patrolling
	 *  simulation.
	 *  If it's -1, then the agent is immortal. */
	private int life_time;
	
	/* Methods. */
	/** Constructor.
	 *  @param vertex The vertex that the agent comes from.
	 *  @param life_time The remaining simulation time for the agent. */	
	public SeasonalAgent(Vertex vertex, int life_time) {
		super(vertex);
		this.life_time = life_time;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// finds the life_time attribute, atualizing it
		int index_life_time = buffer.lastIndexOf("-1");
		buffer.replace(index_life_time, index_life_time + 2, String.valueOf(this.life_time));
		
		// returns the buffer content
		return buffer.toString();
	}		
}
