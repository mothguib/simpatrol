/* MetricTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import model.metric.MaxIdlenessMetric;
import model.metric.MaxInstantaneousIdlenessMetric;
import model.metric.MeanIdlenessMetric;
import model.metric.MeanInstantaneousIdlenessMetric;
import model.metric.Metric;
import model.metric.MetricTypes;

/**
 * Implements a translator that obtains Metric objects from XML source elements.
 * 
 * @see Metric
 * @developer New Metric subclasses must change this class.
 */
public abstract class MetricTranslator extends Translator {
	/* Methods. */
	/**
	 * Obtains the metric objects from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the metric objects.
	 * @return The metric objects from the XML source.
	 * @developer New Metric subclasses must change this method.
	 */
	public static Metric[] getMetrics(Element xml_element) {
		// obtains the nodes with the "metric" tag
		NodeList metric_node = xml_element.getElementsByTagName("metric");

		// holds the obtained metric objects
		List<Metric> metrics = new LinkedList<Metric>();

		// for each metric_node
		for (int i = 0; i < metric_node.getLength(); i++) {
			// obtains the current metric element
			Element metric_element = (Element) metric_node.item(i);

			// obtains its data
			int type = Integer.parseInt(metric_element.getAttribute("type"));
			String str_value = metric_element.getAttribute("value");

			// depending on the type of the metric
			switch (type) {
			// if the metric is a mean instantaneous idleness metric
			case (MetricTypes.MEAN_INSTANTANEOUS_IDLENESS): {
				// puts the new metric on the answer of the method
				metrics.add(new MeanInstantaneousIdlenessMetric());

				// quits the switch structure
				break;
			}

				// if the metric is a max instantaneous idleness metric
			case (MetricTypes.MAX_INSTANTANEOUS_IDLENESS): {
				// puts the new metric on the answer of the method
				metrics.add(new MaxInstantaneousIdlenessMetric());

				// quits the switch structure
				break;
			}

				// if the metric is a mean idleness metric
			case (MetricTypes.MEAN_IDLENESS): {
				// obtains the eventual initial value of the metric
				double initial_value = 0;
				if (str_value.length() > 0)
					initial_value = Double.parseDouble(str_value);

				// puts the new metric on the answer of the method
				metrics.add(new MeanIdlenessMetric(initial_value));

				// quits the switch structure
				break;
			}

				// if the metric is a mean idleness metric
			case (MetricTypes.MAX_IDLENESS): {
				// obtains the eventual initial value of the metric
				double initial_value = 0;
				if (str_value.length() > 0)
					initial_value = Double.parseDouble(str_value);

				// puts the new metric on the answer of the method
				metrics.add(new MaxIdlenessMetric(initial_value));

				// quits the switch structure
				break;
			}

				// developer: new metrics must add code here
			}
		}

		// returns the answer
		return metrics.toArray(new Metric[0]);
	}
}