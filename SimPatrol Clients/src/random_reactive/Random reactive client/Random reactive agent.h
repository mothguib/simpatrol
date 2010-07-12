#include "stdafx.h"
#include "thread.h"

using namespace std;

class RandomReactiveAgent : public IRunnable {
public:
	RandomReactiveAgent(bool param_is_real_time, int param_port_number, string param_server_address) {
		_continue = true;
		is_real_time = param_is_real_time;
		port_number = param_port_number;
		server_address = param_server_address;
	}
	virtual unsigned long run() {
		// the size of the receiving buffer
		// Change its value, if the object enters in infinite loop.
		const int BUFFER_SIZE = 10240;

		// initializing WinSock
		WSADATA wsaData;
		int wsaret=WSAStartup(0x101, &wsaData);
		if(wsaret) {
			_tprintf(_T("Fatal Error: WinSock initialization failed\n"));
			return 1;
		}

		// creating the sockets
		SOCKET conn;
		if(is_real_time)
			// creating a UDP connection
			conn = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
		else
			// creating an TCP connection
			conn = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

		if(conn == INVALID_SOCKET) {
			_tprintf(_T("Fatal Error: Socket initialization failed\n"));
			return 2;
		}

		// getting server information
		unsigned long addr;
		struct hostent *hp;
		addr = inet_addr(server_address.c_str());
		hp = gethostbyaddr((char*)&addr, sizeof(addr), AF_INET);
		if(hp == NULL) {
			_tprintf(_T("Fatal Error: Server unreachable\n"));
			closesocket(conn);
			return 3;
		}
		
		// connecting to the server
		struct sockaddr_in server;
		int remote_addr_len = sizeof(server);
		memset((char *)&server, 0, sizeof(server));
		server.sin_addr.s_addr = *((unsigned long*)hp->h_addr);
		server.sin_family = AF_INET;
		server.sin_port = htons(port_number);
		if(is_real_time) {
			char buff[1];
			if(sendto(conn, buff, strlen(buff)*sizeof(char), 0, (struct sockaddr*)&server, sizeof(server)) ==  -1) {
				_tprintf(_T("Fatal Error: Couldn't contact server\n"));
				closesocket(conn);
				return 4;
			}
		}
		else {
			if(connect(conn, (struct sockaddr*)&server, sizeof(server))) {
				_tprintf(_T("Fatal Error: Couldn't connect to the server\n"));
				closesocket(conn);
				return 4;
			}
		}

		while(_continue) {
			// the current position of the agent
			int elapsed_length = -1;

			// the current node of the agent
			string current_node = "";

			// the neighbourhood of the agent
			vector<string> neighbourhood;

			// while the agent didn't perceive yet
			while(elapsed_length == -1 || current_node.length() == 0 || neighbourhood.size() == 0) {
				// receiving the perceptions from the server
				string perception;
				int y;
				char buff[BUFFER_SIZE];
				if(is_real_time) {					
					while(y = recvfrom(conn, buff, strlen(buff)*sizeof(char), 0, (struct sockaddr*)&server, &remote_addr_len)) {
						perception.append(buff);
					
						if(y < BUFFER_SIZE)
							break;
					
						// if the program entered in infinite loop, change the buffer size
					}
				}
				else {
					while(y = recv(conn, buff, BUFFER_SIZE, 0)) {
						perception.append(buff);
					
						if(y < BUFFER_SIZE)
							break;
					
						// if the program entered in infinite loop, change the buffer size
					}
				}
				perception = perception.substr(0, perception.find("</perception>", 0) + 13);

				// perceiving things
				if(perception.find("<perception type=\"0\"", 0) != string::npos)
					neighbourhood = getNeighbourhood(perception);
				else if(perception.find("<perception type=\"4\"", 0) != string::npos) {
					elapsed_length = getCurrentPosition(perception);
					current_node = getCurrentNodeId(perception);
				}
			}
			
			// choosing the action of the agent
			if(elapsed_length == 0) {
				// choosing the next node
				string next_node = current_node;
				while(next_node.compare(current_node) == 0) {
					int index = rand() % neighbourhood.size();
					next_node = neighbourhood[index];
				}
				
				// sending the messages with the actions
				string message_1 = "<action type=\"2\"/>\n";
				if(is_real_time)
					sendto(conn, message_1.c_str(), strlen(message_1.c_str())*sizeof(char), 0, (struct sockaddr*)&server, sizeof(server));
				else
					send(conn, message_1.c_str(), strlen(message_1.c_str()), 0);
				
				string message_2 = "<action type=\"1\" node_id=\"";
				       message_2.append(next_node);
					   message_2.append("\"/>\n");
				if(is_real_time)
					sendto(conn, message_2.c_str(), strlen(message_2.c_str())*sizeof(char), 0, (struct sockaddr*)&server, sizeof(server));
				else
					send(conn, message_2.c_str(), strlen(message_2.c_str()), 0);
			}
		}
		// closing the connection
		closesocket(conn);

		// terminating WinSock
		WSACleanup();

		return 0;
	}
	virtual void stop() {
		_continue = false;
	}
protected:
	bool _continue;
	bool is_real_time;
	int port_number;
	string server_address;
private:
	vector<string> getNeighbourhood(string perception) {
		vector<string> answer;

		int next_node_index = perception.find("<node ", 0);
		int next_node_id_index = perception.find("id=\"", next_node_index);
		int end_node_id_index;
		while(next_node_index != string::npos) {
			next_node_id_index = next_node_id_index + 4;
			end_node_id_index = perception.find("\"", next_node_id_index);
			answer.push_back(perception.substr(next_node_id_index, end_node_id_index - next_node_id_index));
			next_node_index = perception.find("<node ", end_node_id_index);
			next_node_id_index = perception.find("id=\"", next_node_index);
		}
		
		return answer;
	}

	string getCurrentNodeId(string perception) {
		int node_id_index = perception.find("node_id=\"", 0) + 11;
		int end_node_id_index = perception.find("\"", node_id_index);
		return perception.substr(node_id_index, end_node_id_index - node_id_index);
	}

	int getCurrentPosition(string perception) {
		int elapsed_length_index = perception.find("elapsed_length=\"", 0) + 16;

		if(elapsed_length_index == string::npos)
			return 0;

		int end_elapsed_length_index = perception.find("\"", elapsed_length_index);
		return atoi(perception.substr(elapsed_length_index, end_elapsed_length_index - elapsed_length_index).c_str());
	}
};