/* SocietyTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.etpd.EventTimeProbabilityDistribution;
import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.LimitationTypes;
import model.limitation.StaminaLimitation;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/** Implements a translator that obtains Society objects
 *  from XML source elements.
 *  
 *  @see Society
 *  @developer New Limitation subclasses must change this class. */
public abstract class SocietyTranslator extends Translator {
	/* Methods */
	/** Obtains societies from the pointed XML file.
	 * 
	 *  @param xml_file_path The path to the XML file containing the societies.
	 *  @param graph The graph of the simulation.
	 *  @return The societies from the XML file. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Society[] getSocieties(String xml_file_path, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		// parses the file containing the graph
		Element societies_element = parseFile(xml_file_path);
		
		// returns the obtained societies
		return getSocieties(societies_element, graph);
	}
	
	/** Obtains the societies from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the societies.
	 *  @param graph The graph of the simulation.
	 *  @return The societies from the XML source. */		
	public static Society[] getSocieties(Element xml_element, Graph graph) {
		// obtains the nodes with the "society" tag
		NodeList society_nodes = xml_element.getElementsByTagName("society");
		
		// the answer of the method
		Society[] answer = new Society[society_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current society element
			Element society_element = (Element) society_nodes.item(i);
			
			// obtains its data
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
			Agent[] agents = getAgents(society_element, are_perpetual_agents, graph);
			
			// creates the society (closed or open)
			Society society = null;
			if(is_closed) {
				// agents as perpetual agents
				PerpetualAgent[] perpetual_agents = new PerpetualAgent[agents.length];
				for(int j = 0; j < perpetual_agents.length; j++)
					perpetual_agents[j] = (PerpetualAgent) agents[j];
				
				// new closed society
				society = new ClosedSociety(label, perpetual_agents);
			}
			else {
				// agents as seasonal agents
				SeasonalAgent[] seasonal_agents = new SeasonalAgent[agents.length];
				for(int j = 0; j < seasonal_agents.length; j++)
					seasonal_agents[j] = (SeasonalAgent) agents[j];
				
				// new open society
				society = new OpenSociety(label, seasonal_agents);
			}
			
			// configures the society and puts in the answer
			society.setObjectId(id);
			answer[i] = society;
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the agents from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the agents.
	 *  @param are_perpetual_agents TRUE, if the society of the agents is closed, FALSE if not.
	 *  @param graph The graph of the simulation.
	 *  @return The agents from the XML source. */
	public static Agent[] getAgents(Element xml_element, boolean are_perpetual_agents, Graph graph) {
		// obtains the vertexes and edges of the graph
		Vertex[] vertexes = graph.getVertexes();
		Edge[] edges = graph.getEdges();
		
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
			if(edge_id.length() > 0)
				for(int j = 0; j < edges.length; j++)
					if (edges[j].getObjectId().equals(edge_id)) {
						edge = edges[j];
						break;
					}
			
			// instatiates the new agent (perpetual or seasonal)
			Agent agent = null;
			if(are_perpetual_agents) {
				// new perpetual agent
				agent = new PerpetualAgent(label, vertex, getAllowedPerceptions(agent_element), getAllowedActions(agent_element));
			}
			else {
				// obtains the eventual death time pd
				EventTimeProbabilityDistribution[] read_death_tpd = EventTimeProbabilityDistributionTranslator.getEventTimeProbabilityDistribution(agent_element);
				EventTimeProbabilityDistribution death_tpd = null;
				if(read_death_tpd.length > 0) death_tpd = read_death_tpd[0];
				
				// new seasonal agent
				agent = new SeasonalAgent(label, vertex, getAllowedPerceptions(agent_element), getAllowedActions(agent_element), death_tpd);
			}
			
			// configures the new agent...
			// id configuration
			agent.setObjectId(id);
			
			// state configuration
			if(str_state.length() > 0) agent.setState(Integer.parseInt(str_state));
			
			// edge and elapsed length configuration
			if(edge != null) {
				int elapsed_length = 0;
				if(str_elapsed_length.length() > 0)
					elapsed_length = Integer.parseInt(str_elapsed_length);
				
				agent.setEdge(edge, elapsed_length);
			}
			
			// stamina configuration
			if(str_stamina.length() > 0) agent.setStamina(Integer.parseInt(str_stamina));
			
			// puts on the answer
			answer[i] = agent;
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the allowed perceptions from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the perception permissions.
	 *  @return The allowed perceptions from the XML source. */
	private static PerceptionPermission[] getAllowedPerceptions(Element xml_element) {
		// obtains the nodes with the "allowed_perception" tag
		NodeList allowed_perception_nodes = xml_element.getElementsByTagName("allowed_perception");
		
		// the answer of the method
		PerceptionPermission[] answer = new PerceptionPermission[allowed_perception_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current allowed_perception element
			Element allowed_perception_element = (Element) allowed_perception_nodes.item(i);
			
			// obtains its data
			int perception_type = Integer.parseInt(allowed_perception_element.getAttribute("type"));

			// instatiates the new allowed_perception
			PerceptionPermission allowed_perception =  new PerceptionPermission(getLimitations(allowed_perception_element), perception_type);

			// puts on the answer
			answer[i] = allowed_perception;
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the allowed actions from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the action permissions.
	 *  @return The allowed actions from the XML source. */
	private static ActionPermission[] getAllowedActions(Element xml_element) {
		// obtains the nodes with the "allowed_action" tag
		NodeList allowed_action_nodes = xml_element.getElementsByTagName("allowed_action");
		
		// the answer of the method
		ActionPermission[] answer = new ActionPermission[allowed_action_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current allowed_perception element
			Element allowed_action_element = (Element) allowed_action_nodes.item(i);
			
			// obtains its data
			int action_type = Integer.parseInt(allowed_action_element.getAttribute("type"));

			// instatiates the new allowed_action
			ActionPermission allowed_action =  new ActionPermission(getLimitations(allowed_action_element), action_type);

			// puts on the answer
			answer[i] = allowed_action;
		}
		
		// returns the answer
		return answer;
	}	
	
	/** Obtains the limitations for perceptions/actions from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the limitations.
	 *  @return The limitations from the XML source.
	 *  @developer New Limitation subclasses must change this method. */
	private static Limitation[] getLimitations(Element xml_element) {
		// obtains the nodes with the "limitation" tag
		NodeList limitation_nodes = xml_element.getElementsByTagName("limitation");
		
		// the answer of the method
		Limitation[] answer = new Limitation[limitation_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current limitation element
			Element limitation_element = (Element) limitation_nodes.item(i);
			
			// obtains its data
			int limitation_type = Integer.parseInt(limitation_element.getAttribute("type"));

			// instatiates the new limitation
			// developer: new Limitation subclasses must change this code
			switch(limitation_type) {
				case(LimitationTypes.DEPTH_LIMITATION): {
					// obtains the parameters of the limitation
					int parameter = Integer.parseInt(getLimitationParameters(limitation_element)[0]);
					
					// new depth limitation
					answer[i] = new DepthLimitation(parameter);
					break;
				}
				case(LimitationTypes.STAMINA_LIMITATION): {
					// obtains the parameters of the limitation
					int parameter = Integer.parseInt(getLimitationParameters(limitation_element)[0]);
					
					// new stamina limitation
					answer[i] = new StaminaLimitation(parameter);
					break;
				}
			}
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the limitation parameters from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the limitation parameters.
	 *  @return The limitation parameters from the XML source. */
	private static String[] getLimitationParameters(Element xml_element) {
		// obtains the nodes with the "lmt_parameter" tag
		NodeList lmt_parameter_nodes = xml_element.getElementsByTagName("lmt_parameter");
		
		// the answer of the method
		String[] answer = new String[lmt_parameter_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current limitation parameter element
			Element lmt_parameter_element = (Element) lmt_parameter_nodes.item(i);
			
			// obtains its value and adds to the answer
			answer[i] = lmt_parameter_element.getAttribute("value");
		}
		
		// returns the answer
		return answer;
	}
}