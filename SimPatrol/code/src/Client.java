import java.io.IOException;
import java.net.SocketException;

import util.udp.UDPSocket;


public class Client {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String msg = "<message>" +
		"<configuration type=\"0\" sender_address=\"127.0.0.1\" sender_socket=\"7000\">" +
		"<environment>" +
		"<graph label=\"teste\">" +
		"<vertex id=\"V1\" label=\"A\" priority=\"0\" visibility=\"true\" idleness=\"0\" fuel=\"false\" is_appearing=\"true\"/>" +
		"<vertex id=\"V2\" label=\"B\" priority=\"0\" visibility=\"true\" idleness=\"0\" fuel=\"false\" is_appearing=\"true\">" +
			"<stigma id=\"S_1\" agent_id=\"null\"/>" +
			"<etpd id=\"TPD_11\" seed=\"1000\" next_bool_count=\"-1\" type=\"0\">" +
				"<pd_parameter value=\"0.8\"/>" +
			"</etpd>" +
			"<etpd id=\"TPD_12\" seed=\"1000\" next_bool_count=\"-1\" type=\"1\">" +
				"<pd_parameter value=\"0.23\"/>" +
				"<pd_parameter value=\"0.75\"/>" +
				"<pd_parameter value=\"0.8\"/>" +
				"<pd_parameter value=\"0.12\"/>" +
				"<pd_parameter value=\"0.23\"/>" +
				"<pd_parameter value=\"0.75\"/>" +
				"<pd_parameter value=\"0.8\"/>" +
				"<pd_parameter value=\"0.12\"/>" +
				"<pd_parameter value=\"0.23\"/>" +
				"<pd_parameter value=\"0.75\"/>" +
				"<pd_parameter value=\"0.8\"/>" +
				"<pd_parameter value=\"0.12\"/>" +
				"<pd_parameter value=\"0.23\"/>" +
				"<pd_parameter value=\"0.75\"/>" +
				"<pd_parameter value=\"0.8\"/>" +
				"<pd_parameter value=\"0.12\"/>" +
			"</etpd>" +
		"</vertex>" +
		"<vertex id=\"V3\" label=\"C\" priority=\"0\" visibility=\"true\" idleness=\"0\" fuel=\"false\" is_appearing=\"true\"/>" +
		"<edge id=\"E1\" emitter_id=\"V1\" collector_id=\"V2\" oriented=\"false\" length=\"10.0\" visibility=\"true\" is_appearing=\"true\" is_in_dynamic_emitter_memory=\"false\" is_in_dynamic_collector_memory= \"false\"/>" +
		"<edge id=\"E2\" emitter_id=\"V1\" collector_id=\"V3\" oriented=\"true\" length=\"5.0\" visibility=\"true\" is_appearing=\"true\" is_in_dynamic_emitter_memory=\"false\" is_in_dynamic_collector_memory= \"false\"/>" +
		"<edge id=\"E3\" emitter_id=\"V2\" collector_id=\"V3\" oriented=\"true\" length=\"12.0\" visibility=\"true\" is_appearing=\"true\" is_in_dynamic_emitter_memory=\"false\" is_in_dynamic_collector_memory= \"false\"/>" +
	    "</graph>" +
	    "<society id= \"soc_1\" label=\"sociedade_1\" is_closed=\"false\"> <agent id = \"ag_1\" label=\"daniel\" vertex_id=\"V1\"/> <agent id = \"ag_2\" label=\"tadeu\" vertex_id=\"V1\"/></society>" +
	    "</environment>" +
	    "</configuration>" +
	    "</message>";
		
		String msg_2 = "<message>" +
					   "<configuration type=\"0\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"c:/teste.txt\"/>" +
					   "</message>";
		
		String new_ag = "<message>" +
		"<configuration type=\"1\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"s1\">" +
		"<agent id=\"ag_3\" label=\"Priscila\" vertex_id=\"v1\"/>" +
	    "</configuration>" +
	    "</message>";
		
		String start =  "<message>" +		
		"<configuration type=\"2\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"120\"/>" +
	    "</message>";
		
		UDPSocket socket = new UDPSocket(7000);
		socket.send(msg_2, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(start, "127.0.0.1", 5000);
		System.out.println(socket.receive());
		
		socket.send(new_ag, "127.0.0.1", 5000);
		System.out.println(socket.receive());
	}
}
