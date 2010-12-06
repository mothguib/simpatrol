package utils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import model.Environment;
import model.agent.Agent;
import model.agent.Society;

import org.xml.sax.SAXException;

import util.file.FileWriter;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import control.translator.EnvironmentTranslator;

public class StartRandomizer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws EdgeNotFoundException 
	 * @throws NodeNotFoundException 
	 * @throws ParserConfigurationException 
	 */
	
	public static void RandomizeEnv(String in_path, String out_path) throws ParserConfigurationException, SAXException, IOException, NodeNotFoundException, EdgeNotFoundException {
		
		Environment env = EnvironmentTranslator.getEnvironment(in_path);
		
		model.graph.Graph graph = env.getGraph();
		Society society = env.getSocieties()[0];
		
		model.graph.Node[] nodes = graph.getNodes();
		for(Agent agent : society.getAgents()){
			int nodenum = nodes.length;
			while(nodenum == nodes.length)
				nodenum = (int)(Math.random()*nodes.length);
			agent.setNode(nodes[nodenum]);
		}
		
		FileWriter output_file = new FileWriter(out_path);
		output_file.print(env.fullToXML(0));
		output_file.close();

	}
	
	
	
	public static void main(String[] args) {

		try {
			String original = args[0];
			String name_proto = args[1];
			int startnum = Integer.parseInt(args[2]);
			int endnum  = Integer.parseInt(args[3]);

			for(int i = startnum; i <= endnum; i++){
				RandomizeEnv(original, name_proto + i + ".txt");
			}

		} catch (Exception e) {
			System.out
					.println("Usage \"java StartRandomizer\n"
							+ "<original environment> <name prototype for output files> <start number for output files>"
							+ "<end number for output files> \n" 
							+ "Creates ENDNUM-STARTNUM environment files from the original by randomizing\n" +
							" the node position of the agents \n");
		}

	}

}
