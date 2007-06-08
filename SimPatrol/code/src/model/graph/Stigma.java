/* Stigma.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.interfaces.XMLable;

/** Implements an eventual stigma deposited by a patroller. */
public class Stigma implements XMLable {
	/* Attributes. */
	/** The object id of the stigma.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The object id of the agent that deposited
	 *  the stigma.
	 *  Not part of the patrol problem modelling. */
	private String agent_id;
	
	/** The agent patroller that deposited the stigma. */
	private Agent agent;
	
	/* Methods. */
	/** Constructor.
	 *  @param agent The agent patroller that deposited the stigma. */
	public Stigma(Agent agent) {
		this.agent = agent;
	}
	
	/** Constructor.
	 * 
	 *  Not part of the patrol problem modelling.
	 *  The stigma is created incomplete (without its agent)
	 *  needing posterior completion.
	 * 
	 *  @param agent_id The agent's id reference. */
	public Stigma(String agent_id) {
		this.agent_id = agent_id;
	}
	
	/** Completes the stigma object, searching among the given
	 *  agents, which one is its agent.
	 *  @param agents The agents to be searched. */
	public void completeStigma(Agent[] agents) {
		for(int i = 0; i < agents.length; i++)
			if(agents[i].getObjectId().equals(this.agent_id)) {
				this.agent = agents[i];
				return;
			}
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// checks if the stigma's agent is still active (not null)
		// if yes, obtais its id
		String agent_id = null;
		if(this.agent != null) agent_id = this.agent.getObjectId();
		
		// fills the buffer 
		buffer.append("<stigma id=\"" + this.id + 
				      "\" agent_id=\"" + agent_id +
				      "\"/>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}

	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;		
	}
}
