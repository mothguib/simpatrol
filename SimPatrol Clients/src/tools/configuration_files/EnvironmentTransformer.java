package tools.configuration_files;

import java.io.IOException;

import util.file.FileReader;
import util.file.FileWriter;

/**
 * Transforms an environment file from the old version (SimPatrol 1.0 and older) 
 * to the new one (SimPatrol 1.2). Changes include using "node" instead of
 * "vertex", for example.
 * 
 * TODO: Integrate with the Environment Editor.
 * 
 * @author Maira
 *
 */
public class EnvironmentTransformer {
	
	private String currentMap;
	
	private void setFile(String graphFilePath) {
		try {
			FileReader fileReader = new FileReader(graphFilePath);
			StringBuffer buffer = new StringBuffer();
			while (!fileReader.isEndOfFile()) {
				buffer.append(fileReader.readLine() + "\n");
			}
			fileReader.close();
			
			this.currentMap = buffer.toString();
		} catch (IOException e) {
			this.currentMap = null;
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts the environment file from the old (1.0) to the new format (1.2).
	 * This function assumes that, in the file, the nodes are given first, then the 
	 * edges, then the society.
	 */
	public void convertToNew() {
		String currentMapCopy = this.currentMap;
		this.currentMap = "";
		
		int nextIndex = currentMapCopy.indexOf("vertex id=\"");
		boolean node = true;
		boolean edge = false;
		while (nextIndex > -1) {
			
			if (node) {
				//vertex id="  ->  node id="
				this.currentMap += currentMapCopy.substring(0, nextIndex);
				this.currentMap += "node id=\"";
				currentMapCopy = currentMapCopy.substring(nextIndex+11);
			} else if (edge) {
				//emitter_id="  -> source="
				this.currentMap += currentMapCopy.substring(0, nextIndex);
				this.currentMap += "source=\"";
				currentMapCopy = currentMapCopy.substring(nextIndex+12);
				
				//collector_id="  ->  target="
				nextIndex = currentMapCopy.indexOf("collector_id=\"");
				this.currentMap += currentMapCopy.substring(0, nextIndex);
				this.currentMap += "target=\"";
				currentMapCopy = currentMapCopy.substring(nextIndex+14);
				
				//oriented="  ->  directed="
				nextIndex = currentMapCopy.indexOf("oriented=\"");
				this.currentMap += currentMapCopy.substring(0, nextIndex);
				this.currentMap += "directed=\"";
				currentMapCopy = currentMapCopy.substring(nextIndex+10);
			} else {
				//vertex_id="  -> node_id="
				this.currentMap += currentMapCopy.substring(0, nextIndex);
				this.currentMap += "node_id=\"";
				currentMapCopy = currentMapCopy.substring(nextIndex+11);
			}
			
			nextIndex = currentMapCopy.indexOf("vertex id=\"");
			if (nextIndex == -1) {
				nextIndex = currentMapCopy.indexOf("emitter_id=\"");
				node = false;
				edge = true;
				
				if (nextIndex == -1) {
					nextIndex = currentMapCopy.indexOf("vertex_id=\"");
					edge = false;
				}
			}
		}
		this.currentMap += currentMapCopy;
		
	}
	
	/**
	 * Erases the priority field. This is useful when using TestConfigurationGenerator, which
	 * assumes this field is not previously in the environment file.
	 */
	public void erasePriorityField() {
		String currentMapCopy = this.currentMap;
		this.currentMap = "";
		
		int nextIndex = currentMapCopy.indexOf("priority=\"");
		while (nextIndex > -1) {
			
			this.currentMap += currentMapCopy.substring(0, nextIndex);
			currentMapCopy = currentMapCopy.substring(nextIndex + 10);
			currentMapCopy = currentMapCopy.substring(currentMapCopy.indexOf("\" ") + 2);
			
			nextIndex = currentMapCopy.indexOf("priority=\"");
		}
		this.currentMap += currentMapCopy;
	}

	public void writeToFile(String newGraphFilePath) {
		try {
			FileWriter fileWriter = new FileWriter(newGraphFilePath);
			fileWriter.print(this.currentMap);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String baseDirectory = "res/environment_files";
		String[] mapNames = {"cc_a_5_i", "cc_a_5_ii",
				"cc_corridor_5_i", "hpcc_a_5_i", "hpcc_a_5_ii",
				"hpcc_b_5_i", "hpcc_circle_5_i", "hpcc_grid_5_i",
				"hpcc_islands_5_i", "sc_a_5_i", "sc_a_5_ii"};
		
		EnvironmentTransformer mapTransformer = new EnvironmentTransformer();
		for (int i = 0; i < mapNames.length; i++) {
			mapTransformer.setFile(baseDirectory + "_OLD/" + mapNames[i] + "_old.xml");
			mapTransformer.convertToNew();
			mapTransformer.erasePriorityField();
			mapTransformer.writeToFile(baseDirectory + "/" + mapNames[i] + ".xml");
		}
	}
}
