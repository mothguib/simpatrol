package utils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import model.Environment;
import org.xml.sax.SAXException;

import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import control.translator.EnvironmentTranslator;

public class Mapinfo {

	
	public static void MapInformation(String in_path) throws ParserConfigurationException, SAXException, IOException, NodeNotFoundException, EdgeNotFoundException {
		
		Environment env = EnvironmentTranslator.getEnvironment(in_path);
		
		model.graph.Graph graph = env.getGraph();
		
		model.graph.Node[] nodes = graph.getNodes();
		
		int connectivity = -1;
		double med_connectivity = 0;
		
		for(model.graph.Node node : nodes){
			med_connectivity += node.getEdges().length;
			if(node.getEdges().length > connectivity)
				connectivity = node.getEdges().length;
		}
		
		med_connectivity /= nodes.length;
		
		System.out.print("This map has a maximum node connectivity of " + connectivity + ".\n");
		System.out.print("This map has a medium node connectivity of " + med_connectivity + ".\n");

		
		double max_edge = -1;
		double min_edge = Double.MAX_VALUE;
		double med_edge = 0;
		
		model.graph.Edge[] edges = graph.getEdges();
		for(model.graph.Edge edge : edges){
			med_edge += edge.getLength();
			if(edge.getLength() > max_edge)
				max_edge = edge.getLength();
			if(edge.getLength() < min_edge)
				min_edge = edge.getLength();
		}
		
		med_edge /= edges.length;
		
		System.out.print("This map has longest edge of " + max_edge + ".\n");
		System.out.print("This map has smallest edge of " + min_edge + ".\n");
		System.out.print("This map has medium length of edges of " + med_edge + ".\n");
	}
	
	
	public static void main(String[] args) {

		try {
			String path = args[0];
			MapInformation(path);

		} catch (Exception e) {
			System.out
					.println("Usage \"java MapInfo\n"
							+ "<path to environment> \n" 
							+ "Calculates the maximum and medium node connectivity of the map given as argument, " +
									"and the max, min and medium length of edges.\"\n");
		}

	}
	
	
}
