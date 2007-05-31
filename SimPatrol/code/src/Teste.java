import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import model.agent.ClosedSociety;
import model.agent.Society;
import model.graph.Graph;

import org.xml.sax.SAXException;

import control.parser.GraphTranslator;
import control.simulator.CycledSimulator;
import control.simulator.RealTimeSimulator;

public class Teste {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		/*double[] distrob = {0.23, 0.75, 0.80, 0.12};
		EmpiricalTimeProbabilityDistribution distro = new EmpiricalTimeProbabilityDistribution(1000, distrob);
		
		Vertex vertex_1 = new Vertex("A");
		Vertex vertex_2 = new Vertex("B");
		DynamicVertex vertex_3 = new DynamicVertex("C", distro, distro);
				
		Edge edge_1 = new Edge(vertex_1, vertex_2, 10);
		Edge edge_2 = new Edge(vertex_1, vertex_2, true, 12);
		Edge edge_3 = new Edge(vertex_1, vertex_3, true, 5);
		
		PerpetualAgent agent = new PerpetualAgent();
		Stigma stigma_1 = new Stigma(agent);
		
		HashSet<Stigma> stigmas = new HashSet<Stigma>();
		stigmas.add(stigma_1);
		
		vertex_3.setStigmas(stigmas);					
		
		HashSet<Vertex> vertexes = new HashSet<Vertex>();
		vertexes.add(vertex_1);
		vertexes.add(vertex_2);
		vertexes.add(vertex_3);
		
		Graph grafo = new Graph("teste", vertexes);
		System.out.println(grafo.toXML(0));*/
		
		//Graph grafo = GraphTranslator.getGraph("c:/teste.txt");
		//System.out.println(grafo.toXML(0));
		
		//ClosedSociety soc = new ClosedSociety();
		//CycledSimulator simulator = new CycledSimulator(5, null, null, null);
		//simulator.addSociety(soc);
		//soc.start();
		//simulator.startSimulation();
		//soc.stop();
		
		
		
		//RealTimeSimulator simulator = new RealTimeSimulator(60, null, null, null);
		//simulator.startSimulation();
	}

}
