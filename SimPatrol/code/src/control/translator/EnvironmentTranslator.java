/* EnvironmentTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.etpd.EventTimeProbabilityDistribution;
import model.Environment;
import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.DynamicEdge;
import model.graph.DynamicVertex;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Stigma;
import model.graph.Vertex;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.LimitationTypes;
import model.limitation.StaminaLimitation;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/** Implements a translator that obtains Environment objects
 *  from XML source elements.
 *  @see Environment */
public abstract class EnvironmentTranslator extends Translator {
	/* Methods. */	
	/** Obtains the environment from the given XML file path.
	 *  @param xml_file_path The XML file source containing the environment.
	 *  @return The environment from the XML file source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Environment getEnvironment(String xml_file_path) throws ParserConfigurationException, SAXException, IOException {
		// parses the file containing the environment
		Element environment_element = parseFile(xml_file_path);
		
		// obtains the graph
		Graph graph = getGraphs(environment_element)[0];
		
		// obtains the societies
		Society[] societies = getSocieties(environment_element, graph);
		
		// creates the new graph and returns it
		return new Environment(graph, societies);
	}
	
	/** Obtains the environments from the given XML element.
	 *  @param xml_element The XML source containing the environments.
	 *  @return The environments from the XML source. */
	public static Environment[] getEnvironments(Element xml_element) {
		// obtains the nodes with the "environment" tag
		NodeList environment_node = xml_element.getElementsByTagName("environment");
		
		// the answer to the method
		Environment[] answer = new Environment[environment_node.getLength()];
		
		// for each environment_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current environment element
			Element environment_element = (Element) environment_node.item(i);
			
			// obtains the graph
			Graph graph = getGraphs(environment_element)[0];
			
			// obtains the societies
			Society[] societies = getSocieties(environment_element, graph);
			
			// creates the new graph and adds to the answer
			answer[i] = new Environment(graph, societies);
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the graphs from the given XML element.
	 *  @param xml_element The XML source containing the graphs.
	 *  @return The graphs from the XML source. */
	public static Graph[] getGraphs(Element xml_element) {
		// obtains the nodes with the "graph" tag
		NodeList graph_node = xml_element.getElementsByTagName("graph");
		
		// the answer to the method
		Graph[] answer = new Graph[graph_node.getLength()];
		
		// for each graph_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current graph element
			Element graph_element = (Element) graph_node.item(i);
			
			// obtains the its data
			String label = graph_element.getAttribute("label");		
			
			// obtains the vertexes
			Vertex[] vertexes = getVertexes(graph_element);
			
			// obtains the edges
			getEdges(graph_element, vertexes);
			
			// creates the new graph and adds to the answer
			answer[i] = new Graph(label, vertexes);
		}
		
		// returns the answer
		return answer;		
	}
	
