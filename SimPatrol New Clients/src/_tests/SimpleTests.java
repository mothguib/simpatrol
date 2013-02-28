package _tests;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import launchers.CcLauncher;
import launchers.CrLauncher;
import launchers.GravLauncher;
import launchers.HpccLauncher;
import launchers.ScLauncher;
import strategies.conscientious_reactiveIPC.ConscientiousReactiveClient;
import strategies.cycledIPC.CycledClient;


/**
 * Used to run very simple simulations with any of the currently implemented strategies.
 * 
 * @author Pablo A. Sampaio
 */
public class SimpleTests {
	
	public static void main(String[] args) throws Exception {
		String CONFIG_DIR;
		
		if (args.length == 0) {
			CONFIG_DIR = "configurations\\examples\\";
		} else {
			CONFIG_DIR = args[0];
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Choose the number of the type of agent you want to run (with ICP communication):");
		System.out.println("\t 1 - Gravitational Coordinated");
		System.out.println("\t 2 - Cognitive Coordinated");
		System.out.println("\t 3 - Heuristic Pathfinder Cognitive Coordinated");
		System.out.println("\t 4 - Conscientious Reactive");
		System.out.println("\t 5 - TSP Single Cycle");
		System.out.println("\t -");
		System.out.println("\t 8 - Conscientious Reactive (old implementation)");
		System.out.println("\t 9 - TSP Single Cycle (old implementation)");

		System.out.print  ("> ");
		
		char type = reader.readLine().trim().charAt(0);
		
		switch (type) {
		case '1':
			GravLauncher.main(new String[]{
					"127.0.0.1", "5000", 
					CONFIG_DIR + "gravcoord_test_dir.xml",
					"-time", "20",
					"-log", "tmp\\gravcoord_log.txt",
					"-grav", "Node", "Ar", "1.0", "sum",
					"-ipc",
					//"-callback"
				});
			break;

		case '2':
			CcLauncher.main(new String[]{
					"127.0.0.1", "5000", 
					CONFIG_DIR + "cc_test.xml",
					"-time", "30",
					"-log", "tmp\\cc_log.txt",
					"-ipc",
					//"-callback"
				});
			break;
		
		case '3':
			HpccLauncher.main(new String[]{
					"127.0.0.1", "5000", 
					CONFIG_DIR + "hpcc_test.xml",
					"-time", "30",
					"-log", "tmp\\hpcc_log.txt",
					"-ipc",
					//"-callback"
				});
			break;

		case '4':
			CrLauncher.main(new String[]{
					"127.0.0.1", "5000", 
					CONFIG_DIR + "cr_test.xml",
					"-time", "30",
					"-log", "tmp\\cr_log.txt",
					"-ipc"
				});
			break;
		
		case '5':
			ScLauncher.main(new String[]{
					"127.0.0.1", "5000", 
					CONFIG_DIR + "sc_test.xml",
					"-time", "30",
					"-log", "tmp\\sc_log.txt",
					"-ipc",
					//"-callback"
				});
			break;
			
		case '8':
			ConscientiousReactiveClient.main(new String[]{
					"127.0.0.1",
					"5000",
					CONFIG_DIR + "cr_test.xml",
					"-t", "30",
					"-l", "tmp\\cr_log.txt",
				});
			break;
		
		case '9':
			CycledClient.main(new String[]{
					"127.0.0.1",
					"5000",
					CONFIG_DIR + "sc_test.xml",
					"-t", "30",
					"-l", "tmp\\sc_log.txt",
				});
			break;
		
		default:
			System.out.println("Invalid option! Quitting...");
		
		}
		
	}

}
