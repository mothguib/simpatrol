/* StigmaTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;
import model.stigma.Stigma;

/**
 * Implements a translator that obtains Stigma objects from XML source elements.
 * 
 * @see Stigma
 * @developer New Stigma subclasses must change this class.
 */
public abstract class StigmaTranslator extends Translator {
	/* Methods. */
	/**
	 * Obtains the stigmas from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the stigmas.
	 * @param graph
	 *            The graph of the simulation performed by SimPatrol.
	 * @return The stigmas from the XML source.
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 * @developer New Stigma subclasses must change this method.
	 */
	public static Stigma[] getStigmas(Element xml_element, Graph graph)
			throws NodeNotFoundException, EdgeNotFoundException {
		// obtains the nodes with the "stigma" tag
		NodeList stigma_node = xml_element.getElementsByTagName("stigma");

		// holds the obtained stigmas
		List<Stigma> stigmas = new LinkedList<Stigma>();

		// for each stigma_node
		for (int i = 0; i < stigma_node.getLength(); i++) {
			// obtains the current stigma element
			Element stigma_element = (Element) stigma_node.item(i);

			// obtains its data
			String node_id = stigma_element.getAttribute("node_id");
			String edge_id = stigma_element.getAttribute("edge_id");

			// if the node_id is valid
			if (node_id.length() > 0) {
				// finds the correspondent node in the graph
				Node node = null;

				Node[] nodes = graph.getNodes();
				for (int j = 0; j < nodes.length; j++)
					if (nodes[j].getObjectId().equals(node_id)) {
						node = nodes[j];
						break;
					}

				// if a valid node was found
				if (node != null)
					// adds a new stigma to the set of stigmas
					stigmas.add(new Stigma(node));
				// if not, throw exception
				else
					throw new NodeNotFoundException();
			}
			// if not
			else {
				// finds the correspondent edge in the graph
				Edge edge = null;

				Edge[] edges = graph.getEdges();
				for (int j = 0; j < edges.length; j++)
					if (edges[j].getObjectId().equals(edge_id)) {
						edge = edges[j];
						break;
					}

				// if a valid edge was found
				if (edge != null)
					// adds a new stigma to the set of stigmas
					stigmas.add(new Stigma(edge));
				// if not, throw exception
				else
					throw new EdgeNotFoundException();
			}
		}

		// returns the answer
		return stigmas.toArray(new Stigma[0]);
	}
}