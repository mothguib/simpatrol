/* OpenSociety.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.graph.Vertex;

/** Implements the open societies of agents of SimPatrol. */
public class OpenSociety extends Society {
	/* Attributes. */
	/** The vertexes where new agents can be born. */
	private Set<Vertex> nest_vertexes;
	
	/** The maximum number of agents the society can have.
	 * 
	 *  If it's set to -1, there's no maximum.
	 * */
	private int max_agents_count;

	/* Methods. */
	/** Constructor.
	 *  @param label The label of the closed society.
	 *  @param seasonal_agents The seasonal agents that compound the open society.
	 *  @param nest_vertexes The vertexes where new agents can be born.
	 *  @param max_agents_count The maximum number of agents the society can have. If it's set to -1, there's no maximum. */
	public OpenSociety(String label, SeasonalAgent[] seasonal_agents, Vertex[] nest_vertexes, int max_agents_count) {
		super(label, seasonal_agents);
				
		for(int i = 0; i < seasonal_agents.length; i++)
			seasonal_agents[i].setSociety(this);
		
		if(nest_vertexes != null && nest_vertexes.length > 0) {
			this.nest_vertexes = new HashSet<Vertex>();
			for(int i = 0; i < nest_vertexes.length; i++)
				this.nest_vertexes.add(nest_vertexes[i]);
		}
		else this.nest_vertexes = null;
		
		this.max_agents_count = max_agents_count;		
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
		buffer.replace(index_type + 11, 4, "false");
		
		// changes the maximum number of agents
		int index_max_agents_count = buffer.lastIndexOf("max_agents_count=\"-1\"");
		buffer.replace(index_max_agents_count + 18, 2, String.valueOf(this.max_agents_count));

		// adds the nest vertexes, if necessary
		if(this.nest_vertexes != null) {
			// deletes the closing tag "</society>"
			StringBuffer closing_tag = new StringBuffer();			
			for(int i = 0; i < identation; i++) closing_tag.append("\t");
			closing_tag.append("</society>");
			
			int last_valid_index = buffer.indexOf(closing_tag.toString());
			buffer.delete(last_valid_index, buffer.length());
			
			// adds the nest vertexes tags
			Object[] vertexes_array = this.nest_vertexes.toArray();
			for(int i = 0; i < vertexes_array.length; i++) {
				// applies the identation
				for(int j = 0; j < identation + 1; j++)
					buffer.append("\t");
				
				// writes the "nest_vertex" tag
				buffer.append("<nest_vertex vertex_id = \"" + ((Vertex) vertexes_array[i]).getObjectId() + "\"/>\n");				
			}
			
			// finishes the buffer content
			for(int i = 0; i < identation; i++)
				buffer.append("\t");		
			buffer.append("</society>\n");
		}
		
		// returns the buffer content
		return buffer.toString();
	}
}
