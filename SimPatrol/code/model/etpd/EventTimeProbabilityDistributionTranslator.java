/* EventTimeProbabilityDistributionTranslator.java (2.0) */
package br.org.simpatrol.server.model.etpd;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains event time probability distributions
 * from a given XML source.
 * 
 * @see EventTimeProbabilityDistribution
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class EventTimeProbabilityDistributionTranslator extends
		XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the event time probability distributions from the given xml
	 * element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the etpds to be translated.
	 * 
	 * @return The event time probability distributions.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<EventTimeProbabilityDistribution> getEventTimeProbabilityDistribution(
			Element xmlElement) throws XMLToObjectTranslationException {
		// obtains the nodes with the "etpd" tag
		NodeList etpdNodes = xmlElement.getElementsByTagName("etpd");

		// the answer of the method
		List<EventTimeProbabilityDistribution> answer = new ArrayList<EventTimeProbabilityDistribution>(
				etpdNodes.getLength());

		// for each occurrence
		for (int i = 0; i < etpdNodes.getLength(); i++) {
			// obtains the current etpd element
			Element etpdElement = (Element) etpdNodes.item(i);

			// obtains the data
			int seed = Integer.parseInt(etpdElement.getAttribute("seed"));
			String strNextBoolCount = etpdElement
					.getAttribute("next_bool_count");
			String strSamplingTimeInterval = etpdElement
					.getAttribute("sampling_interval");
			byte type = Byte.parseByte(etpdElement.getAttribute("type"));

			// instantiates a new etpd
			EventTimeProbabilityDistribution etpd = null;
			if (type == EventTimeProbabilityDistributionTypes.EMPIRICAL
					.getType()) {
				etpd = new EmpiricalEventTimeProbabilityDistribution(seed, this
						.getETPDParameters(etpdElement));
			} else if (type == EventTimeProbabilityDistributionTypes.NORMAL
					.getType()) {
				double[] parameters = this.getETPDParameters(etpdElement);
				etpd = new NormalEventTimeProbabilityDistribution(seed,
						parameters[0], parameters[1]);
			} else if (type == EventTimeProbabilityDistributionTypes.SPECIFIC
					.getType()) {
				double[] parameters = this.getETPDParameters(etpdElement);
				etpd = new SpecificEventTimeProbabilityDistribution(seed,
						parameters[0], (int) parameters[1]);
			} else if (type == EventTimeProbabilityDistributionTypes.UNIFORM
					.getType()) {
				etpd = new UniformEventTimeProbabilityDistribution(seed, this
						.getETPDParameters(etpdElement)[0]);
			} else {
				throw new XMLToObjectTranslationException(
						"EventTimeProbabilityDistribution type does not exist.");
			}

			// configures the new etpd, if necessary
			if (strNextBoolCount.length() > 0)
				etpd.setNextBoolCounter(Integer.parseInt(strNextBoolCount));

			if (strSamplingTimeInterval.length() > 0)
				etpd.setSamplingTimeInterval(Double
						.parseDouble(strSamplingTimeInterval));

			// adds the new etpd to the answer
			answer.add(etpd);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the probability distribution parameters from a given etpd
	 * element.
	 * 
	 * @param etpdElement
	 *            The XML source containing the parameters of the etpd.
	 * @return The parameters of the etpd.
	 */
	private double[] getETPDParameters(Element etpdElement) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList etpdParameterNodes = etpdElement
				.getElementsByTagName("pd_parameter");

		// the answer for the method
		double[] answer = new double[etpdParameterNodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current etpd parameter element
			Element etpdParameterElement = (Element) etpdParameterNodes.item(i);

			// adds the current parameter value to the answer
			answer[i] = Double.parseDouble(etpdParameterElement
					.getAttribute("value"));
		}

		// returns the answer
		return answer;
	}
}