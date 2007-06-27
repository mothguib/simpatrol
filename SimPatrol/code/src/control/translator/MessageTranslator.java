/* MessageTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import model.graph.Graph;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import control.configuration.AgentCreationConfiguration;
import control.configuration.Configuration;
import control.configuration.Orientation;
import view.message.Message;

/** Implements a translator that obtains messages from a given xml source.
 *  @see Message */
public abstract class MessageTranslator extends Translator {
	/* Methods. */	
	/** Obtains the message from the given xml string, except
	 *  for messages containing an "agent creation configuration".
	 *  
	 *  To obtain messages with an "agent creation configuration",
	 *  use getAgentCreationConfigurationMessage() 
	 *  
	 *  @param xml_string The XML source containing the message to be translated.
	 *  @return The message. 
	 *  @throws IOException
	 *  @throws SAXException
	 *  @throws ParserConfigurationException
	 *  @see AgentCreationConfiguration */
	public static Message getMessage(String xml_string) throws ParserConfigurationException, SAXException, IOException {		
		// parses the string
		Element message_element = parseString(xml_string);
		
		// tries to obtain its content
		// 1st: tries to obtain a configuration
		// (except for an "agent creation configuration")
		Configuration[] configurations = ConfigurationTranslator.getConfigurations(message_element);
		if(configurations.length > 0) return new Message(configurations[0]);
		
		// 2nd: tries to obtain an orientation
		Orientation[] orientations = ConfigurationTranslator.getOrientations(message_element);
		if(orientations.length > 0) return new Message(orientations[0]);
		
		// TODO continuar e colocar na ordem ótima...
		// 3rd: tries to obtain a requisition
		// 4th: tries to obtain an answer
		// 5th: tries to obtain an intention		
		
		// default answer
		return null;
	}
	
	/** Obtains the message from the given xml string,
	 *  as a message with an "agent creation configuration".
	 *  @param xml_string The XML source containing the message to be translated.
	 *  @param graph The graph eventually obtained from a previous environment configuration.
	 *  @return The message. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Message getAgentCreationConfigurationMessage(String xml_string, Graph graph) throws ParserConfigurationException, SAXException, IOException {		
		// parses the string
		Element message_element = parseString(xml_string);
		
		// tries to obtain an "agent creation configuration"
		AgentCreationConfiguration[] configurations = ConfigurationTranslator.getAgentCreationConfigurations(message_element, graph);
		if(configurations.length > 0)
			return new Message(configurations[0]);
		else return null;
	}
}