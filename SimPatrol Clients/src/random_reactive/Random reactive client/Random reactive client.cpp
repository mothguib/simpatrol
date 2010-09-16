// Random reactive client.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Random reactive client.h"
#include "Random reactive agent.h"
#include "Metric client.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// The one and only application object

CWinApp theApp;

using namespace std;

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	int nRetCode = 0;

	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{
		// TODO: change error code to suit your needs
		_tprintf(_T("Fatal Error: MFC initialization failed\n"));
		nRetCode = 1;
	}
	else
	{
		// TODO: code your application's behavior here.
		// server address
		const char * SERVER_ADDRESS = "127.0.0.1";
		const u_short SERVER_PORT = 5000;
		const int BUFFER_SIZE = 1024;
		const bool IS_REALTIME_SIMULATION = false;
		const string ENVIRONMENT_CONFIGURATION_MESSAGE = "<configuration type=\"0\" parameter=\"c:/env2.txt\"/>\n";
		const string START_SIMULATION_MESSAGE = "<configuration type=\"3\" parameter=\"600\"/>\n";
		
		// initializing WinSock
		WSADATA wsaData;
		int wsaret=WSAStartup(0x101, &wsaData);
		if(wsaret) {
			_tprintf(_T("Fatal Error: WinSock initialization failed\n"));
			return 2;
		}

		// initializing socket connection
		SOCKET conn;
		conn = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
		if(conn == INVALID_SOCKET) {
			_tprintf(_T("Fatal Error: Socket initialization failed\n"));
			return 3;
		}
		
		// getting server information
		unsigned long addr;
		struct hostent *hp;
		addr = inet_addr(SERVER_ADDRESS);
		hp = gethostbyaddr((char*)&addr, sizeof(addr), AF_INET);
		if(hp == NULL) {
			_tprintf(_T("Fatal Error: Server unreachable\n"));
			closesocket(conn);
			return 4;
		}

		// connecting to the server
		struct sockaddr_in server;
		server.sin_addr.s_addr = *((unsigned long*)hp->h_addr);
		server.sin_family = AF_INET;
		server.sin_port = htons(SERVER_PORT);
		if(connect(conn, (struct sockaddr*)&server, sizeof(server))) {
			_tprintf(_T("Fatal Error: Couldn't connect to the server\n"));
			closesocket(conn);
			return 5;
		}

		// sending an "environment configuration" message
		send(conn, ENVIRONMENT_CONFIGURATION_MESSAGE.c_str(), ENVIRONMENT_CONFIGURATION_MESSAGE.length(), 0);
		
		// receiving the message with the ports reserved to the agents		
		string received_message;
		int y;
		char buff[BUFFER_SIZE];
		while(y = recv(conn, buff, BUFFER_SIZE, 0)) {
			received_message.append(buff);

			if(y < BUFFER_SIZE) {
				y = recv(conn, buff, BUFFER_SIZE, 0);
				break;
			}

			// if the program entered in infinite loop, change the buffer size
		}
		received_message = received_message.substr(0, received_message.find("</orientation>", 0) + 14);

		// parsing the ports from the received message
		vector<int> agents_ports;
		int next_port_index = received_message.find("socket=\"", 0);
		int end_port_index;
		while(next_port_index != string.npos) {			
			next_port_index = next_port_index + 8;
			end_port_index = received_message.find("\"", next_port_index);
			agents_ports.push_back(atoi(received_message.substr(next_port_index, end_port_index - next_port_index).c_str()));
			next_port_index = received_message.find("socket=\"", end_port_index);
		}

		// creating the correspondent number of agents
		vector<RandomReactiveAgent *> agents;
		vector<Thread *> agents_threads;
		for(int i = 0; i < agents_ports.size(); i++) {
			RandomReactiveAgent * agent = new RandomReactiveAgent(IS_REALTIME_SIMULATION, agents_ports[i], SERVER_ADDRESS);
			Thread * thread = new Thread(agent);
			agents.push_back(agent);
			agents_threads.push_back(thread);
			thread->start();
		}

		// configuring the metrics of the simulation
		// >> the ports reserved to the metrics
		int metrics_ports[4];

		// >> creating the "mean instantaneous idleness" metric
		char * message = "<configuration type=\"2\" parameter=\"10\"><metric type=\"0\"/></configuration>\n";
		send(conn, message, strlen(message), 0);
		received_message = "";
		while(y = recv(conn, buff, BUFFER_SIZE, 0)) {
			received_message.append(buff);
			
			if(y < BUFFER_SIZE) {
				y = recv(conn, buff, BUFFER_SIZE, 0);
				break;
			}

			// if the program entered in infinite loop, change the buffer size
		}
		received_message = received_message.substr(0, received_message.find("/>", 0) + 2);
		next_port_index = received_message.find("message=\"", 0) + 9;
		end_port_index = received_message.find("\"", next_port_index);
		metrics_ports[0] = atoi(received_message.substr(next_port_index, end_port_index - next_port_index).c_str());

		// >> creating the "max instantaneous idleness" metric
		message = "<configuration type=\"2\" parameter=\"10\"><metric type=\"1\"/></configuration>\n";
		send(conn, message, strlen(message), 0);
		received_message = "";
		while(y = recv(conn, buff, BUFFER_SIZE, 0)) {
			received_message.append(buff);

			if(y < BUFFER_SIZE) {
				y = recv(conn, buff, BUFFER_SIZE, 0);
				break;
			}

			// if the program entered in infinite loop, change the buffer size
		}
		received_message = received_message.substr(0, received_message.find("/>", 0) + 2);
		next_port_index = received_message.find("message=\"", 0) + 9;
		end_port_index = received_message.find("\"", next_port_index);
		metrics_ports[1] = atoi(received_message.substr(next_port_index, end_port_index - next_port_index).c_str());

		// >> creating the "mean idleness" metric
		message = "<configuration type=\"2\" parameter=\"10\"><metric type=\"2\"/></configuration>\n";
		send(conn, message, strlen(message), 0);
		received_message = "";
		while(y = recv(conn, buff, BUFFER_SIZE, 0)) {
			received_message.append(buff);

			if(y < BUFFER_SIZE) {
				y = recv(conn, buff, BUFFER_SIZE, 0);
				break;
			}

			// if the program entered in infinite loop, change the buffer size
		}
		received_message = received_message.substr(0, received_message.find("/>", 0) + 2);
		next_port_index = received_message.find("message=\"", 0) + 9;
		end_port_index = received_message.find("\"", next_port_index);
		metrics_ports[2] = atoi(received_message.substr(next_port_index, end_port_index - next_port_index).c_str());

		// >> creating the "max idleness" metric
		message = "<configuration type=\"2\" parameter=\"10\"><metric type=\"3\"/></configuration>\n";
		send(conn, message, strlen(message), 0);
		received_message = "";
		while(y = recv(conn, buff, BUFFER_SIZE, 0)) {
			received_message.append(buff);

			if(y < BUFFER_SIZE) {
				y = recv(conn, buff, BUFFER_SIZE, 0);
				break;
			}

			// if the program entered in infinite loop, change the buffer size
		}
		received_message = received_message.substr(0, received_message.find("/>", 0) + 2);
		next_port_index = received_message.find("message=\"", 0) + 9;
		end_port_index = received_message.find("\"", next_port_index);
		metrics_ports[3] = atoi(received_message.substr(next_port_index, end_port_index - next_port_index).c_str());

		// creating and starting the metric clients
		MetricClient * metric_client_1 = new MetricClient(metrics_ports[0], SERVER_ADDRESS, "c:\\metrica_01.txt");
		Thread * thread_metric_client_1 = new Thread(metric_client_1);
		thread_metric_client_1->start();

		MetricClient * metric_client_2 = new MetricClient(metrics_ports[1], SERVER_ADDRESS, "c:\\metrica_02.txt");
		Thread * thread_metric_client_2 = new Thread(metric_client_2);
		thread_metric_client_2->start();

		MetricClient * metric_client_3 = new MetricClient(metrics_ports[2], SERVER_ADDRESS, "c:\\metrica_03.txt");
		Thread * thread_metric_client_3 = new Thread(metric_client_3);
		thread_metric_client_3->start();

		MetricClient * metric_client_4 = new MetricClient(metrics_ports[3], SERVER_ADDRESS, "c:\\metrica_04.txt");
		Thread * thread_metric_client_4 = new Thread(metric_client_4);
		thread_metric_client_4->start();

		// sending a "start simulation" configuration		
		send(conn, START_SIMULATION_MESSAGE.c_str(), strlen(message), 0);

		// while the tcp main connection is up, keep online
		while(y = recv(conn, buff, 8, 0));

		// stopping threads
		for(int i = 0; i < agents_threads.size(); i++)
			agents_threads[i]->stop();
		thread_metric_client_1->stop();
		thread_metric_client_2->stop();
		thread_metric_client_3->stop();
		thread_metric_client_4->stop();

		// deleting threads
		for(int i = 0; i < agents_threads.size(); i++) {
			delete agents[i];
			delete agents_threads[i];
		}
		delete metric_client_1;
		delete thread_metric_client_1;
		delete metric_client_2;
		delete thread_metric_client_2;
		delete metric_client_3;
		delete thread_metric_client_3;
		delete metric_client_4;
		delete thread_metric_client_4;

		// closing the connection
		closesocket(conn);

		// terminating WinSock
		WSACleanup();
	}

	return nRetCode;
}
