/* PerceptionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import model.graph.Graph;
import model.perception.EmptyPerception;
import model.perception.GraphPerception;
import model.perception.Perception;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Implements a translator that obtains Perception objects
 *  from XML source elements.
 *  @see Perception */
public abstract class PerceptionTranslator extends Translator {
	/* Methods. */
	/** Obtains the perceptions from the given XML element.
	 *  @param xml_element The XML source containing the perceptions.
	 *  @return The perceptions from the XML source. */
	public static Perception[] getPerceptions(Element xml_element) {
		// obtains the nodes with the "perception" tag
		NodeList perception_node = xml_element.getElementsByTagName("perception");
		
		// the answer to the method
		Perception[] answer = new Perception[perception_node.getLength()];
		
		// for each perception_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current perception element
			// TODO descomentar...
			Element perception_element = (Element) perception_node.item(i);
			
			// the current perception
			Perception perception = null;
			
			// 1st. tries to obtain a graph from the perception element
			if(perception == null) {
				Graph[] read_graph = EnvironmentTranslator.getGraphs(perception_element);
				if(read_graph.length > 0) perception = new GraphPerception(read_graph[0]);
			}
			
			// 2nd.
			// TODO prosseguir a obtencao dos varios tipos de percepcoes			
			
			// if perception is still null, so it's an empty perception
			if(perception == null)
			answer[i] = new EmptyPerception();
		}
		
		// returns the answer
		return answer;
	}
}