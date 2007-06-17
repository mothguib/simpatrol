/* OpenSociety.java */

/* The package of this class. */
package model.agent;

/** Implements the open societies of agents of SimPatrol. */
public final class OpenSociety extends Society {	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the closed society.
	 *  @param seasonal_agents The seasonal agents that compound the open society. */
	public OpenSociety(String label, SeasonalAgent[] seasonal_agents) {
		super(label, seasonal_agents);
		
		// for each agent, sets its society
		for(int i = 0; i < seasonal_agents.length; i++)
			seasonal_agents[i].setSociety(this);			
	}
	
	/** Removes a given agent from the society. 
	 *  @param agent The agent to be removed. */
	public void removeAgent(SeasonalAgent agent) {
		this.agents.remove(agent);
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// changes the society type
		int index_type = buffer.lastIndexOf("is_closed=\"true\"");
		if(index_type > -1) buffer.replace(index_type + 11, index_type + 11 + 4, "false");
		else {
			int index_bigger = buffer.indexOf(">");
			buffer.insert(index_bigger, "is_closed=\"false\"");
		}
		
		// returns the buffer content
		return buffer.toString();
	}
}