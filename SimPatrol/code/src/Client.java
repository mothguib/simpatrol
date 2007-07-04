import java.io.IOException;
import util.udp.UDPSocket;

public class Client {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String new_env = "<message>" +
					   "<configuration type=\"0\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"c:/teste.txt\"/>" +
					   "</message>";
		
		String new_ag = "<message>" +
		"<configuration type=\"1\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"s1\">" +
		"<agent id=\"a4\" label=\"Priscila\" vertex_id=\"v1\"/>" +
	    "</configuration>" +
	    "</message>";
		
		String start =  "<message>" +		
		"<configuration type=\"2\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"60\"/>" +
	    "</message>";
		
		UDPSocket socket = new UDPSocket(7005);
		socket.send(new_env, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(start, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(new_ag, "127.0.0.1", 5000);
		System.out.println(socket.receive());
	}
}