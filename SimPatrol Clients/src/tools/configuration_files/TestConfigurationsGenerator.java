package tools.configuration_files;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import tools.configuration_files.client_types.AgentType;
import tools.configuration_files.client_types.CCClientType;
import tools.configuration_files.client_types.CRClientType;
import tools.configuration_files.client_types.ClientType;
import tools.configuration_files.client_types.HPCCClientType;
import tools.configuration_files.client_types.SCClientType;
import util.file.FileReader;
import util.file.FileWriter;

/**
 * Generates environment files with random priorities and random agents' positions, 
 * to help on the preparation of experiments.
 * 
 * TODO: Integrate with the Environment Editor.
 * 
 * @author Maira
 */
public class TestConfigurationsGenerator {
	
	/** The path of the map files */
	private final String MAP_FILES_PATH = "res/environment_files/maps/map_";
	
	/** The path of the map files */
	private final String CONFIGURATION_FILES_PATH = "res/environment_files/configurations/";
	
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
	private Vector<Integer> definedPositions;

	public TestConfigurationsGenerator() {
		this.random = new Random();
	}
	
	/**
	 * Defines, randomly, each node's priority and each agent's position.
	 * The same priority and position distribution is used for all client types.
	 * @param clientTypes
	 * @param mapName
	 * @param minPriority
	 * @param maxPriority
	 * @param configurationId
	 */
	public void generateConfiguration(ClientType[] clientTypes, String mapName,
			int minPriority, int maxPriority, int configurationId) {
		this.mapName = mapName;
		this.maxPriority = maxPriority;
		this.minPriority = minPriority;
		
		this.nodeLabels = new Vector<String>();
		this.definedPositions = new Vector<Integer>();
		
		//Generated environment file name format:
		//<clientName>_<mapName>_<numberOfAgents>_<minPriority>to<maxPriority>_<configurationNumber>.xml
		String fileName;
		for (int i = 0; i < clientTypes.length; i++) {
			fileName = clientTypes[i].getName() + "_" + this.mapName + "_" +
				clientTypes[i].getAgentTypes()[0].getQuantity() + "_" + this.minPriority +
				"to" + this.maxPriority + "_" + configurationId + ".xml";
			this.generateEnvironmentFile(clientTypes[i],
				this.CONFIGURATION_FILES_PATH + fileName);
		}
	}
	
	private void generateEnvironmentFile(ClientType clientType, String filePath) {
		try {
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.println("<environment>");
			fileWriter.print(this.processMap());
			fileWriter.println("\t<society id=\"sociedade\" label=\"sociedade\" " + 
				"is_closed=\"true\">");
			
			AgentType[] agentTypes = clientType.getAgentTypes();
			int currentAgent = 0;
			int vertexIndex;
			List<List<Integer>> allLimitations;
			List<Integer> limitations;
			for (int type = 0; type < agentTypes.length; type++) {
				for (int i = 0; i < agentTypes[type].getQuantity(); i++) {
					
					if (this.definedPositions.size() <= currentAgent) {
						this.definedPositions.add(random.nextInt(this.nodeLabels.size()));
					}
					
					vertexIndex = this.definedPositions.get(currentAgent);
					
					String agentLabel = agentTypes[type].getName();
					if (agentTypes[type].getQuantity() > 1) {
						agentLabel = String.valueOf((char)('a' + type)) + (i+1);
					}
					
					fileWriter.println("\t\t<agent id=\"" + agentLabel + "\" " +
						"label=\"" + agentLabel + "\" state=\"1\" " + 
						"node_id=\"" + this.nodeLabels.elementAt(vertexIndex) + 
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
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		
		FileReader fileReader = new FileReader(this.MAP_FILES_PATH + this.mapName + ".xml");
		String fileLine;
		int nodeIndex;
		double priority;
		String nodeLabel;
		while (!fileReader.isEndOfFile()) {
			fileLine = fileReader.readLine();
			
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
		}
		fileReader.close();
		
		return mapString;
	}
	
	public static void main(String[] args) throws IOException {
		TestConfigurationsGenerator testConfigurationsGenerator =
			new TestConfigurationsGenerator();
	
		ClientType[] clientTypes = new ClientType[4];
		
		String[] mapNames = {"a", "b", "circle", "corridor", "grid", "islands"};
		int[] agentNumbers = {5, 10};
		int[] maxPriorities = {1, 2, 5};
		
		int numberOfRandomizations = 3;
		
		for (int j = 0; j < mapNames.length; j++) {
			for (int j2 = 0; j2 < agentNumbers.length; j2++) {
				
				clientTypes[0] = new CCClientType(agentNumbers[j2]);
				clientTypes[1] = new CRClientType(agentNumbers[j2]);
				clientTypes[2] = new HPCCClientType(agentNumbers[j2]);
				clientTypes[3] = new SCClientType(agentNumbers[j2]);
				//Se for usar mais clientes depois, fazer sem o processamento do mapa e gerar
				//algo pra pegar as posicoes das configurações existentes para usar novamente
				
				for (int k = 0; k < maxPriorities.length; k++) {
					for (int k2 = 0; k2 < numberOfRandomizations; k2++) {
						testConfigurationsGenerator.generateConfiguration(
								clientTypes, mapNames[j], 1, maxPriorities[k], k2);
					}
				}
			}
		}
	}
}
