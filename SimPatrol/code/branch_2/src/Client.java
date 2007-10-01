import java.io.IOException;
import util.udp.UDPSocket;

public class Client {

	public static void main(String[] args) throws IOException {
		String new_environment = "<configuration type=\"0\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"c:/env.txt\"/>";		

		String new_ag =
		"<configuration type=\"1\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"s1\">" +
		"<agent id=\"a4\" label=\"Priscila\" vertex_id=\"v1\" max_stamina=\"100\" stamina=\"20\">" +
		"<allowed_perception type=\"1\">" +
		"<limitation type=\"1\">" +
		"<lmt_parameter value=\"1\"/>" +
		"</limitation>" +
		"</allowed_perception>" +
		"<allowed_action type=\"1\">" +
		//"<limitation type=\"1\">" +
		//"<lmt_parameter value=\"1\"/>" +
		//"</limitation>" +
		"</allowed_action>" +
		"</agent>" +
	    "</configuration>";
		
		/*String new_metric =
			"<configuration type=\"2\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"10\">" +
			"<metric type=\"3\" value=\"0\"/>" +
			"</configuration>";*/
		
		String start = "<configuration type=\"3\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"120\"/>";
		
		UDPSocket socket = new UDPSocket(7000);
		socket.send(new_environment, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		/*socket.send(new_metric, "127.0.0.1", 5000);
		System.out.println(socket.receive());*/
		
		socket.send(start, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(new_ag, "127.0.0.1", 5000);
		System.out.println(socket.receive());
	}
}