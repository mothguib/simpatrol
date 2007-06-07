/* OpenSociety.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import java.util.LinkedHashSet;
import java.util.Set;
import util.ipd.IntegerProbabilityDistribution;
import model.graph.Vertex;

/** Implements the open societies of agents of SimPatrol. */
public class OpenSociety extends Society {
	/* Attributes. */
	/** The vertexes where new agents are born. */
	private Set<Vertex> nest_vertexes;
	
	/** The probability distributions for each agent birth quantity per
	 *  nest vertex . */
	private Set<IntegerProbabilityDistribution> birth_quantity_ipds;
	
	/** The propability distribution for the life time of the new agents. */
	private IntegerProbabilityDistribution life_time_ipd;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the closed society.
	 *  @param seasonal_agents The seasonal agents that compound the open society.
	 *  @param nest_vertex The vertexes where new agents are born.
	 *  @param birth_quantity_ipds The probability distributions for each agent birth quantity per nest vertex.
	 *  @param life_time_ipd The propability distribution for the life time of the new agents. */
	public OpenSociety(String label, SeasonalAgent[] seasonal_agents, Vertex[] nest_vertexes, IntegerProbabilityDistribution[] birth_quantity_ipds, IntegerProbabilityDistribution life_time_ipd) {
		super(label, seasonal_agents);
		
		if(nest_vertexes != null && nest_vertexes.length > 0) {
			this.nest_vertexes = new LinkedHashSet<Vertex>();
			for(int i = 0; i < nest_vertexes.length; i++)
				this.nest_vertexes.add(nest_vertexes[i]);
		}
		else this.nest_vertexes = null;
		
		if(birth_quantity_ipds != null && birth_quantity_ipds.length > 0) {
			this.birth_quantity_ipds = new LinkedHashSet<IntegerProbabilityDistribution>();
			for(int i = 0; i < birth_quantity_ipds.length; i++)
				this.birth_quantity_ipds.add(birth_quantity_ipds[i]);
		}
		else this.birth_quantity_ipds = null;
		
		this.life_time_ipd = life_time_ipd;
	}
	
	/** Creates new agents, if it's the case. */
	public void createNewAgents() {
		// if nest vertexes and ipds are defined
		if(this.nest_vertexes != null && this.birth_quantity_ipds != null) {			
			Object[] nest_vertexes_array = this.nest_vertexes.toArray();
			Object[] birth_quantity_ipds_array = this.birth_quantity_ipds.toArray();
			
			// for each nest vertex
			for(int i = 0; i < nest_vertexes_array.length; i++) {
				// registers how many agents shall be created
				int new_agents_count = 0;

				if(i < birth_quantity_ipds_array.length)
					new_agents_count = ((IntegerProbabilityDistribution) birth_quantity_ipds_array[i]).nextInt();
				
				// creates each one of the new agents
				for(int j = 0; j < new_agents_count; j++) {
					// creates a new agent
					SeasonalAgent new_agent = new SeasonalAgent((Vertex) nest_vertexes_array[i], this.life_time_ipd.nextInt());
															
					// produces and configures an id for the new agent
					String id = new_agent.getClass().getName() + "@" + Integer.toHexString(new_agent.hashCode()) + "#" + String.valueOf(System.currentTimeMillis() + i + j);
					new_agent.setObjectId(id);
					
					// adds the new agent to the society
					this.agents.add(new_agent);
					
					// starts the new agent
					new_agent.start();
				}
			}
		}
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// changes the society type from 0 (closed) to 1 (open)
		int index_type = buffer.lastIndexOf("true");
		buffer.replace(index_type, 4, "false");

		// deletes the final closing tag "</society>"
		int index_final_tag = buffer.indexOf("</society>");
		buffer.delete(index_final_tag, buffer.length());
		
		// adds the ipd for the life time of the new agents
		buffer.append(this.life_time_ipd.toXML(identation + 1));
		
		// adds the nest vertex - ipd pairs
		if(this.nest_vertexes != null && this.birth_quantity_ipds != null) {
			Object[] nest_vertexes_array = this.nest_vertexes.toArray();
			Object[] birth_quantity_ipds_array = this.birth_quantity_ipds.toArray();
			
			// for each nest vertex
			for(int i = 0; i < nest_vertexes_array.length; i++) {
				// applies the identation
				for(int j = 0; j < identation + 1; j++)
					buffer.append("\t");
				
				// fills the buffer with the "nest_vertex_ipd_pair" tag
				buffer.append("<nest_vertex_ipd_pair vertex_id=\">" + ((Vertex) nest_vertexes_array[i]).getObjectId() +
						      "\">\n");
				if(i < birth_quantity_ipds_array.length)
					buffer.append(((IntegerProbabilityDistribution)birth_quantity_ipds_array[i]).toXML(identation + 2));
				
				for(int j = 0; j < identation + 1; j++)
					buffer.append("\t");
				
				buffer.append("</nest_vertex_ipd_pair vertex_id>\n");
			}
		}
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");		
		buffer.append("</society>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}
}
