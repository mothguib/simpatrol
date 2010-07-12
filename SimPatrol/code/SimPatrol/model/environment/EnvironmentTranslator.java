/* EnvironmentTranslator.java (2.0) */
package br.org.simpatrol.server.model.environment;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.agent.Society;
import br.org.simpatrol.server.model.agent.SocietyTranslator;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.graph.GraphTranslator;
import br.org.simpatrol.server.model.graph.GraphWithoutVertexesException;
import br.org.simpatrol.server.model.metric.Metric;
import br.org.simpatrol.server.model.metric.MetricTranslator;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Environment} objects from XML
 * source elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class EnvironmentTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains {@link Environment} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing {@link Environment} objects.
	 * 
	 * @return The {@link Environment} objects obtained from the XML source.
	 * 
	 * @throws GraphWithoutVertexesException
	 *             A graph must have at least one vertex.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Environment> getEnvironments(Element xmlElement)
			throws XMLToObjectTranslationException,
			GraphWithoutVertexesException {
		// obtains the nodes with the "environment" tag
		NodeList environmentNode = xmlElement
				.getElementsByTagName("environment");

		// the answer to the method
		List<Environment> answer = new ArrayList<Environment>(environmentNode
				.getLength());

		// the translator of graph objects
		GraphTranslator graphTranslator = new GraphTranslator();

		// the translator of society objects
		SocietyTranslator societyTranslator = new SocietyTranslator();

		// the translator of metric objects
		MetricTranslator metricTranslator = new MetricTranslator();

		// for each environment_node
		for (int i = 0; i < environmentNode.getLength(); i++) {
			// obtains the current environment element
			Element environmentElement = (Element) environmentNode.item(i);

			// obtains the graph of the environment
			Graph graph = graphTranslator.getGraphs(environmentElement).get(0);

			// obtains the societies of the environment
			List<Society> societies = societyTranslator.getSocieties(
					environmentElement, graph);

			// obtains the metrics of the environment
			List<Metric> metrics = metricTranslator
					.getMetrics(environmentElement);

			// adds the new environment to the answer
			answer.add(new Environment(graph, new HashSet<Society>(societies),
					new HashSet<Metric>(metrics)));
		}

		// returns the answer
		return answer;
	}
}