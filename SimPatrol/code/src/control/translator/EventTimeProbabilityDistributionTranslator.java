/* EventTimeProbabilityDistributionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import model.etpd.EmpiricalEventTimeProbabilityDistribution;
import model.etpd.EventTimeProbabilityDistribution;
import model.etpd.EventTimeProbabilityDistributionTypes;
import model.etpd.NormalEventTimeProbabilityDistribution;
import model.etpd.SpecificEventTimeProbabilityDistribution;
import model.etpd.UniformEventTimeProbabilityDistribution;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implements a translator that obtains event time probability distributions
 * from a given XML source.
 * 
 * @see EventTimeProbabilityDistribution
 * @developer New EventTimeProbabilityDistribution subclasses must change this
 *            class.
 */
public abstract class EventTimeProbabilityDistributionTranslator extends
		Translator {
	/* Methods. */
	/**
	 * Obtains the event time probability distributions from the given xml
	 * element.
	 * 
	 * @param xml_element
	 *            The XML source containing the etpds to be translated.
	 * @return The event time probability distributions.
	 * @developer New EventTimeProbabilityDistribution subclasses must change
	 *            this method.
	 */
	public static EventTimeProbabilityDistribution[] getEventTimeProbabilityDistribution(
			Element xml_element) {
		// obtains the nodes with the "etpd" tag
		NodeList etpd_nodes = xml_element.getElementsByTagName("etpd");

		// the answer of the method
		EventTimeProbabilityDistribution[] answer = new EventTimeProbabilityDistribution[etpd_nodes
				.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current etpd element
			Element etpd_element = (Element) etpd_nodes.item(i);

			// obtains the data
			int seed = Integer.parseInt(etpd_element.getAttribute("seed"));
			String str_next_bool_count = etpd_element
					.getAttribute("next_bool_count");
			int type = Integer.parseInt(etpd_element.getAttribute("type"));

			// instantiates the new etpd and configures it
			switch (type) {
			case EventTimeProbabilityDistributionTypes.UNIFORM: {
				answer[i] = new UniformEventTimeProbabilityDistribution(seed,
						getETPDParameters(etpd_element)[0]);
				break;
			}
			case EventTimeProbabilityDistributionTypes.EMPIRICAL: {
				answer[i] = new EmpiricalEventTimeProbabilityDistribution(seed,
						getETPDParameters(etpd_element));
				break;
			}
			case EventTimeProbabilityDistributionTypes.NORMAL: {
				double[] parameters = getETPDParameters(etpd_element);
				answer[i] = new NormalEventTimeProbabilityDistribution(seed,
						parameters[0], parameters[1]);
				break;
			}
			case EventTimeProbabilityDistributionTypes.SPECIFIC: {
				double[] parameters = getETPDParameters(etpd_element);
				answer[i] = new SpecificEventTimeProbabilityDistribution(seed,
						parameters[0], (int) parameters[1]);
				break;
			}

				// developer: new event time probability distributions must add
				// code here
			}

			// configures the new etpd, if necessary
			if (str_next_bool_count.length() > 0)
				answer[i].setNext_bool_counter(Integer
						.parseInt(str_next_bool_count));
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the probability distribution parameters from a given etpd
	 * element.
	 * 
	 * @param etpd_element
	 *            The XML source containing the etpd's parameters.
	 * @return The etpd's parameters.
	 */
	private static double[] getETPDParameters(Element etpd_element) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList etpd_parameter_nodes = etpd_element
				.getElementsByTagName("pd_parameter");

		// the answer for the method
		double[] answer = new double[etpd_parameter_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current etpd parameter element
			Element etpd_parameter_element = (Element) etpd_parameter_nodes
					.item(i);

			// adds the current parameter value to the answer
			answer[i] = Double.parseDouble(etpd_parameter_element
					.getAttribute("value"));
		}

		// returns the answer
		return answer;
	}
}