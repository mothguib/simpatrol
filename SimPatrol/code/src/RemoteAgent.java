import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import util.udp.UDPSocket;

public class RemoteAgent {
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		int socket_number = Integer.parseInt(teclado.readLine());
		
		UDPSocket socket = new UDPSocket(8000);
		
		String msg = "<message><requisition perception_type=\"0\"/></message>";
		socket.send(msg, "127.0.0.1", socket_number);
		System.out.println(socket.receive());		
	}
}
