import java.io.IOException;
import java.net.SocketException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import model.Environment;
import model.agent.Agent;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.graph.Graph;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.permission.ActionPermission;

import view.gui.agent.AgentGUI;
import view.gui.etpd.EventTimeProbabilityDistributionGUI;
import view.gui.limitation.LimitationGUI;
import view.gui.permission.PermissionGUI;
import control.simulator.RealTimeSimulator;
import control.translator.EnvironmentTranslator;

public class Teste {
	public static void main(String[] args) throws SocketException {		
		//new RealTimeSimulator(5000, 0.5);
		/*EventTimeProbabilityDistributionGUI
		gui = new EventTimeProbabilityDistributionGUI();
		gui.setVisible(true);*/
	
		/*DepthLimitation limitation = new DepthLimitation(12);
		StaminaLimitation limitation_2 = new StaminaLimitation(5.5);
		LimitationGUI gui = new LimitationGUI(limitation_2);
		gui.setVisible(true);*/
		
		//DepthLimitation limitation = new DepthLimitation(12);
		//StaminaLimitation limitation_2 = new StaminaLimitation(5.5);
		//Limitation[] limitations = {limitation, limitation_2};
		//ActionPermission permission = new ActionPermission(null, 1);
		//PermissionGUI gui = new PermissionGUI(null, permission);
		//gui.setVisible(true);
		
		try {
			Environment env = EnvironmentTranslator.getEnvironment("c:/env.txt");
			Graph graph = env.getGraph();
			
			Agent agent = new SeasonalAgent("Daniel", graph.getVertexes()[0], null, null, null);
			agent.setObjectId("a1");
			AgentGUI gui = new AgentGUI(null, agent, graph);
			gui.setVisible(true);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
