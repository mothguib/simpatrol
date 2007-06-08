/* SocietyTranslator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Edge;
import model.graph.Stigma;
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
	public Society[] getSocieties(Element xml_element, Vertex[] vertexes, Edge[] edges) {
		// obtains the nodes with the "society" tag
		NodeList society_nodes = xml_element.getElementsByTagName("society");
		
		// is there any society node?
		if(society_nodes.getLength() == 0)
			return new Society[0];
		
		// the answer of the method
		Society[] answer = new Society[society_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current society element
			Element society_element = (Element) society_nodes.item(i);
			
			// obtains the data
			String id = society_element.getAttribute("id");
			String label = society_element.getAttribute("label");
			boolean is_closed = Boolean.parseBoolean(society_element.getAttribute("is_closed"));
			int max_agents_count = Integer.parseInt(society_element.getAttribute("max_agents_cout"));
			
			// obtains the agents
			boolean are_perpetual_agents = true; 
			if(!is_closed) are_perpetual_agents = false;
			Agent[] agents = getAgents(society_element, are_perpetual_agents, vertexes, edges);
			
			// obtains the eventual nest vertexes references
			Vertex[] nest_vertexes = null;
			if(!is_closed) nest_vertexes = getNestVertexes(society_element, vertexes);
			
			// creates and configures the society
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
				
				society = new OpenSociety(label, seasonal_agents, nest_vertexes, max_agents_count);
			}
			society.setObjectId(id);
			
			// completes the enventual stigmas of the vertexes and edges
			// i. e. connects the stigma to its agent
			for(int j = 0; j < vertexes.length; j++) {
				Stigma[] stigmas = vertexes[j].getStigmas();
				
				for(int k = 0; k < stigmas.length; k++)
					stigmas[k].completeStigma(agents);
			}
			
			for(int j = 0; j < edges.length; j++) {
				Stigma[] stigmas = edges[j].getStigmas();
				
				for(int k = 0; k < stigmas.length; k++)
					stigmas[k].completeStigma(agents);
			}
						
			// puts in the answer
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
		
		// is there any agent node?
		if(agent_nodes.getLength() == 0)
			return new Agent[0];
		
		// the answer of the method
		Agent[] answer = new Agent[agent_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current agent element
			Element agent_element = (Element) agent_nodes.item(i);
			
			// obtains the data
			String id = agent_element.getAttribute("id");
			int state = Integer.parseInt(agent_element.getAttribute("state"));
			String vertex_id = agent_element.getAttribute("vertex_id");
			String edge_id = agent_element.getAttribute("edge_id");
			int elapsed_length = Integer.parseInt(agent_element.getAttribute("elapsed_length"));
			int stamina = Integer.parseInt(agent_element.getAttribute("stamina"));
			
			// finds the vertex of the agent
			Vertex vertex = null;
			for(int j = 0; j < vertexes.length; j++)
				if(vertexes[j].getObjectId().equals(vertex_id)) {
					vertex = vertexes[j];
					break;
				}
			
			// finds the eventual edge of the agent
			Edge edge = null;
			for(int j = 0; j < edges.length; j++)
				if(edges[j].getObjectId().equals(edge_id)) {
					edge = edges[j];
					break;
				}

			// instatiates and configures the new agent
			Agent agent = null;			
			if(are_perpetual_agents) agent = new PerpetualAgent(vertex);
			else agent = new SeasonalAgent(vertex, EventTimeProbabilityDistributionTranslator.getEventTimeProbabilityDistribution(agent_element)[0]);
			
			agent.setObjectId(id);
			agent.setState(state);
			agent.setEdge(edge, elapsed_length);
			agent.setStamina(stamina);
			
			// puts on the answer
			answer[i] = agent;
		}
		
		// returns the answer
		return answer;		
	}
	
	/** Obtains the nest vertexes of an eventual open society from
	 *  the given XML element.
	 *  @param xml_element The XML source containing the nest vertexes.
	 *  @param vertexes The set of vertexes in a simulation.
	 *  @return The nest vertexes of the open society. */
	private static Vertex[] getNestVertexes(Element xml_element, Vertex[] vertexes) {
		// obtains the nodes with the "nest_vertex" tag
		NodeList nest_vertex_nodes = xml_element.getElementsByTagName("nest_vertex");
		
		// is there any nest vertex?
		if(nest_vertex_nodes.getLength() == 0)
			return new Vertex[0];
		
		// the answer of the method
		Vertex[] answer = new Vertex[nest_vertex_nodes.getLength()];
		
		// for each node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current nest vertex element
			Element nest_vertex_element = (Element) nest_vertex_nodes.item(i);
			
			// obtains the data
			String vertex_id = nest_vertex_element.getAttribute("vertex_id");
			
			// finds the correspondent vertex
			for(int j = 0; j < vertexes.length; j++)
				if(vertexes[j].getObjectId().equals(vertex_id)) {
					answer[i] = vertexes[j];
					break;
				}
		}
		
		// return the answer
		return answer;
	}
}
