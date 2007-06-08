/* GraphTranslator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.etpd.EventTimeProbabilityDistribution;
import model.graph.DynamicEdge;
import model.graph.DynamicVertex;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Stigma;
import model.graph.Vertex;

/** Implements a translator that obtains Graph objects from
 *  XML files or sources.
 *  @see Graph */
public abstract class GraphTranslator extends Translator {
	/* Methods. */
	/** Obtains the graph from a given XML source file.
	 *  @param xml_file_path The XML source file containing the graph.
	 *  @return The graph from the XML source file. */
	public static Graph getGraph(String xml_file_path) throws ParserConfigurationException, SAXException, IOException {
		// parses the xml file in a graph element
		Element graph_element = Translator.parse(xml_file_path);
		
		// obtains the data
		String id = graph_element.getAttribute("id");
		String label = graph_element.getAttribute("label");		
		
		// obtains the vertexes
		Vertex[] vertexes = getVertexes(graph_element);
		
		// obtains the edges
		getEdges(graph_element, vertexes);
		
		// creates the new graph and configures it
		Graph answer = new Graph(label, vertexes);
		answer.setObjectId(id);
		
		// returns the graph
		return answer;
	}
	
	/** Obtains the graphs from the given XML element.
	 *  @param xml_element The XML source containing the graphs.
	 *  @return The graphs from the XML source. */
	public static Graph[] getGraphs(Element xml_element) {
		// obtains the nodes with the "graph" tag
		NodeList graph_node = xml_element.getElementsByTagName("graph");
		
		// is there any graph node?
		if(graph_node.getLength() == 0)
			return new Graph[0];
		
		// the answer to the method
		Graph[] answer = new Graph[graph_node.getLength()];
		
		// for each graph_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current graph element
			Element graph_element = (Element) graph_node.item(i);
			
			// obtains the data
			String id = graph_element.getAttribute("id");
			String label = graph_element.getAttribute("label");		
			
			// obtains the vertexes
			Vertex[] vertexes = getVertexes(graph_element);
			
			// obtains the edges
			getEdges(graph_element, vertexes);
			
			// creates the new graph and configures it
			Graph graph = new Graph(label, vertexes);
			graph.setObjectId(id);
			
			// adds to the answer
			answer[i] = graph;
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
			
			// obtains the data
			String id = vertex_element.getAttribute("id");
			String label = vertex_element.getAttribute("label");
			int priority = Integer.parseInt(vertex_element.getAttribute("priority"));
			boolean visibility = Boolean.parseBoolean(vertex_element.getAttribute("visibility"));
			int idleness = Integer.parseInt(vertex_element.getAttribute("idleness"));
			boolean fuel = Boolean.parseBoolean(vertex_element.getAttribute("fuel"));
			boolean is_appearing = Boolean.parseBoolean(vertex_element.getAttribute("is_appearing"));
			
			// obtains the stigmas
			Stigma[] stigmas = getStigmas(vertex_element);
			
			// obtains the time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator.getEventTimeProbabilityDistribution(vertex_element);
			
			// instatiates the new vertex
			Vertex current_vertex = null;
			if(etpds.length == 0) current_vertex = new Vertex(label);
			else current_vertex = new DynamicVertex(label, etpds[0], etpds[1], is_appearing);
			
			// configures the new vertex
			current_vertex.setObjectId(id);
			current_vertex.setPriority(priority);
			current_vertex.setVisibility(visibility);
			current_vertex.setIdleness(idleness);
			current_vertex.setFuel(fuel);
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
		
		// is there any edge node?
		if(edge_nodes.getLength() == 0)
			return new Edge[0];
		
		// the answer to the method
		Edge[] answer = new Edge[edge_nodes.getLength()];		
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current edge element
			Element edge_element = (Element)edge_nodes.item(i);
			
			// obtains the data
			String id = edge_element.getAttribute("id");
			String emitter_id = edge_element.getAttribute("emitter_id");
			String collector_id = edge_element.getAttribute("collector_id");
			boolean oriented = Boolean.parseBoolean(edge_element.getAttribute("oriented"));
			double length = Double.parseDouble(edge_element.getAttribute("length"));
			boolean visibility = Boolean.parseBoolean(edge_element.getAttribute("visibility"));
			boolean is_appearing = Boolean.parseBoolean(edge_element.getAttribute("is_appearing"));
			boolean is_in_dynamic_emitter_memory = Boolean.parseBoolean(edge_element.getAttribute("is_in_dynamic_emitter_memory"));
			boolean is_in_dynamic_collector_memory = Boolean.parseBoolean(edge_element.getAttribute("is_in_dynamic_collector_memory"));
			
			// obtains the stigmas
			Stigma[] stigmas = getStigmas(edge_element);						
			
			// obtains the time probability distributions
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
			
			// instantiates the new edge
			Edge current_edge = null;
			if(etpds.length == 0) current_edge = new Edge(emitter, collector, oriented, length);
			else current_edge = new DynamicEdge(emitter, collector, oriented, length, etpds[0], etpds[1], is_appearing);
			
			// configures the new edge
			current_edge.setObjectId(id);
			current_edge.setVisibility(visibility);
			current_edge.setStigmas(stigmas);
			
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
		
		// is there any stigma node?
		if(stigma_nodes.getLength() == 0)
			return new Stigma[0];
		
		// the answer to the method
		Stigma[] answer = new Stigma[stigma_nodes.getLength()];
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current stigma element
			Element stigma_element = (Element)stigma_nodes.item(i);
			
			// obtains the data
			String id = stigma_element.getAttribute("id");
			String agent_id = stigma_element.getAttribute("agent_id");
			
			// instantiates the new stigma and configures it
			Stigma current_stigma = new Stigma(agent_id);
			current_stigma.setObjectId(id);
			
			//adds the new stigma to the answer
			answer[i] = current_stigma;
		}
		
		// returns the answer
		return answer;
	}
}
