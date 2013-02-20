package util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;

import tools.graph_generator.RandomGraphGenerator;
import util.graph.Graph;

public class test {
	
	public static void test(String in_path) throws ParserConfigurationException, SAXException, IOException {
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
		
		
		HS_Graph graph2 = new HS_Graph("mygraph", graph.getNodes());
		System.out.print("This map has a diameter of " + graph2.getDiameter() + " (" + graph2.getHopDiameter() + " sauts).\n");
		System.out.println(graph2.hasGap());
		
		
		Graph graph3 = graph2.getDijkstraPath(graph2.getNode("v1"), graph2.getNode("v38"));
		System.out.print(graph3.fullToXML(0));
		
		HS_Graph graph4 = graph2.getFloydWarshallPath(graph2.getNode("v1"), graph2.getNode("v38"));
		System.out.print(graph4.fullToXML(0));
		
		
	}
	
	public static void test2(){
		RandomGraphGenerator generator = new RandomGraphGenerator();
		
		HS_Graph graph = new HS_Graph("test", generator.generateUndirected_noGap(500, 1, 7, 2, 22).getNodes());
		
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
		
		System.out.print("This map has a diameter of " + graph.getDiameter() + " (" + graph.getHopDiameter() + " sauts).\n");
		System.out.println(graph.hasGap());
		
		System.out.print(graph.fullToXML(0));
	}
	
	public static void test3(String in_path) throws ParserConfigurationException, SAXException, IOException, NodeNotFoundException, EdgeNotFoundException {
		util.Environment env = EnvironmentTranslator.getEnvironment(in_path);
		
		util.graph.Graph graph = env.getGraph();
		util.graph.Node[] nodes = graph.getNodes();
		HS_Graph graph2 = new HS_Graph("mygraph", graph.getNodes());
		
		
		model.Environment env2 = control.translator.EnvironmentTranslator.getEnvironment(in_path);
		model.graph.Graph graph1 = env2.getGraph();
		
		model.graph.Node node2 = null;
		for(int i = 0; i < graph1.getNodes().length; i++)
			if(graph1.getNodes()[i].getObjectId().equals("v31"))
				node2 = graph1.getNodes()[i];
		model.graph.Graph subgraph = graph1.getVisibleEnabledSubgraph(node2, 5);
		for(int i = 0; i < subgraph.getNodes().length; i++)
				System.out.print(subgraph.getNodes()[i].getObjectId() + ", ");
		
		System.out.println();
		
		for(int i = 0; i < graph2.getNodes().length; i++){
			double bla = graph2.getHopDistance(graph2.getNodes()[i], graph2.getNode("v31"));
			if(graph2.getHopDistance(graph2.getNodes()[i], graph2.getNode("v31")) < 5)
				System.out.print(graph2.getNodes()[i].getObjectId() + ", ");
		}
				
		System.out.println();
		
		Graph graph3 = graph2.getDijkstraPath(graph2.getNode("v31"), graph2.getNode("v23"));
		Graph graph4 = graph2.getFloydWarshallPath(graph2.getNode("v31"), graph2.getNode("v23"));
		
		HS_Graph graph5 = graph2.getSubgraphByHops(graph2.getNode("v31"), 5);
		System.out.println(graph5.hasGap());
		
		/*
		for(int i = 1; i <= 50; i++){
			for(int j = 1; j <=  50; j++)
				System.out.print(graph2.getDistance(graph2.getNode("v"+i), graph2.getNode("v"+j)) + ";");
			System.out.println();
		}
		
		for(int i = 1; i <= 50; i++){
			for(int j = 1; j <=  50; j++)
				System.out.print((int) (graph2.getHopDistance(graph2.getNode("v"+i), graph2.getNode("v"+j))) + ";");
			System.out.println();
		}
		*/
		
		
		
	}
	
	
	public static void main(String[] args) {

		try {
			String path = args[0];
			test3(path);

		} catch (Exception e) {
			System.out
					.println("Usage \"java MapInfo\n"
							+ "<path to environment> \n" 
							+ "Calculates the maximum and medium node connectivity of the map given as argument, " +
									"and the max, min and medium length of edges.\"\n");
		}

	}
	
}
