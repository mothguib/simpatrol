/* MetricTranslator.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Metric} objects from XML source
 * elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class MetricTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the {@link Metric} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the {@link Metric} objects.
	 * 
	 * @return The {@link Metric} objects obtained from the XML source.
	 * 
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Metric> getMetrics(Element xmlElement)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "metric" tag
		NodeList metricNode = xmlElement.getElementsByTagName("metric");

		// holds all the obtained metrics
		List<Metric> answer = new ArrayList<Metric>(metricNode.getLength());

		// for each metric node
		for (int i = 0; i < metricNode.getLength(); i++) {
			// obtains the current metric element
			Element metricElement = (Element) metricNode.item(i);

			// obtains its data
			byte type = Byte.parseByte(metricElement.getAttribute("type"));
			String strValue = metricElement.getAttribute("value");

			// depending on the type of the metric...
			if (type == MetricTypes.MEAN_INSTANTANEOUS_IDLENESS.getType())
				answer.add(new MeanInstantaneousIdlenessMetric());

			else if (type == MetricTypes.MAX_INSTANTANEOUS_IDLENESS.getType())
				answer.add(new MaxInstantaneousIdlenessMetric());

			else if (type == MetricTypes.MEAN_IDLENESS.getType()) {
				// obtains the eventual initial value of the metric
				double initialValue = 0;
				if (strValue.length() > 0)
					initialValue = Double.parseDouble(strValue);

				// puts the new metric on the answer of the method
				answer.add(new MeanIdlenessMetric(initialValue));
			}

			else if (type == MetricTypes.MAX_IDLENESS.getType()) {
				// obtains the eventual initial value of the metric
				double initialValue = 0;
				if (strValue.length() > 0)
					initialValue = Double.parseDouble(strValue);

				// puts the new metric on the answer of the method
				answer.add(new MaxIdlenessMetric(initialValue));

			}

			// else, throws an exception
			else
				throw new XMLToObjectTranslationException(
						"Metric type not valid.");
		}

		// returns the answer
		return answer;
	}
}