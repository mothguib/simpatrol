import java.io.IOException;
import control.simulator.*;

public class Teste {
	public static void main(String[] args) throws IOException {
		boolean is_real_time_simulation = Boolean.parseBoolean(args[0]);
		
		if(is_real_time_simulation)
			new RealTimeSimulator(5000, 0.25);
		else
			new CycledSimulator(5000, 0.25);
	}
}
