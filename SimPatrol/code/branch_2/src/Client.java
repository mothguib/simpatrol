import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ObjectOutputStream output;
		ObjectInputStream input;
		
		String new_environment = "<configuration type=\"0\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"c:/env.txt\"/>";		

		String new_ag =
		"<configuration type=\"1\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"s1\">" +
		"<agent id=\"a4\" label=\"Priscila\" vertex_id=\"v1\" max_stamina=\"100\" stamina=\"20\">" +
		//"<allowed_perception type=\"1\">" +
		//"<limitation type=\"1\">" +
		//"<lmt_parameter value=\"1\"/>" +
		//"</limitation>" +
		//"</allowed_perception>" +
		"<allowed_action type=\"1\">" +
		//"<limitation type=\"1\">" +
		//"<lmt_parameter value=\"1\"/>" +
		//"</limitation>" +
		"</allowed_action>" +
		"</agent>" +
	    "</configuration>";
		
		String new_metric =
			"<configuration type=\"2\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"10\">" +
			"<metric type=\"3\" value=\"0\"/>" +
			"</configuration>";
		
		String start = "<configuration type=\"3\" sender_address=\"127.0.0.1\" sender_socket=\"7000\" parameter=\"120\"/>";
		
		Socket socket = new Socket("127.0.0.1", 5000);
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		
		output.writeObject(new_environment);
		output.flush();
		
		String message = (String) input.readObject();
		System.out.println(message);
		
		output.writeObject(start);
		output.flush();

		message = (String) input.readObject();
		System.out.println(message);
		
		output.writeObject(new_ag);
		output.flush();
		
		message = (String) input.readObject();
		System.out.println(message);
		
		output.writeObject(new_metric);
		output.flush();
		
		message = (String) input.readObject();
		System.out.println(message);
		
		output.close();
		input.close();
		socket.close();
	}
}