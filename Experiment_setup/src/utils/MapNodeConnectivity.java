package utils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import model.Environment;
import org.xml.sax.SAXException;

import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import control.translator.EnvironmentTranslator;

public class MapNodeConnectivity {

	
	public static void nodeConnectivity(String in_path) throws ParserConfigurationException, SAXException, IOException, NodeNotFoundException, EdgeNotFoundException {
		
		Environment env = EnvironmentTranslator.getEnvironment(in_path);
		
		model.graph.Graph graph = env.getGraph();
		
		model.graph.Node[] nodes = graph.getNodes();
		
		int connectivity = -1;
		for(model.graph.Node node : nodes){
			if(node.getEdges().length > connectivity)
				connectivity = node.getEdges().length;
		}
		
		System.out.print("This map has a maximum node connectivity of " + connectivity + ".\n");

	}
	
	public static void main(String[] args) {

		try {
			String path = args[0];
			nodeConnectivity(path);

		} catch (Exception e) {
			System.out
					.println("Usage \"java MapNodeConnectivity\n"
							+ "<environment> \n" 
							+ "Calculates the maximum node connectivity of the map given as argument\"\n");
		}

	}
	
	
}
