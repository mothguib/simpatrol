/* SeasonalAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import util.etpd.EventTimeProbabilityDistribution;
import model.graph.Vertex;
import model.interfaces.Mortal;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/** Implements the agents that compound the 
 *  open societies of SimPatrol. */
public final class SeasonalAgent extends Agent implements Mortal {
	/* Attributes. */
	/** The probability distribution for the death time of the agent. */
	private EventTimeProbabilityDistribution death_tpd;
	
	/** The society of the agent. */
	private OpenSociety society;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param label The label of the agent. 
	 *  @param vertex The vertex that the agent comes from.
	 *  @param death_tpd The probability distribution for the death time of the agent.
	 *  @param allowed_perceptions The allowed perceptions to the agent.
	 *  @param allowed_actions The allowed actions to the agent. */
	public SeasonalAgent(String label, Vertex vertex, PerceptionPermission[] allowed_perceptions, ActionPermission[] allowed_actions, EventTimeProbabilityDistribution death_tpd) {
		super(label, vertex, allowed_perceptions, allowed_actions);
		this.death_tpd = death_tpd;
	}
	
	/** Configures the society of the agent.
	 *  @param society The society of the agent. */
	public void setSociety(OpenSociety society) {
		this.society = society;		
	}
	
	/** Obtains a perpetual version of this agent.
	 * 
	 *  @return The perpetual agent correspondent to this one. */
	public PerpetualAgent getPerpetualVersion() {
		Object[] allowed_perceptions_array = this.allowed_perceptions.toArray();
		PerceptionPermission[] allowed_perceptions = new PerceptionPermission[allowed_perceptions_array.length];
		for(int i = 0; i < allowed_perceptions.length; i++)
			allowed_perceptions[i] = (PerceptionPermission) allowed_perceptions_array[i];
		
		Object[] allowed_actions_array = this.allowed_actions.toArray();
		ActionPermission[] allowed_actions = new ActionPermission[allowed_actions_array.length];
		for(int i = 0; i < allowed_actions.length; i++)
			allowed_actions[i] = (ActionPermission) allowed_actions_array[i];
		
		return new PerpetualAgent(this.label, this.vertex, allowed_perceptions, allowed_actions);
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// atualizes the answer, if necessary
		if(this.death_tpd != null) {
			// deletes the closing tag			
			if(this.allowed_perceptions == null && this.allowed_actions == null) {
				int last_valid_index = buffer.lastIndexOf("/>");
				buffer.delete(last_valid_index, buffer.length());
				buffer.append(">\n");
			}
			else {
				StringBuffer closing_tag = new StringBuffer();
				for(int i = 0; i < identation; i++) closing_tag.append("\t");
				closing_tag.append("</agent>");
				
				int last_valid_index = buffer.lastIndexOf(closing_tag.toString());
				buffer.delete(last_valid_index, buffer.length());
			}
			
			// writes the death tpd
			buffer.append(this.death_tpd.toXML(identation + 1));
			
			// closes the main tag
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</agent>\n");			
		}
		
		// returns the answer
		return buffer.toString();
	}
	
	public void die() {
		// removes the agent from its society
		this.society.removeAgent(this);
		
		// screen message
		System.out.println("[SimPatrol.Event] agent " + this.getObjectId() + " died.");
	}
	
	public EventTimeProbabilityDistribution getDeathTPD() {
		return this.death_tpd;
	}
}