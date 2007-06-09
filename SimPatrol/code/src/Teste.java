import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Graph;
import model.graph.Vertex;

import org.xml.sax.SAXException;

import util.etpd.SpecificEventTimeProbabilityDistribution;
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
		// leitura do grafo
		Graph grafo = GraphTranslator.getGraph("c:/teste.txt");
		System.out.println(grafo.toXML(0));
		Vertex[] vertexes = grafo.getVertexes();		
		
		// criação de uma sociedade fechada
		PerpetualAgent agent_11 = new PerpetualAgent(vertexes[0]);
		PerpetualAgent agent_12 = new PerpetualAgent(vertexes[0]);
		PerpetualAgent agent_13 = new PerpetualAgent(vertexes[0]);
		PerpetualAgent[] agents_1 = {agent_11, agent_12, agent_13};
		ClosedSociety closed_society = new ClosedSociety("perpétua", agents_1);
		System.out.println(closed_society.toXML(0));
		
		// criação de uma sociedade aberta
		SpecificEventTimeProbabilityDistribution etpd_1 = new SpecificEventTimeProbabilityDistribution(1000, 1, 100);
		SpecificEventTimeProbabilityDistribution etpd_2 = new SpecificEventTimeProbabilityDistribution(1000, 1, 50);
		SpecificEventTimeProbabilityDistribution etpd_3 = new SpecificEventTimeProbabilityDistribution(1000, 0, 10);
		SeasonalAgent agent_21 = new SeasonalAgent(vertexes[0], etpd_1);
		SeasonalAgent agent_22 = new SeasonalAgent(vertexes[0], etpd_2);
		SeasonalAgent agent_23 = new SeasonalAgent(vertexes[0], etpd_3);
		SeasonalAgent[] agents_2 = {agent_21, agent_22, agent_23};
		Vertex[] nest_vertexes = {vertexes[0]};
		OpenSociety open_society = new OpenSociety("sazonal", agents_2, nest_vertexes, 3);
		System.out.println(open_society.toXML(0));
		
		Society[] societies = {closed_society, open_society};
				
		// criação do simulador em ciclos
		//CycledSimulator simulator = new CycledSimulator(200, grafo, societies, null, null);
		//simulator.startSimulation();				
		
		// criação de um simulador rt
		RealTimeSimulator simulator = new RealTimeSimulator(120, grafo, societies, null, null);
		simulator.startSimulation();
		
		System.out.println(grafo.toXML(0));
		System.out.println(closed_society.toXML(0));
		System.out.println(open_society.toXML(0));
	}

}
