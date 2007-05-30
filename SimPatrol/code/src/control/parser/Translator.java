/* Translator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/** Implements translators that obtain objects from XML files. */
public abstract class Translator {	
	/* Methods. */
	/** Parses a given XML file.
	 *  @param xml_file_path The path of the XML file to be translated. */
	public static Element parse(String xml_file_path) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xml_file_path);
        
        return doc.getDocumentElement();		
	}
}