/* SeasonalAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import model.etpd.EventTimeProbabilityDistribution;
import model.graph.Node;
import model.interfaces.Mortal;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/**
 * Implements the agents that compound the open societies of SimPatrol.
 */
public final class SeasonalAgent extends Agent implements Mortal {
	/* Attributes. */
	/** The probability distribution for the death time of the agent. */
	private EventTimeProbabilityDistribution death_tpd;

	/** The society of the agent. */
	private OpenSociety society;

	
	/*
	 * These attributes are NOT used by the simulator
	 * However they are used by other tools.
	 * These attributes are thus added to make a SINGLE package
	 * containing every needed thing to use the simulator in the 
	 * most efficient and easy way
	 */
	
	// this is the id of the society to join when the agent is inactive at the beginning
	public String Society_to_join;
	
	// this is the activating and deactivating time of the agent
	// if both are != -1, they must satisfy activating <= deactivating
	public int activating_time = -1;
	public int deactivating_time = -1;
	
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the agent.
	 * @param node
	 *            The node that the agent comes from.
	 * @param death_tpd
	 *            The probability distribution for the death time of the agent.
	 * @param allowed_perceptions
	 *            The allowed perceptions to the agent.
	 * @param allowed_actions
	 *            The allowed actions to the agent.
	 */
	public SeasonalAgent(String label, Node node,
			PerceptionPermission[] allowed_perceptions,
			ActionPermission[] allowed_actions,
			EventTimeProbabilityDistribution death_tpd) {
		super(label, node, allowed_perceptions, allowed_actions);
		this.death_tpd = death_tpd;
	}
	
	public SeasonalAgent(String label, Node node,
			PerceptionPermission[] allowed_perceptions,
			ActionPermission[] allowed_actions, 
			EventTimeProbabilityDistribution death_tpd,
			String SocToJoin, int activate, int deactivate) {
		this(label, node, allowed_perceptions, allowed_actions, death_tpd);
		this.Society_to_join = SocToJoin;
		this.activating_time = activate;
		this.deactivating_time = deactivate;
	}

	/**
	 * Configures the society of the agent.
	 * 
	 * @param society
	 *            The society of the agent.
	 */
	public void setSociety(OpenSociety society) {
		this.society = society;
	}
	
	
	public String getSocietyToJoin(){
		return this.Society_to_join;
	}
	
	public void setSocietyToJoin(String soc_id){
		this.Society_to_join = soc_id;
	}
	
	public int getActivatingTime(){
		return this.activating_time;
	}
	
	// this setter enforces activating_time < deactivating_time if deactivating_time != -1
	public void setActivatingTime(int time){
		if(this.deactivating_time == -1)
			this.activating_time = time;
		else
			if(time <= this.deactivating_time)
				this.activating_time = time;
	}
	
	public int getDeactivatingTime(){
		return this.deactivating_time;
	}
	
	// this setter enforces activating_time < deactivating_time if activating_time != -1
	public void setDeactivatingTime(int time){
		if(this.activating_time == -1)
			this.deactivating_time = time;
		else
			if(time >= this.activating_time)
				this.deactivating_time = time;
	}
	
	

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML(identation));

		// here we add the new attributes
		int agent_tag = buffer.indexOf(">");
		int agent_tag2 = buffer.indexOf("/>");
		if(agent_tag == agent_tag2 + 1)
			agent_tag = agent_tag - 2;
		else
			agent_tag = agent_tag - 1;
		String bufferstart = buffer.substring(0, agent_tag);
		String bufferend = buffer.substring(agent_tag);
		
		if(this.Society_to_join != null && this.Society_to_join.length() > 0)
			bufferstart += "\" society_to_join=\"" + this.Society_to_join;
		if(this.activating_time != -1)
			bufferstart += "\" activating_time=\"" + this.activating_time;
		if(this.deactivating_time != -1)
			bufferstart += "\" deactivating_time=\"" + this.deactivating_time;
		
		bufferstart += bufferend;
		buffer = new StringBuffer(bufferstart);
		
		
		// updates the answer, if necessary
		if (this.death_tpd != null) {
			// deletes the closing tag
			if (this.allowed_perceptions == null
					&& this.allowed_actions == null) {
				int last_valid_index = buffer.lastIndexOf("/>");
				buffer.delete(last_valid_index, buffer.length());
				buffer.append(">\n");
			} else {
				StringBuffer closing_tag = new StringBuffer();
				for (int i = 0; i < identation; i++)
					closing_tag.append("\t");
				closing_tag.append("</agent>");

				int last_valid_index = buffer.lastIndexOf(closing_tag
						.toString());
				buffer.delete(last_valid_index, buffer.length());
			}

			// writes the death tpd
			buffer.append(this.death_tpd.fullToXML(identation + 1));

			// closes the main tag
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</agent>\n");
		}

		// returns the answer
		return buffer.toString();
	}

	
	/**
	 * Ability for the seasonal agent to deactivate. 
	 * The agent is removed from its current society
	 */
	public void deactivate() {
		// removes the agent from its society
		this.society.removeAgent(this);
	}
	
	/**
	 * Ability for the seasonal agent to activate.
	 * The agent is added to its current society
	 */
	public boolean  activate(){
		return this.society.addAgent(this);
	}
	
	
	/**
	 * Is the agent inactive (i.e. in the InactiveSociety) ?
	 */
	public boolean isInactive(){
		return this.society.getObjectId().equals("InactiveSociety");
	}

	public EventTimeProbabilityDistribution getDeathTPD() {
		return this.death_tpd;
	}
}