import random_reactive.RandomReactiveClient;
import gravitational.version1.GravitationalMainClient1;
import gravitational.version2.GravitationalMainClient2;
import gray_box_learner.GrayBoxLearnerClient;
import heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatedClient;
import conscientious_reactive.ConscientiousReactiveClient;
import cognitive_coordinated.CognitiveCoordinatedClient;
import cycled.CycledClient;


public class Main {

	public static void main(String[] args) {
		
//		CognitiveCoordinatedClient.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\cc_test.xml",
//				"tmp\\cc_log.txt",
//				"100",
//				"false"
//			});
		
//		ConscientiousReactiveClient.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\cr_test.xml",
//				"tmp\\cr_log.txt",
//				"100",
//				"false"
//			});

//		CycledClient.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\sc_test.xml",
//				"tmp\\sc_log.txt",
//				"100",
//				"false"
//			});

//		HeuristicCognitiveCoordinatedClient.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\hpcc_test.xml",
//				"tmp\\hpcc_log.txt",
//				"100",
//				"false"
//			});
		
//		GravitationalMainClient1.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\grav1_test.xml",
//				"tmp\\grav1_log.txt",
//				"100",
//				"Edge",
//				"A",
//				"2",
//				"max"
//			});
		
//		GravitationalMainClient2.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\grav2_test.xml",
//				"tmp\\grav2_log.txt",
//				"100",
//				"Edge",
//				"A",
//				"2",
//				"max"
//			});
		
		// NOT TESTED
//		GrayBoxLearnerClient.main(new String[]{
//				"127.0.0.1",
//				"5000",
//				"res\\configurations\\examples\\gbla_test.xml",
//				"tmp\\gbla_log.txt",
//				"100",
//				"false",
//				"0.1",  // exploration
//				"0.05", // delta a
//				"0.9",  // discount factor
//				"tmp\\qtable",
//				"true",
//				"4",    // max neighbors
//				"1?",
//				"2?",
//				"3?"
//			});
		
		RandomReactiveClient.main(new String[]{
				"127.0.0.1",
				"5000",
				"res\\configurations\\examples\\random_test.xml",
				"tmp\\random_log.txt",
				"100",
				"false"
			});

	}
	
}
