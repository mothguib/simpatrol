import java.io.IOException;
import util.udp.UDPSocket;

public class Client {

	public static void main(String[] args) throws IOException {
		String new_graph = "<configuration type=\"0\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"c:/graph.txt\"/>";		
		String new_societies = "<configuration type=\"1\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"c:/societies.txt\"/>";
		String new_ag =
		"<configuration type=\"2\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"s1\">" +
		"<agent id=\"a4\" label=\"Priscila\" vertex_id=\"v1\">" +
		"<allowed_perception type=\"1\">" +
		"<limitation type=\"0\">" +
		"<lmt_parameter value=\"1\"/>" +
		"</limitation>" +
		"</allowed_perception>" +
		"<allowed_perception type=\"0\">" +
		"<limitation type=\"0\">" +
		"<lmt_parameter value=\"1\"/>" +
		"</limitation>" +
		"</allowed_perception>" +
		"</agent>" +
	    "</configuration>";
		
		String start = "<configuration type=\"3\" sender_address=\"127.0.0.1\" sender_socket=\"7005\" parameter=\"120\"/>";
		
		UDPSocket socket = new UDPSocket(7005);
		socket.send(new_graph, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(new_societies, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(start, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(new_ag, "127.0.0.1", 5000);
		System.out.println(socket.receive());
	}
}