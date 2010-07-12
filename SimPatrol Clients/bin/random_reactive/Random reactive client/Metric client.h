#include "stdafx.h"
#include "thread.h"

using namespace std;

class MetricClient : public IRunnable {
public:
	MetricClient(int param_port_number, string param_server_address, string param_file_name) {
		_continue = true;
		port_number = param_port_number;
		server_address = param_server_address;
		file_name = param_file_name;
	}
	virtual unsigned long run() {
		while(_continue) {
			// the size of the receiving buffer
			const int BUFFER_SIZE = 256;

			// initializing WinSock
			WSADATA wsaData;
			int wsaret=WSAStartup(0x101, &wsaData);
			if(wsaret) {
				_tprintf(_T("Fatal Error: WinSock initialization failed\n"));
				return 1;
			}

			// creating the local UDP socket
			SOCKET conn;
			conn = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
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
			char buff[BUFFER_SIZE];
			struct sockaddr_in server;
			memset((char *)&server, 0, sizeof(server));
			server.sin_family = AF_INET;
			server.sin_port = htons(port_number);
			server.sin_addr = *((struct in_addr *)hp->h_addr);
			if(sendto(conn, buff, strlen(buff)*sizeof(char), 0, (struct sockaddr*)&server, sizeof(server)) ==  -1) {
				_tprintf(_T("Fatal Error: Couldn't contact server\n"));
				closesocket(conn);
				return 4;
			}
			
			// creating and opening the metric file
			FILE * p_file;
			p_file = fopen(file_name.c_str(), "w");

			// receiving the metric values
			int remote_addr_len = sizeof(server);
			while(recvfrom(conn, buff, strlen(buff)*sizeof(char), 0, (struct sockaddr*)&server, &remote_addr_len)) {
				string received_message;
				received_message.append(buff);
				int value_index = received_message.find("value=\"", 0) + 7;
				int end_value_index = received_message.find("\"", value_index);
				fprintf(p_file, "%s\n", received_message.substr(value_index, end_value_index - value_index).c_str());
			}
			
			// closing the file
			fclose(p_file);

			// closing the connection
			closesocket(conn);

			// terminating WinSock
			WSACleanup();

			_continue = false;
		}
		return 0;
	}
	virtual void stop() {
		_continue = false;
	}
protected:
	bool _continue;
	int port_number;
	string server_address;
	string file_name;
};