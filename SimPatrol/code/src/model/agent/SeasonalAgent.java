/* SeasonalAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import util.etpd.EventTimeProbabilityDistribution;
import model.graph.Vertex;
import model.interfaces.Mortal;

/** Implements the agents that compound the 
 *  open societies of SimPatrol. */
public class SeasonalAgent extends Agent implements Mortal {
	/* Attributes. */
	/** The probability distribution for the death time of the agent. */
	private EventTimeProbabilityDistribution death_tpd;
	
	/** The society of the agent. */
	private OpenSociety society;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the agent. 
	 *  @param vertex The vertex that the agent comes from.
	 *  @param death_tpd The probability distribution for the death time of the agent. */
	public SeasonalAgent(String label, Vertex vertex, EventTimeProbabilityDistribution death_tpd) {
		super(label, vertex);
		this.death_tpd = death_tpd;
	}
	
	/** Configures the society of the agent.
	 *  @param society The society of the agent. */
	public void setSociety(OpenSociety society) {
		this.society = society;		
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// atualizes the answer, if necessary
		if(this.death_tpd != null) {
			// deletes the closing tag
			int last_valid_index = buffer.lastIndexOf("/>");
			buffer.delete(last_valid_index, buffer.length());
			buffer.append(">\n");
			
			// writes the death tpd
			buffer.append(this.death_tpd.toXML(identation + 1));			
		}
		
		// closes the agent tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</agent>\n");
		
		// returns the answer
		return buffer.toString();
	}

	public void die() {
		// stops the work of the agent
		this.stopWorking();
		
		// removes the agent from its society
		this.society.removeAgent(this);
		
		// TODO retirar linha abaixo
		System.out.println(this.getObjectId() + " morreu.");
	}

	public EventTimeProbabilityDistribution getDeathTPD() {
		return this.death_tpd;
	}
}