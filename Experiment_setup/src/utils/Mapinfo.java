package utils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.DoubleList;
import util.EnvironmentTranslator;

public class Mapinfo {

	
	public static void MapInformation(String in_path) throws ParserConfigurationException, SAXException, IOException {
		
		util.Environment env = EnvironmentTranslator.getEnvironment(in_path);
		
		util.graph.Graph graph = env.getGraph();
		
		util.graph.Node[] nodes = graph.getNodes();
		
		DoubleList connect_list = new DoubleList();
		for(int i = 0; i < nodes.length; i++)
			connect_list.add(nodes[i].getEdges().length);
		
		System.out.print("This map has a maximum node connectivity of " + connect_list.max() + ".\n");
		System.out.print("This map has a medium node connectivity of " + connect_list.mean() + ".\n");
		System.out.print("This map has a std dev node connectivity of " + connect_list.standardDeviation() + ".\n");
		
		
		util.graph.Edge[] edges = graph.getEdges();
		DoubleList edgelength_list = new DoubleList();
		for(int i = 0; i < edges.length; i++)
			edgelength_list.add(edges[i].getLength());
		
		System.out.print("This map has longest edge of " + edgelength_list.max() + ".\n");
		System.out.print("This map has smallest edge of " + edgelength_list.min() + ".\n");
		System.out.print("This map has medium length of edges of " + edgelength_list.mean() + ".\n");
		System.out.print("This map has std dev length of edges of " + edgelength_list.standardDeviation() + ".\n");
		
		
		
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
