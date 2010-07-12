import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import society.SocietyGUI;
import model.graph.Graph;
import control.translator.EnvironmentTranslator;
import model.Environment;
import model.agent.Society;


public class Teste {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		Environment env = EnvironmentTranslator.getEnvironment("c:/env.txt");
		Graph graph = env.getGraph();
		
		Society society = env.getSocieties()[0];
		SocietyGUI gui = new SocietyGUI(null, society, graph);
		gui.setVisible(true);
	}
}
