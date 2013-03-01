package tools.configurations_generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import tools.configurations_generator.client_types.CRClientType;
import tools.configurations_generator.client_types.GravClientType;
import tools.configurations_generator.client_types.HPCCClientType;
import tools.configurations_generator.client_types.SCClientType;


/**
 * Generates environment files with random priorities and random agents' positions, 
 * to help on the preparation of experiments.
 * 
 * TODO: Integrate with the Environment Editor.
 * 
 * @author Maira
 */
public class ConfigurationsGenerator {
	
	/** The path of the map files */
	private final String MAP_FILES_PATH = "maps\\map_";
	
	/** The path where files will be written */
	private final String CONFIGURATION_FILES_PATH = "output_configurations\\";
	
	/** Random numbers generator */
	private final Random random;
	
	/** The name of the map to be used */
	private String mapName;
	
	/** The maximum priority for the map's nodes */
	private int maxPriority;
	
	/** The maximum priority for the map's nodes */
	private int minPriority;

	/** Labels of all the vertices in the graph */
	private Vector<String> nodeLabels;
	
	/** Agent positions that were already defined */
	private Vector<String> definedPositions;

	public ConfigurationsGenerator() {
		this.random = new Random();
		this.definedPositions = new Vector<String>();
	}
	
	/**
	 * Defines, randomly, each node's priority and each agent's position.
	 * The same priority and position distribution is used for all client types.
	 * @param clientTypes
	 * @param mapName
	 * @param minPriority
	 * @param maxPriority
	 * @param variation
	 */
	public void generateConfigurations(List<ClientType> clientTypes, String mapName,
			int minPriority, int maxPriority, int variation) {
		this.mapName = mapName;
		this.maxPriority = maxPriority;
		this.minPriority = minPriority;
		
		this.nodeLabels = new Vector<String>();
		//this.definedPositions = new Vector<String>();
		
		//Format of the names of the configuration files generated:
		//<clientName>_<mapName>_<numberOfAgents>_<minPriority>to<maxPriority>_<variationNumber>.xml
		
		String fileName;
		ClientType client;
		
		for (int i = 0; i < clientTypes.size(); i++) {
			client = clientTypes.get(i);
			fileName = String.format("%1$s-%2$s-%3$02d-%4$dto%5$d-%6$d.xml", client.getName(), this.mapName,
									client.getAgentTypes()[0].getQuantity(),
									this.minPriority, this.maxPriority, variation);
			
			try {
				this.generateConfigurationFile(client, this.CONFIGURATION_FILES_PATH + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets predefined positions from the given file, which should be a text file with one
	 * valid node id per line. The number of positions in the file should be, at least,
	 * equal to the maximum number of agents which will be used on that map. If there is a
	 * coordinator, one more position should be given for it. 
	 *     
	 * @throws FileNotFoundException 
	 */
	public void loadPredefinedPositions(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		System.out.println("Loading positions from: " + file);		
		this.definedPositions = new Vector<>();
		
		String line = reader.readLine();
		while (line != null) {
			line = line.trim();
			if (! line.equals("")) {
				this.definedPositions.add(line);				
			}
			line = reader.readLine();
		}
		
		reader.close();
	}
	
	public void savePredefinedPositions(String file) throws IOException {
		PrintStream out = new PrintStream(file);
		
		for (String pos : this.definedPositions) {
			out.println(pos);
		}
		
		out.close();
	}
	
	private void generateConfigurationFile(ClientType clientType, String filePath) throws IOException {
		PrintStream fileWriter = new PrintStream(filePath);
		fileWriter.println("<environment>");
		fileWriter.print(this.processMap());
		fileWriter.println("\t<society id=\"sociedade\" label=\"sociedade\" " + 
				"is_closed=\"true\">");

		AgentType[] agentTypes = clientType.getAgentTypes();
		int currentAgent = 0;
		List<List<Integer>> allLimitations;
		List<Integer> limitations;

		for (int type = 0; type < agentTypes.length; type++) {
			for (int i = 0; i < agentTypes[type].getQuantity(); i++) {

				if (this.definedPositions.size() <= currentAgent) {
					int vertexIndex = this.random.nextInt(this.nodeLabels.size());
					this.definedPositions.add(this.nodeLabels.get(vertexIndex));
				}

				String agentLabel = agentTypes[type].getName();
				if (agentTypes[type].getQuantity() > 1) {
					agentLabel = String.valueOf((char)('a' + type)) + (i+1);
				}

				fileWriter.println("\t\t<agent id=\"" + agentLabel + "\" " +
						"label=\"" + agentLabel + "\" state=\"1\" " + 
						"node_id=\"" + this.definedPositions.get(currentAgent) + 
						"\" stamina=\"1.0\" max_stamina=\"1.0\">");

				int[] agentPerceptionTypes =
						agentTypes[type].getAllowedPerceptions();
				allLimitations = agentTypes[type].getAllowedPerceptionLimitations();
				for (int j = 0; j < agentPerceptionTypes.length; j++) {
					fileWriter.print(
							"\t\t\t<allowed_perception type=\"" +
									agentPerceptionTypes[j] + "\"");

					if (allLimitations.size() > j && allLimitations.get(j).size() > 0) {
						fileWriter.println(">");

						limitations = allLimitations.get(j);
						for (int k = 0; k < limitations.size(); k++) {
							fileWriter.println("\t\t\t\t<limitation type=\"0\">");
							fileWriter.println("\t\t\t\t\t<lmt_parameter value=\"" +
									limitations.get(k) + "\"/>");
							fileWriter.println("\t\t\t\t</limitation>");
						}

						fileWriter.println("\t\t\t</allowed_perception>");
					} else {
						fileWriter.println("/>");
					}
				}

				int[] agentActionTypes =
						agentTypes[type].getAllowedActions();
				allLimitations = agentTypes[type].getAllowedActionLimitations();
				for (int j = 0; j < agentActionTypes.length; j++) {
					fileWriter.print(
							"\t\t\t<allowed_action type=\"" +
									agentActionTypes[j] + "\"");

					if (allLimitations.size() > j && allLimitations.get(j).size() > 0) {
						fileWriter.println(">");

						limitations = allLimitations.get(j);
						for (int k = 0; k < limitations.size(); k++) {
							fileWriter.println("\t\t\t\t<limitation type=\"0\">");
							fileWriter.println("\t\t\t\t\t<lmt_parameter value=\"" +
									limitations.get(k) + "\"/>");
							fileWriter.println("\t\t\t\t</limitation>");
						}

						fileWriter.println("\t\t\t</allowed_action>");
					} else {
						fileWriter.println("/>");
					}
				}

				currentAgent++;

				fileWriter.println("\t\t</agent>");
			}
		}

		fileWriter.println("\t</society>");
		fileWriter.println("</environment>");
		fileWriter.flush();
		fileWriter.close();
	}
	
	/**
	 * Processes the desired map, storing all the node labels and generating random
	 * priorities for all those nodes.
	 * Note: this funcion adds the field "priority" to the map XML, assuming it's not
	 * already there.
	 * 
	 * @return mapString
	 * 		The map with random priorities associated to each of its nodes.
	 * @throws IOException 
	 */
	private String processMap() throws IOException {
		String mapString = "";
		
		BufferedReader fileReader = new BufferedReader(new java.io.FileReader(this.MAP_FILES_PATH + this.mapName + ".xml"));
		int nodeIndex;
		double priority;
		String nodeLabel;

		String fileLine = fileReader.readLine();		
		while (fileLine != null) {
			mapString += "\t";
			
			nodeIndex = fileLine.indexOf("node id=\"");
			if (nodeIndex > -1) {
				mapString += fileLine.substring(0, nodeIndex + 9);
				fileLine = fileLine.substring(nodeIndex + 9);
				
				nodeLabel = fileLine.substring(0, fileLine.indexOf("\""));
				mapString += nodeLabel;
				fileLine = fileLine.substring(fileLine.indexOf("\""));
				
				priority = random.nextDouble()*(this.maxPriority-this.minPriority) + this.minPriority;
				mapString += "\" priority=\"" + priority;
				
				this.nodeLabels.add(nodeLabel);
			}
			
			mapString += fileLine + "\n";
			fileLine = fileReader.readLine();
		}
		fileReader.close();
		
		return mapString;
	}
	
	public static void main(String[] args) throws IOException {
		String[][] GRAV_PARAMS = { {"Edge", "Node"},
			  {"Ar", "Ge"},
			  {"1.0", "2.0"},
			  {"max", "sum"}
			};

		ConfigurationsGenerator generator = new ConfigurationsGenerator();
	
		List<ClientType> clientTypes = new LinkedList<ClientType>();
		
		String[] mapNames = { "a", "grid", "islands", "cicles_corridor" , "city_traffic", "random_directed_1", "random_directed_2" };
		int[] agentNumbers = { 1, 6, 11, 16 };
		int[] maxNodePriorities = { 1 }; //priorities ranges from 1 to these values
		
		int numberOfRandomizations = 1;
		
		for (int m = 0; m < mapNames.length; m++) {
			
//			//loads positions previously generated -- should be done here, once for graph, in the future
//			generator.loadPredefinedPositions("_positions-" + mapNames[m] + ".txt");
			
			for (int a = 0; a < agentNumbers.length; a++) {
				
				//clientTypes.add(new CCClientType(agentNumbers[a]));
				clientTypes.add(new CRClientType(agentNumbers[a]));
				//clientTypes.add(new HPCCClientType(agentNumbers[a]));
				clientTypes.add(new SCClientType(agentNumbers[a]));
				
				//adiciona variantes do grav
//				for (int param0 = 0; param0 < GRAV_PARAMS[0].length; param0++) {
//					for (int param1 = 0; param1 < GRAV_PARAMS[1].length; param1++) {
//						for (int param2 = 0; param2 < GRAV_PARAMS[2].length; param2++) {
//							for (int param3 = 0; param3 < GRAV_PARAMS[3].length; param3++) {
//								clientTypes.add(new GravClientType(agentNumbers[a], GRAV_PARAMS[0][param0], GRAV_PARAMS[1][param1], GRAV_PARAMS[2][param2], GRAV_PARAMS[3][param3]));
//							}
//						}
//					}	
//				}

				for (int k = 0; k < maxNodePriorities.length; k++) {
					for (int id = 0; id < numberOfRandomizations; id++) {
						//loads positions previously generated
						generator.loadPredefinedPositions("_positions-" + mapNames[m] + ".txt");
						
						generator.generateConfigurations(clientTypes, mapNames[m], 1, maxNodePriorities[k], id);
					}
				}
				
				// should be done once for map (change, in the future)
				if (a == agentNumbers.length - 1) {
					//to save the positions used
					//generator.savePredefinedPositions("_positions-auto-" + mapNames[m] + ".txt");
				}
			}
			
		}
		
		System.out.println("Done!");
	}
}