	/** Obtains the vertexes from the given XML element.
	 *  @param xml_element The XML source containing the vertexes.
	 *  @return The vertexes from the XML source. */	
	private static Vertex[] getVertexes(Element xml_element) {
		// obtains the nodes with the "vertex" tag
		NodeList vertex_nodes = xml_element.getElementsByTagName("vertex");
		
		// the answer to the method
		Vertex[] answer = new Vertex[vertex_nodes.getLength()];
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current vertex element
			Element vertex_element = (Element)vertex_nodes.item(i);
			
			// obtains its data
			String id = vertex_element.getAttribute("id");
			String label = vertex_element.getAttribute("label");
			String str_priority = vertex_element.getAttribute("priority");
			String str_visibility = vertex_element.getAttribute("visibility");
			String str_idleness = vertex_element.getAttribute("idleness");
			String str_fuel = vertex_element.getAttribute("fuel");
			String str_is_appearing = vertex_element.getAttribute("is_appearing");
			
			// obtains the eventual stigmas
			Stigma[] stigmas = getStigmas(vertex_element);
			
			// obtains the eventual time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator.getEventTimeProbabilityDistribution(vertex_element);
			
			// instatiates the new vertex (normal or dynamic)
			Vertex current_vertex = null;
			if(etpds.length == 0) {
				// new normal vertex
				current_vertex = new Vertex(label);
			}
			else {
				// verifies if the vertex is appearing
				boolean is_appearing = true;
				if(str_is_appearing.length() > 0) is_appearing = Boolean.parseBoolean(str_is_appearing);
				
				// new dynamic vertex
				current_vertex = new DynamicVertex(label, etpds[0], etpds[1], is_appearing);
			}
			
			// configures the new vertex
			// id configuration
			current_vertex.setObjectId(id);
			
			// priority configuration
			int priority = 0;
			if(str_priority.length() > 0) priority = Integer.parseInt(str_priority);
			current_vertex.setPriority(priority);
			
			// visibility configuration
			boolean visibility = true;
			if(str_visibility.length() > 0) visibility = Boolean.parseBoolean(str_visibility);
			current_vertex.setVisibility(visibility);
			
			// idleness configuration
			int idleness = 0;			
			if(str_idleness.length() > 0) idleness = Integer.parseInt(str_idleness);
			current_vertex.setIdleness(idleness);
			
			// fuel configuration
			boolean fuel = false;
			if(str_fuel.length() > 0) fuel = Boolean.parseBoolean(str_fuel);
			current_vertex.setFuel(fuel);
			
			// stigma configuration
			current_vertex.setStigmas(stigmas);
			
			// adds the new vertex to the answer
			answer[i] = current_vertex;
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the edges from the given XML element.
	 *  @param xml_element The XML source containing the edges.
	 *  @param vertexes The set of vertexes read from the XML source.
	 *  @return The edges from the XML source. */
	private static Edge[] getEdges(Element xml_element, Vertex[] vertexes) {
		// obtains the nodes with the "edge" tag
		NodeList edge_nodes = xml_element.getElementsByTagName("edge");
		
		// the answer to the method
		Edge[] answer = new Edge[edge_nodes.getLength()];		
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current edge element
			Element edge_element = (Element) edge_nodes.item(i);
			
			// obtains its data
			String id = edge_element.getAttribute("id");
			String emitter_id = edge_element.getAttribute("emitter_id");
			String collector_id = edge_element.getAttribute("collector_id");
			String str_oriented = edge_element.getAttribute("oriented");
			double length = Double.parseDouble(edge_element.getAttribute("length"));
			String str_visibility = edge_element.getAttribute("visibility");
			String str_is_appearing = edge_element.getAttribute("is_appearing");
			String str_is_in_dynamic_emitter_memory = edge_element.getAttribute("is_in_dynamic_emitter_memory");
			String str_is_in_dynamic_collector_memory = edge_element.getAttribute("is_in_dynamic_collector_memory");
			
			// obtains the eventual stigmas
			Stigma[] stigmas = getStigmas(edge_element);						
			
			// obtains the eventual time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator.getEventTimeProbabilityDistribution(edge_element);
			
			// finds the correspondent emitter and collector vertexes
			Vertex emitter = null;
			Vertex collector = null;
			
			for(int j = 0; j < vertexes.length; j++) {
				Vertex current_vertex = vertexes[j];
								
				if(current_vertex.getObjectId().equals(emitter_id)) {
					emitter = current_vertex;
					if(collector != null) break;
				}
				
				if(current_vertex.getObjectId().equals(collector_id)) {
					collector = current_vertex;
					if(emitter != null) break;
				}					
			}
			
			// decides if the edge is oriented
			boolean oriented = false;
			if(str_oriented.length() > 0) oriented = Boolean.parseBoolean(str_oriented);
			
			// instantiates the new edge (normal or dynamic)
			Edge current_edge = null;
			if(etpds.length == 0) {
				// new normal edge
				current_edge = new Edge(emitter, collector, oriented, length);				
			}
			else {
				// decides if the edge is appearing
				boolean is_appearing = true;
				if(str_is_appearing.length() > 0) is_appearing = Boolean.parseBoolean(str_is_appearing);
				
				// new dynamic edge
				current_edge = new DynamicEdge(emitter, collector, oriented, length, etpds[0], etpds[1], is_appearing);
			}
			
			// configures the new edge
			// id configuration
			current_edge.setObjectId(id);
			
			// visibility configuration
			boolean visibility = true;
			if(str_visibility.length() > 0) visibility = Boolean.parseBoolean(str_visibility);
			current_edge.setVisibility(visibility);
			
			// stigmas configuration
			current_edge.setStigmas(stigmas);
			
			// decides if the edge is in the emitter and collector appearing memories
			boolean is_in_dynamic_emitter_memory = false;
			boolean is_in_dynamic_collector_memory = false;
			if(str_is_in_dynamic_emitter_memory.length() > 0) is_in_dynamic_emitter_memory = Boolean.parseBoolean(str_is_in_dynamic_emitter_memory);
			if(str_is_in_dynamic_collector_memory.length() > 0) is_in_dynamic_collector_memory = Boolean.parseBoolean(str_is_in_dynamic_collector_memory);
			
			// if the emitter is a dynamic vertex and
			// the current edge is in its memory of appearing edges
			if(emitter instanceof DynamicVertex && is_in_dynamic_emitter_memory)
				((DynamicVertex) emitter).addAppearingEdge(current_edge);
			
			// if the collector is a dynamic vertex and
			// the current edge is in its memory of appearing edges
			if(collector instanceof DynamicVertex && is_in_dynamic_collector_memory)
				((DynamicVertex) collector).addAppearingEdge(current_edge);
			
			// adds the new edge to the answer
			answer[i] = current_edge;
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the stigmas from the given vertex/edge element.
	 *  @param vertex_edge_element The XML source containing the stigmas.
	 *  @return The stigmas from the XML source. */
	private static Stigma[] getStigmas(Element vertex_edge_element) {
		// obtains the nodes with the "stigma" tag
		NodeList stigma_nodes = vertex_edge_element.getElementsByTagName("stigma");
		
		// the answer to the method
		Stigma[] answer = new Stigma[stigma_nodes.getLength()];
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// WARNING new stigma implementations shall reflect changes here
			
			// adds the new stigma to the answer
			answer[i] = new Stigma();
		}
		
		// returns the answer
		return answer;
	}
	
	/* Methods */
	/** Obtains the societies from the given XML element.
	 *  @param xml_element The XML source containing the societies.
	 *  @param graph The graph of the simulation.
	 *  @return The societies from the XML source. */		
	private static Society[] getSocieties(Element xml_element, Graph graph) {
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
			if(edge_id != null)
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
			
			// configures the new agent
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
	 *  @param xml_element The XML source containing the limitations.
	 *  @return The limitations from the XML source. */
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
				
				// WARNING new limitations shall introduce new code here
			}
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the limitation parameters from the given XML element.
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