package cycled;

import util.Keyboard;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

public final class TSPSolvedCycledCoordinatorAgent extends
		CycledCoordinatorAgent {

	public TSPSolvedCycledCoordinatorAgent(String[] tsp_solution,
			double solution_length) {
		super();

		for (String step : tsp_solution)
			this.PLAN.add(step);

		this.solution_length = solution_length;
	}

	protected boolean perceiveTSPSolution(String perception) {
		System.err.println("Solution length: " + this.solution_length);
		return true;
	}

	/**
	 * Turns this class into an executable one. Useful when running this agent
	 * in an individual machine.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to listen to this client. index 2: "true", if the simulation
	 *            is a real time one, "false" if not.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);

			double solution_length = Double.parseDouble(args[3]);

			String[] plan = new String[args.length - 4];
			for (int i = 4; i < args.length; i++)
				plan[i - 4] = args[i];

			TSPSolvedCycledCoordinatorAgent coordinator = new TSPSolvedCycledCoordinatorAgent(
					plan, solution_length);
			if (is_real_time_simulation)
				coordinator.setConnection(new UDPClientConnection(
						server_address, server_socket_number));
			else
				coordinator.setConnection(new TCPClientConnection(
						server_address, server_socket_number));

			coordinator.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			coordinator.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatorAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)> <solution length> <step plan>1..*\"");
		}
	}
}