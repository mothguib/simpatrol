/* GraphParser.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.tpd.TimeProbabilityDistribution;
import model.agent.Agent;
import model.graph.DynamicEdge;
import model.graph.DynamicVertex;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Stigma;
import model.graph.Vertex;

/** Implements a translator that obtains Graph objects from
 *  XML files.
 *  @see Graph */
public abstract class GraphTranslator extends Translator {
	/** Obtains the vertexes from the given XML source file.
	 *  @param xml_file_path The XML source file containing the graph. */	
	public static Graph getGraph(String xml_file_path) throws ParserConfigurationException, SAXException, IOException {
		// parses the xml file in a graph element
		Element graph_element = Translator.parse(xml_file_path);
		
		// obtains the data
		String id = graph_element.getAttribute("id");
		String label = graph_element.getAttribute("label");		
		
		// obtains the vertexes
		Set<Vertex> vertexes = getVertexes(graph_element);
		
		// obtains the edges
		getEdges(graph_element, vertexes);
		
		// creates the new graph and configures it
		Graph answer = new Graph(label, vertexes);
		answer.setObjectId(id);
		
		// returns the graph
		return answer;
	}
	
	/** Obtains the vertexes from the given XML element.
	 *  @param xml_element The XML source containing the vertexes. */	
	private static Set<Vertex> getVertexes(Element xml_element) {
		// the answer for the method
		Set<Vertex> vertexes = new HashSet<Vertex>();
		
		// obtains the nodes with the "vertex" tag
		NodeList vertex_nodes = xml_element.getElementsByTagName("vertex");
		
		// for each ocurrence
		for(int i = 0; i < vertex_nodes.getLength(); i++) {
			// obtains the current vertex element
			Element vertex_element = (Element)vertex_nodes.item(i);
			
			// obtains the data
			String id = vertex_element.getAttribute("id");
			String label = vertex_element.getAttribute("label");
			int priority = Integer.parseInt(vertex_element.getAttribute("priority"));
			boolean visibility = Boolean.parseBoolean(vertex_element.getAttribute("visibility"));
			int idleness = Integer.parseInt(vertex_element.getAttribute("idleness"));
			boolean fuel = Boolean.parseBoolean(vertex_element.getAttribute("fuel"));
			
			// obtains the stigmas
			Set<Stigma> stigmas = getStigmas(vertex_element);
			
			// obtains the time probability distributions
			TimeProbabilityDistribution[] tpds = TimeProbabilityDistributionTranslator.getTimeProbabilityDistribution(vertex_element);
			
			// instatiates the new vertex
			Vertex current_vertex = null;
			if(tpds == null) current_vertex = new Vertex(label);
			else current_vertex = new DynamicVertex(label, tpds[0], tpds[1]);
			
			// configures the new vertex
			current_vertex.setObjectId(id);
			current_vertex.setPriority(priority);
			current_vertex.setVisibility(visibility);
			current_vertex.setIdleness(idleness);
			current_vertex.setFuel(fuel);
			current_vertex.setStigmas(stigmas);
			
			// adds the new vertex to the set of vertexes
			vertexes.add(current_vertex);
		}
		
		// returns the answer
		if(vertexes.size() == 0) return null;
		else return vertexes;
	}

	/** Obtains the edges from the given XML element.
	 *  @param xml_element The XML source containing the edges.
	 *  @param vertexes The set of vertexes read from the XML source. */	
	private static Set<Edge> getEdges(Element xml_element, Set<Vertex> vertexes) {
		// the answer for the method
		Set<Edge> edges = new HashSet<Edge>();
		
		// obtains the nodes with the "edge" tag
		NodeList edge_nodes = xml_element.getElementsByTagName("edge");
		
		// for each ocurrence
		for(int i = 0; i < edge_nodes.getLength(); i++) {
			// obtains the current edge element
			Element edge_element = (Element)edge_nodes.item(i);
			
			// obtains the data
			String id = edge_element.getAttribute("id");
			String emitter_id = edge_element.getAttribute("emitter_id");
			String collector_id = edge_element.getAttribute("collector_id");
			boolean oriented = Boolean.parseBoolean(edge_element.getAttribute("oriented"));
			double length = Double.parseDouble(edge_element.getAttribute("length"));
			boolean visibility = Boolean.parseBoolean(edge_element.getAttribute("visibility"));
			
			// obtains the stigmas
			Set<Stigma> stigmas = getStigmas(edge_element);						
			
			// obtains the time probability distributions
			TimeProbabilityDistribution[] tpds = TimeProbabilityDistributionTranslator.getTimeProbabilityDistribution(edge_element);
			
			// finds the correspondent emitter ans collector vertexes
			Vertex emitter = null;
			Vertex collector = null;			
			Object[] vertexes_array = vertexes.toArray();
			
			for(int j = 0; j < vertexes_array.length; j++) {
				Vertex current_vertex = (Vertex) vertexes_array[j];
								
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
			if(tpds == null) current_edge = new Edge(emitter, collector, oriented, length);
			else current_edge = new DynamicEdge(emitter, collector, oriented, length, tpds[0], tpds[1]);
						
			// configures the new edge
			current_edge.setObjectId(id);
			current_edge.setVisibility(visibility);
			current_edge.setStigmas(stigmas);
			
			// adds the new edge to the set of edges
			edges.add(current_edge);
		}
		
		// returns the answer
		if(edges.size() == 0) return null;
		else return edges;
	}
	
	/** Obtains the stigmas from the given vertex/edge element.
	 *  @param vertex_edge_element The XML source containing the stigmas. */
	private static Set<Stigma> getStigmas(Element vertex_edge_element) {
		// the answer of the method
		Set<Stigma> stigmas = new HashSet<Stigma>();
		
		// obtains the nodes with the "stigma" tag
		NodeList stigma_nodes = vertex_edge_element.getElementsByTagName("stigma");
		
		// for each ocurrence
		for(int i = 0; i < stigma_nodes.getLength(); i++) {
			// obtains the current stigma element
			Element stigma_element = (Element)stigma_nodes.item(i);
			
			// obtains the data
			String id = stigma_element.getAttribute("id");
			
			// obtains the agent
			// TODO chamar AgentTranslator!!
			Agent agent = null;
			
			// instantiates the new stigma and cofigures it
			Stigma current_stigma = new Stigma(agent);
			current_stigma.setObjectId(id);
			
			//adds the new stigma to the stigmas set
			stigmas.add(current_stigma);
		}
		
		// returns the answer
		if(stigmas.size() == 0) return null;
		else return stigmas;
	}
}
