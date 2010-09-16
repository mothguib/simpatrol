package cycled;

import java.io.IOException;

import org.xml.sax.SAXException;

import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Vertex;

public class TSPSolver {
	public static void main(String[] args) throws IOException, SAXException {
		if (args.length > 0) {
			String graph_file_path = args[0];
			FileReader file_reader = new FileReader(graph_file_path);

			StringBuffer graph_buffer = new StringBuffer();
			while (graph_buffer.indexOf("</graph>") == -1)
				graph_buffer.append(file_reader.readLine());
			graph_buffer.append("</environment>");

			Graph graph = GraphTranslator.getGraphs(GraphTranslator
					.parseString(graph_buffer.toString()))[0];

			Vertex[] tsp_solution = null;
			double best_solution_length = Double.MAX_VALUE;

			for (int i = 0; i < 5000; i++) {
				System.out.println("i " + i);
				Vertex[] solution = graph.getTSPSolution2();
				double solution_length = 0;

				for (int j = 1; j < solution.length; j++)
					solution_length = solution_length
							+ Math.ceil(graph.getDijkstraPath(solution[j - 1],
									solution[j]).getEdges()[0].getLength());

				System.out.println("solution length " + solution_length);

				if (solution_length < best_solution_length) {
					tsp_solution = solution;
					best_solution_length = solution_length;
				}
			}

			System.out.println("Solution length " + best_solution_length);
			for (Vertex vertex : tsp_solution)
				System.out.print("\"" + vertex.getObjectId() + "\" ");
			System.out.println("ok");
		}
	}
}
