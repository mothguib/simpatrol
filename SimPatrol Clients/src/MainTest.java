import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import random_reactive.RandomReactiveClient;
import gravitational.version1.GravitationalMainClient1;
import gravitational.version2.GravitationalMainClient2;
import gray_box_learner.GrayBoxLearnerClient;
import heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatedClient;
import conscientious_reactive.ConscientiousReactiveClient;
import cognitive_coordinated.CognitiveCoordinatedClient;
import cycled.CycledClient;


/**
 * This class executes any of the available agents with a test configuration. 
 * 
 * @author Pablo
 */
public class MainTest {

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Choose the number of the type of agent you want to run:");
		System.out.println("\t1 - Cognitive Coordinated");
		System.out.println("\t2 - Conscientious Reactive");
		System.out.println("\t3 - TSP - Single Cycle");
		System.out.println("\t4 - Heuristic Pathfinder Cognitive Coordinated");
		System.out.println("\t5 - Gravitational I");
		System.out.println("\t6 - Gravitational II");
		System.out.println("\t7 - Gray Box Learner (training)");
		System.out.println("\t8 - Gray Box Learner");
		System.out.println("\t9 - Random Reactive");
		System.out.print  ("> ");
		
		int type = Integer.parseInt(reader.readLine());
		
		switch (type) {

		case 1:
			CognitiveCoordinatedClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\cc_test.xml",
					"tmp\\cc_log.txt",
					"100",
					"false"
				});
			break;
		
		case 2:
			ConscientiousReactiveClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\cr_test.xml",
					"tmp\\cr_log.txt",
					"100",
					"false"
				});
			break;
		
		case 3:
			CycledClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\sc_test.xml",
					"tmp\\sc_log.txt",
					"100",
					"false"
				});
			break;
		
		case 4:
			HeuristicCognitiveCoordinatedClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\hpcc_test.xml",
					"tmp\\hpcc_log.txt",
					"100",
					"false"
				});
			break;
		
		case 5:
			GravitationalMainClient1.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\grav1_test.xml",
					"tmp\\grav1_log.txt",
					"100",
					"Edge",
					"A",
					"2",
					"max"
				});
			break;
		
		case 6:
			GravitationalMainClient2.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\grav2_test.xml",
					"tmp\\grav2_log.txt",
					"100",
					"Edge",
					"A",
					"2",
					"max"
				});
			break;
		
		case 7:
			// NOT TESTED
			GrayBoxLearnerClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\gbla_test.xml",
					"tmp\\gbla_log.txt",
					"100",
					"false",
					"0.1",  // exploration
					"0.05", // delta a
					"0.9",  // discount factor
					"tmp\\qtable",
					"true",
					"4",    // max neighbors
					"1?",
					"2?",
					"3?"
				});
			break;
		
		case 8:
			// NOT TESTED
			GrayBoxLearnerClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\gbla_test.xml",
					"tmp\\gbla_log.txt",
					"100",
					"false",
					"0.1",  // exploration
					"0.05", // delta a
					"0.9",  // discount factor
					"tmp\\qtable",
					"false",
					"4",    // max neighbors
					"1?",
					"2?",
					"3?"
				});
			break;
			
		case 9:
			RandomReactiveClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"res\\configurations\\examples\\random_test.xml",
					"tmp\\random_log.txt",
					"100",
					"false"
				});
			break;
		
		default:
			System.out.println("Invalid option!");
		
		}
		
	}
	
}
