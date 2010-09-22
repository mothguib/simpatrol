/* Translator.java */

/* The package of this class. */
package simpatrol.userclient.util;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/** Implements translators that obtain Java objects from XML files. */
public abstract class Translator {
	/* Methods. */
	/**
	 * Parses a given XML file.
	 * 
	 * @param xml_file_path
	 *            The path of the XML file containing the objects to be
	 *            translated.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Element parseFile(String xml_file_path)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xml_file_path);

		return doc.getDocumentElement();
	}

	/**
	 * Parses a given XML string.
	 * 
	 * @param xml_string
	 *            The string of the XML source containing the objects to be
	 *            translated.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Element parseString(String xml_string)
			throws ParserConfigurationException, SAXException, IOException {
		InputSource is = new InputSource(new StringReader(xml_string));
		
		DOMParser parser = new DOMParser();
		parser.parse(is);

		Document doc = parser.getDocument();
		return doc.getDocumentElement();
	}
}