import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import control.simulator.RealTimeSimulator;

public class Teste {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {		
		new RealTimeSimulator(5000);
	}

}
