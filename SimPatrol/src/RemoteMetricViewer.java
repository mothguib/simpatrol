import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import util.udp.UDPSocket;


public class RemoteMetricViewer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		int socket_number = Integer.parseInt(teclado.readLine());
		
		UDPSocket socket = new UDPSocket(8005);
		socket.send("", "127.0.0.1", socket_number);
		
		while(true) {
			System.out.print(socket.receive());
		}
	}

}
