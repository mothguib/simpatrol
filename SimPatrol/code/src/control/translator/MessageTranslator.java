/* MessageTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import control.configuration.Configuration;
import view.message.ConfigurationMessage;
import view.message.Message;

/** Implements a translator that obtains messages from a given xml source.
 *  @see Message */
public abstract class MessageTranslator extends Translator {
	/* Methods. */
	/** Obtains the message from the given xml string.
	 *  @param xml_string The XML source containing the message to be translated.
	 *  @return The message. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Message getMessage(String xml_string) throws ParserConfigurationException, SAXException, IOException {		
		// parses the string
		Element message_element = parseString(xml_string);
		
		// obtains its content
		// tries to obtains an eventual configuration
		Configuration[] configurations = ConfigurationTranslator.getConfigurations(message_element);
		if(configurations.length > 0)
			return new ConfigurationMessage(configurations[0]);
		
		// TODO other tryings...
		
		// default answer
		return null;
	}
}