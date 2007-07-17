import java.net.SocketException;
import control.simulator.RealTimeSimulator;

public class Teste {
	public static void main(String[] args) throws SocketException {		
		new RealTimeSimulator(5000, 500);
	}
}
