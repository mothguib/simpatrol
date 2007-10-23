// Random reactive client.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Random reactive client.h"

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
		string SERVER_NAME = "127.0.0.1";
		int SERVER_PORT = 5000;

		TCPSocket sock(SERVER_NAME,SERVER_PORT);
		
	}

	return nRetCode;
}
