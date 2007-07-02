/* RequisitionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import model.perception.Perception;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.requisition.Answer;
import control.requisition.Requisition;

/** Implements a translator that obtains requisitions and answers
 *  from a given XML source. */
public abstract class RequisitionTranslator extends Translator {
	/* Methods. */
	/** Obtains the requisitions from the given XML element.
	 *  @param xml_element The XML source containing the requisitions.
	 *  @return The requisitions from the XML source. */
	public static Requisition[] getRequisitions(Element xml_element) {
		// obtains the nodes with the "requisition" tag
		NodeList requisition_node = xml_element.getElementsByTagName("requisition");
		
		// the answer to the method
		Requisition[] answer = new Requisition[requisition_node.getLength()];
		
		// for each requisition_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current requisition element
			Element requisition_element = (Element) requisition_node.item(i);
			
			// obtains the type of the required perception
			int perception_type = Integer.parseInt(requisition_element.getAttribute("perception_type"));
			
			// puts the new requisition to the answer
			answer[i] = new Requisition(perception_type);
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the answers from the given XML element.
	 *  @param xml_element The XML source containing the answers.
	 *  @return The answers from the XML source. */
	public static Answer[] getAnswers(Element xml_element) {
		// obtains the nodes with the "answer" tag
		NodeList answer_node = xml_element.getElementsByTagName("answer");
		
		// the answer to the method
		Answer[] met_answer = new Answer[answer_node.getLength()];
		
		// for each answer_node
		for(int i = 0; i < met_answer.length; i++) {
			// obtains the current answer element
			Element answer_element = (Element) answer_node.item(i);
			
			// obtains its perception
			Perception perception = PerceptionTranslator.getPerceptions(answer_element)[0];
						
			// puts the new answer to the answer of the method
			met_answer[i] = new Answer(perception);
		}
		
		// returns the answer of the method
		return met_answer;
	}
}