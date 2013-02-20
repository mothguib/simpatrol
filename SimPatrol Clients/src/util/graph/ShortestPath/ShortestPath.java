package util.graph.ShortestPath;

import java.util.LinkedList;

import util.graph.Graph;


/**
 * @author pouletc
 *
 */
public interface ShortestPath {

	public LinkedList<String> GetShortestPath(Graph graph, LinkedList<String> nodes);
}
