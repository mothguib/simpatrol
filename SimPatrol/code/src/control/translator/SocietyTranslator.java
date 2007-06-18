/* SocietyTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.ObjectIdGenerator;
import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Edge;
import model.graph.Vertex;

/** Implements a translator that obtains Society objects from
 *  XML sources.
 *  @see Society */
public abstract class SocietyTranslator extends Translator {
	/* Methods */
	/** Obtains the societies from the given XML element.
	 *  @param xml_element The XML source containing the societies.
	 *  @param vertexes The set of vertexes in a simulation.
	 *  @param edges The set of edges in a simulation.
	 *  @return The societies from the XML source. */		
	public static Society[] getSocieties(Element xml_element, Vertex[] vertexes, Edge[] edges) {
		// obtains the nodes with the "society" tag
		NodeList society_nodes = xml_element.getElementsByTagName("society");
		
		// the answer of the method
		Society[] answer = new Society[society_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current society element
			Element society_element = (Element) society_nodes.item(i);
			
			// obtains the data
			String id = society_element.getAttribute("id");
			String label = society_element.getAttribute("label");
			String str_is_closed = society_element.getAttribute("is_closed");
			
			// decides if the society is closed or not
			boolean is_closed = true;
			if(str_is_closed.length() > 0)
				is_closed = Boolean.parseBoolean(str_is_closed);
			
			// obtains the agents			
			boolean are_perpetual_agents = true; 
			if(!is_closed) are_perpetual_agents = false;
			Agent[] agents = getAgents(society_element, are_perpetual_agents, vertexes, edges);
			
			// creates the society
			Society society = null;
			if(is_closed) {
				PerpetualAgent[] perpetual_agents = new PerpetualAgent[agents.length];
				for(int j = 0; j < perpetual_agents.length; j++)
					perpetual_agents[j] = (PerpetualAgent) agents[j];
				
				society = new ClosedSociety(label, perpetual_agents);
			}
			else {
				SeasonalAgent[] seasonal_agents = new SeasonalAgent[agents.length];
				for(int j = 0; j < seasonal_agents.length; j++)
					seasonal_agents[j] = (SeasonalAgent) agents[j];
				
				society = new OpenSociety(label, seasonal_agents);
			}
			
			// configures the society and puts in the answer
			if(id.length() == 0) id = ObjectIdGenerator.generateObjectId(society);
			society.setObjectId(id);
			answer[i] = society;
		}
		
		// returns the answer
		return answer;
	}
		
	/** Obtains the agents from the given XML element.
	 *  @param xml_element The XML source containing the agents.
	 *  @param are_perpetual_agents TRUE, if the society of the agents is closed, FALSE if not.
	 *  @param vertexes The set of vertexes in a simulation.
	 *  @param edges The set of edges in a simulation.
	 *  @return The agents from the XML source. */
	private static Agent[] getAgents(Element xml_element, boolean are_perpetual_agents, Vertex[] vertexes, Edge[] edges) {
		// obtains the nodes with the "agent" tag
		NodeList agent_nodes = xml_element.getElementsByTagName("agent");
		
		// the answer of the method
		Agent[] answer = new Agent[agent_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current agent element
			Element agent_element = (Element) agent_nodes.item(i);
			
			// obtains the data
			String id = agent_element.getAttribute("id");
			String label = agent_element.getAttribute("label");
			String str_state = agent_element.getAttribute("state");
			String vertex_id = agent_element.getAttribute("vertex_id");
			String edge_id = agent_element.getAttribute("edge_id");
			String str_elapsed_length = agent_element.getAttribute("elapsed_length");
			String str_stamina = agent_element.getAttribute("stamina");
			
			// finds the vertex of the agent
			Vertex vertex = null;
			for(int j = 0; j < vertexes.length; j++)
				if(vertexes[j].getObjectId().equals(vertex_id)) {
					vertex = vertexes[j];
					break;
				}
			
			// finds the eventual edge of the agent
			Edge edge = null;
			if(edge_id != null)
				for(int j = 0; j < edges.length; j++)
					if (edges[j].getObjectId().equals(edge_id)) {
						edge = edges[j];
						break;
					}
			
			// instatiates the new agent
			Agent agent = null;
			if(are_perpetual_agents) agent = new PerpetualAgent(label, vertex);
			else agent = new SeasonalAgent(label, vertex, EventTimeProbabilityDistributionTranslator.getEventTimeProbabilityDistribution(agent_element)[0]);
			
			// configures the new agent
			if(id == null) id = ObjectIdGenerator.generateObjectId(agent);
			agent.setObjectId(id);
			
			if(str_state.length() > 0) agent.setState(Integer.parseInt(str_state));
			
			if(edge != null) {
				int elapsed_length = 0;
				if(str_elapsed_length.length() > 0)
					elapsed_length = Integer.parseInt(str_elapsed_length);
				
				agent.setEdge(edge, elapsed_length);
			}
			
			int stamina = 1;
			if(str_stamina.length() > 0) stamina = Integer.parseInt(str_stamina);
			agent.setStamina(stamina);
			
			// puts on the answer
			answer[i] = agent;
		}
		
		// returns the answer
		return answer;		
	}	
}