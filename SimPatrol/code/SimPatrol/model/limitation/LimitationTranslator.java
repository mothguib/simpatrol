/* LimitationTranslator.java (2.0) */
package br.org.simpatrol.server.model.limitation;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Limitation} objects from XML
 * source elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class LimitationTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the {@link Limitation} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the limitations.
	 * 
	 * @return The limitations obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Limitation> getLimitations(Element xmlElement)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "limitation" tag
		NodeList limitationNodes = xmlElement
				.getElementsByTagName("limitation");

		// the answer for the method
		List<Limitation> answer = new ArrayList<Limitation>(limitationNodes
				.getLength());

		// for all the occurrences
		for (int i = 0; i < limitationNodes.getLength(); i++) {
			// obtains the current limitation element
			Element limitationElement = (Element) limitationNodes.item(i);

			// obtains its data
			byte limitationType = Byte.parseByte(limitationElement
					.getAttribute("type"));

			// instantiates the new limitation
			if (limitationType == LimitationTypes.DEPTH.getType()) {
				// obtains the parameters of the limitation
				int parameter = Integer.parseInt(this
						.getLimitationParameters(limitationElement)[0]);

				// new depth limitation
				answer.add(new DepthLimitation(parameter));
			}

			else if (limitationType == LimitationTypes.STAMINA.getType()) {
				// obtains the parameters of the limitation
				int parameter = Integer.parseInt(this
						.getLimitationParameters(limitationElement)[0]);

				// new stamina limitation
				answer.add(new StaminaLimitation(parameter));
			}

			else if (limitationType == LimitationTypes.SPEED.getType()) {
				// obtains the parameters of the limitation
				double parameter = Double.parseDouble(this
						.getLimitationParameters(limitationElement)[0]);

				// new speed limitation
				answer.add(new SpeedLimitation(parameter));
			}

			else if (limitationType == LimitationTypes.ACCELERATION.getType()) {
				// obtains the parameters of the limitation
				double parameter = Double.parseDouble(this
						.getLimitationParameters(limitationElement)[0]);

				// new acceleration limitation
				answer.add(new AccelerationLimitation(parameter));
			}

			// else, throws a limitation type not valid exception
			else
				throw new XMLToObjectTranslationException(
						"Limitation type not valid.");
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the parameters of a limitation from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the parameters of a limitation.
	 * 
	 * @return The parameters of a limitation from the given XML source.
	 */
	private String[] getLimitationParameters(Element xmlElement) {
		// obtains the nodes with the "lmt_parameter" tag
		NodeList lmtParameterNodes = xmlElement
				.getElementsByTagName("lmt_parameter");

		// the answer of the method
		String[] answer = new String[lmtParameterNodes.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current limitation parameter element
			Element lmtParameterElement = (Element) lmtParameterNodes.item(i);

			// obtains its value and adds to the answer
			answer[i] = lmtParameterElement.getAttribute("value");
		}

		// returns the answer
		return answer;
	}
}
