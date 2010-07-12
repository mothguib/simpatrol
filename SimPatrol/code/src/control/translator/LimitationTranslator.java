/* LimitationTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import model.limitation.AccelerationLimitation;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.LimitationTypes;
import model.limitation.SpeedLimitation;
import model.limitation.StaminaLimitation;

/**
 * Implements a translator that obtains Limitation objects from XML source
 * elements.
 * 
 * @see Limitation
 * @developer New Limitation subclasses must change this class.
 */
public abstract class LimitationTranslator extends Translator {
	/**
	 * Obtains the limitations for perceptions/actions from the given XML
	 * element.
	 * 
	 * @param xml_element
	 *            The XML source containing the limitations.
	 * @return The limitations from the XML source.
	 * @developer New Limitation subclasses must change this method.
	 */
	public static Limitation[] getLimitations(Element xml_element) {
		// obtains the nodes with the "limitation" tag
		NodeList limitation_nodes = xml_element
				.getElementsByTagName("limitation");

		// the answer of the method
		Limitation[] answer = new Limitation[limitation_nodes.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current limitation element
			Element limitation_element = (Element) limitation_nodes.item(i);

			// obtains its data
			int limitation_type = Integer.parseInt(limitation_element
					.getAttribute("type"));

			// instantiates the new limitation
			// developer: new Limitation subclasses must change this code
			switch (limitation_type) {
			case (LimitationTypes.DEPTH): {
				// obtains the parameters of the limitation
				int parameter = Integer
						.parseInt(getLimitationParameters(limitation_element)[0]);

				// new depth limitation
				answer[i] = new DepthLimitation(parameter);
				break;
			}
			case (LimitationTypes.STAMINA): {
				// obtains the parameters of the limitation
				int parameter = Integer
						.parseInt(getLimitationParameters(limitation_element)[0]);

				// new stamina limitation
				answer[i] = new StaminaLimitation(parameter);
				break;
			}
			case (LimitationTypes.SPEED): {
				// obtains the parameters of the limitation
				double parameter = Double
						.parseDouble(getLimitationParameters(limitation_element)[0]);

				// new speed limitation
				answer[i] = new SpeedLimitation(parameter);
				break;
			}
			case (LimitationTypes.ACCELERATION): {
				// obtains the parameters of the limitation
				double parameter = Double
						.parseDouble(getLimitationParameters(limitation_element)[0]);

				// new acceleration limitation
				answer[i] = new AccelerationLimitation(parameter);
				break;
			}
			}
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the limitation parameters from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the limitation parameters.
	 * @return The limitation parameters from the XML source.
	 */
	private static String[] getLimitationParameters(Element xml_element) {
		// obtains the nodes with the "lmt_parameter" tag
		NodeList lmt_parameter_nodes = xml_element
				.getElementsByTagName("lmt_parameter");

		// the answer of the method
		String[] answer = new String[lmt_parameter_nodes.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current limitation parameter element
			Element lmt_parameter_element = (Element) lmt_parameter_nodes
					.item(i);

			// obtains its value and adds to the answer
			answer[i] = lmt_parameter_element.getAttribute("value");
		}

		// returns the answer
		return answer;
	}
}
