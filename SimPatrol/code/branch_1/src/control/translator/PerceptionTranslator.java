/* PerceptionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;
import model.graph.Graph;
import model.perception.GraphPerception;
import model.perception.Perception;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Implements a translator that obtains Perception objects
 *  from XML source elements.
 *  
 *  @see Perception
 *  @developer New Perception subclasses must change this class. */
public abstract class PerceptionTranslator extends Translator {
	/* Methods. */
	/** Obtains the perceptions from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the perceptions.
	 *  @return The perceptions from the XML source.
	 *  @developer New Perception subclasses must change this method. */
	public static Perception[] getPerceptions(Element xml_element) {
		// obtains the nodes with the "perception" tag
		NodeList perception_node = xml_element.getElementsByTagName("perception");
		
		// holds all the obtained perceptions
		List<Perception> perceptions = new LinkedList<Perception>();
		
		// for each perception_node
		for(int i = 0; i < perception_node.getLength(); i++) {
			// obtains the current perception element
			Element perception_element = (Element) perception_node.item(i);
			
			// the current perception to be obtained
			Perception perception = null;
			
			// 1st. tries to obtain a graph from the perception element
			if(perception == null) {
				Graph[] read_graph = GraphTranslator.getGraphs(perception_element);
				if(read_graph.length > 0) perception = new GraphPerception(read_graph[0]);
			}
			
			// 2nd.
			// developer: new perceptions must add code here
			
			// adds the current perception to the list of perceptions, if it's valid
			if(perception != null)
				perceptions.add(perception);			
		}
		
		// mounts and returns the answer
		Perception[] answer = new Perception[perceptions.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = perceptions.get(i);
		return answer;
	}
}