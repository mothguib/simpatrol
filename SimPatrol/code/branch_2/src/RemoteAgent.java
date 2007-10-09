import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import util.net.UDPSocket;

public class RemoteAgent {
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		int socket_number = Integer.parseInt(teclado.readLine());
		
		UDPSocket socket = new UDPSocket(8000);
		socket.send("", "127.0.0.1", socket_number);
		
		//boolean flag = false;
		
		//while(true) {
			
			
			//if(!flag) {
				//socket.send("<action type=\"0\" vertex_id=\"v3\"/>");
				socket.send("<action type=\"1\" initial_speed=\"1\" vertex_id=\"v3\"/>");
				//flag = true;
			//}		
			
			//System.out.print(socket.receive());
		//}
	}
}
