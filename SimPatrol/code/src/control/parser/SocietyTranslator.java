/* SocietyTranslator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.ipd.IntegerProbabilityDistribution;
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
			// obtains the current agent element
			Element society_element = (Element) society_nodes.item(i);
			
			// obtains the data
			String id = society_element.getAttribute("id");
			String label = society_element.getAttribute("label");
			boolean is_closed = Boolean.parseBoolean(society_element.getAttribute("is_closed"));
			
			// obtains the agents
			boolean are_perpetual_agents = true; 
			if(!is_closed) are_perpetual_agents = false;
			Agent[] agents = getAgents(society_element, are_perpetual_agents, vertexes, edges);
			
			// obtains the eventual ipd for the life time of the agents
			IntegerProbabilityDistribution ipd_life_time = null;
			if(!is_closed) ipd_life_time = IntegerProbabilityDistributionTranslator.getIntegerProbabilityDistribution(society_element)[0];
			
			// obtains the eventual nest_vertex - ipd pairs
			Object[] nest_vertex_ipd_pairs = null;
			if(!is_closed) nest_vertex_ipd_pairs = getNestVertexes_IPDs(society_element, vertexes);
			
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
				
				society = new OpenSociety(label, seasonal_agents, (Vertex[]) nest_vertex_ipd_pairs[0], (IntegerProbabilityDistribution[]) nest_vertex_ipd_pairs[1], ipd_life_time);
			}
			society.setObjectId(id);
			
			// puts on the answer
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
			int life_time = Integer.parseInt(agent_element.getAttribute("life_time"));
			
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
			
			// instatiates and cofigures the new agent
			Agent agent = null;
			
			if(are_perpetual_agents) agent = new PerpetualAgent(vertex);
			else agent = new SeasonalAgent(vertex, life_time);
			
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
	
	/** Obtains the nest_vertex - ipd pairs from the given XML element.
	 *  @param xml_element The XML source containing the agents.
	 *  @param vertexes The set of vertexes in a simulation.
	 *  @return The nest vertexes and correspondent ipds from the XML source. */
	private static Object[] getNestVertexes_IPDs(Element xml_element, Vertex[] vertexes) {
		// The answer of the method
		Object[] answer = new Object[2];
		Vertex[] nest_vertexes = null;
		IntegerProbabilityDistribution[] ipds = null;
		
		// obtains the nodes with the "nest_vertex_ipd_pair" tag
		NodeList nest_vertex_ipd_nodes = xml_element.getElementsByTagName("nest_vertex_ipd_pair");
		
		// is there any nest_vertex - ipd pair?
		if(nest_vertex_ipd_nodes.getLength() == 0)
			return answer;
		else {
			nest_vertexes = new Vertex[nest_vertex_ipd_nodes.getLength()];
			ipds = new IntegerProbabilityDistribution[nest_vertex_ipd_nodes.getLength()];
		}
		
		// for each ocurrence of the pair
		for(int i = 0; i < nest_vertex_ipd_nodes.getLength(); i++) {
			// obtains the current nest_vertex - ipd pair element
			Element nest_vertex_ipd_element = (Element) nest_vertex_ipd_nodes.item(i);
			
			// obtains the nest vertex id
			String nest_vertex_id = nest_vertex_ipd_element.getAttribute("vertex_id");
			
			// obtains the ipd
			ipds[i] = IntegerProbabilityDistributionTranslator.getIntegerProbabilityDistribution(nest_vertex_ipd_element)[0]; 
			
			// obtains the nest vertex
			for(int j = 0; j < vertexes.length; j++)
				if(vertexes[j].getObjectId().equals(nest_vertex_id)) {
					nest_vertexes[i] = vertexes[j];
					break;
				}
		}		
		// returns the answer
		answer[0] = nest_vertexes;
		answer[1] = ipds;
		return answer;
	}
}
