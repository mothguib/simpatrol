/* MessageTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import model.graph.Edge;
import model.graph.Vertex;
import model.interfaces.XMLable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import view.message.Message;
import view.message.MessageTypes;

/** Implements a translator that obtains messages from a given xml source.
 *  @see Message */
public abstract class MessageTranslator extends Translator {
	/* Methods. */
	/** Obtains the message from the given xml string.
	 *  @param xml_string The XML source containing the message to be translated.
	 *  @param vertexes The set of vertexes in a simulation.
	 *  @param edges The set of edges in a simulation.  
	 *  @return The message. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Message getMessage(String xml_string, Vertex[] vertexes, Edge[] edges) throws ParserConfigurationException, SAXException, IOException {
		// parses the string
		Element message_element = parseString(xml_string);
		
		// obtains the data
		int type = Integer.parseInt(message_element.getAttribute("type"));
		String sender_address = message_element.getAttribute("sender_address");
		String str_sender_socket = message_element.getAttribute("sender_socket");
		
		// obtains the socket number of the sender
		int sender_socket = -1;
		if(str_sender_socket.length() > 0) sender_socket = Integer.parseInt(str_sender_socket);
		
		// instantiates the new message
		Message answer = new Message(type, getContent(type, message_element, vertexes, edges), getParameters(message_element), sender_address, sender_socket);
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the content of a message from
	 *  the given XML element.
	 *  @param vertexes The set of vertexes in a simulation.
	 *  @param edges The set of edges in a simulation.
	 *  @param xml_element The XML element holding the content.
	 *  @param message_type The type of the message.
	 *  @see MessageTypes */
	private static XMLable getContent(int message_type, Element xml_element, Vertex[] vertexes, Edge[] edges) {
		switch(message_type) {
			case(MessageTypes.GRAPH_CREATION): {
				return GraphTranslator.getGraphs(xml_element)[0];
			}
			case(MessageTypes.SOCIETY_CREATION): {
				return SocietyTranslator.getSocieties(xml_element, vertexes, edges)[0];
			}
			default: return null;
		}		
	}
	
	/** Obtains the parameters of a message from
	 *  the given XML element.
	 *  @param xml_element The XML element containing the parameters. */
	private static String[] getParameters(Element xml_element) {
		// obtains the nodes with the "msg_parameter" tag
		NodeList parameter_nodes = xml_element.getElementsByTagName("msg_parameter");
		
		// the answer of the method
		String[] answer = new String[parameter_nodes.getLength()];
		
		// for each parameter node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current parameter element
			Element parameter_element = (Element) parameter_nodes.item(i);
			
			// obtains the data
			answer[i] = parameter_element.getAttribute("value");
		}
		
		// return the answer
		return answer;
	}
}