import java.net.SocketException;
import control.simulator.*;

public class Teste {
	public static void main(String[] args) throws SocketException {		
		new CycledSimulator(5000, 0.25);
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
		
		/*try {
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
		}*/
	}
}
