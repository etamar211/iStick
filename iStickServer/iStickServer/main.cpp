#include "KeyboardAction.h"
#include "MouseAction.h"
#include <iostream>
#include "BarcodeCreator.h"
#include "ServerLogic.h"
#include <thread>
#include "status.h"


#define PORT 6580

SOCKET getClients(SOCKET sock, SOCKADDR_IN serverService);

int status = 2;
static BarcodeCreator* BC = new BarcodeCreator();

int main()
{	
	
	SOCKET clientSock;
	ServerLogic * SL;


	SOCKET serverSocket;
	WSADATA info;
	int err, connectResult;
	//socket type configuration
	err = WSAStartup(MAKEWORD(2, 0), &info);
	//in case of an error
	if (err != 0){
		std::cout << "WSAStartup failed with error: " << err << std::endl;
		system("PAUSE");
		exit(err);
	}
	//socket creation
	serverSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	//in case of an error
	if (serverSocket == INVALID_SOCKET){
		std::cout << "Error creating socket = " << WSAGetLastError() << std::endl;
		system("PAUSE");
		WSACleanup();
		exit(WSAGetLastError());
	}

	//socket configuration
	SOCKADDR_IN serverService;
	serverService.sin_family = AF_INET;
	serverService.sin_addr.s_addr = INADDR_ANY;
	serverService.sin_port = htons(PORT);

	//ask for connection
	connectResult = bind(serverSocket, (struct sockaddr*) &serverService, sizeof(serverService));

	//in case of an error
	if (connectResult == SOCKET_ERROR){
		std::cout << "connect function failed with error: " << WSAGetLastError() << std::endl;
		system("PAUSE");
		closesocket(serverSocket);
		WSACleanup();
		exit(1);
	}

	//main function of client binding
	while (true){
		if (status == 2)
		{
			clientSock = getClients(serverSocket, serverService);
			if (clientSock != INVALID_SOCKET)
			{
				SL = new ServerLogic(clientSock);
				std::cout << "exited" << std::endl;
				SL->~ServerLogic();
			}

		}
	}

	std::cout << "Goodbye!" << std::endl;
	closesocket(clientSock);
	WSACleanup();
	system("PAUSE");
	return 0;
}

SOCKET getClients(SOCKET serverSocket, SOCKADDR_IN serverService){
	/*
	*	this function will get client and will create a thread to handle those clients
	*/
	int  addr_len;
	char recievedMsg[1024];
	SOCKET clientSock;
	int err,connectResult;
	WSADATA info;
	//listen for a connection
	err = WSAStartup(MAKEWORD(2, 0), &info);
	//in case of an error
	if (err != 0){
		std::cout << "WSAStartup failed with error: " << err << std::endl;
		system("PAUSE");
		exit(err);
	}

	connectResult = listen(serverSocket, SOMAXCONN);
	if (connectResult != 0){
		std::cout << "the server can't listen: " << WSAGetLastError() << std::endl;
		system("PAUSE");
		closesocket(serverSocket);
		WSACleanup();
		exit(1);
	}

	//accept a connection
	while (1){
		BC = new BarcodeCreator();							// creating barcode
		addr_len = sizeof(serverService);
		/*
		*	Show to user the barcode and the ip and password:
		*/
		std::cout << "Please scan the following barcode in order to start playing!! :)" << std::endl;
		//system("QRCode.jpg"); // show the user the barcode that he needs to scan

		std::cout << "If you can't scan the barcode, here is your ip and the password to connect to the server:" << std::endl;
		std::cout << "IP: " << BC->getIp() << std::endl;
		std::cout << "Password: " << BC->getPass() << std::endl;


		clientSock = accept(serverSocket, (struct sockaddr*) &serverService, &addr_len);
		if (!(clientSock)){
			std::cout << "the server can't accept the connection" << std::endl;
		}
		else{
			//accepted a connection, recieving password
			connectResult = recv(clientSock, recievedMsg, sizeof(recievedMsg), 0);
			if (connectResult != SOCKET_ERROR){
				std::string password(recievedMsg);
				int lenToErase = password.find_first_of('|');
				password.erase(0, lenToErase + 1);
				//check pass
				if (!password.compare(0,8,BC->getPass())){
					std::cout << "Connected! eNjoy :)" << std::endl;
					connectResult = send(clientSock, "100", sizeof("100"), 0); //right password keycode
					if (connectResult != SOCKET_ERROR){
						return clientSock;
					}
					else{
						std::cout << "Socket Error" << std::endl;
						closesocket(clientSock);
					}
				}
				else
				{
					std::cout << "Wrong Password!" << std::endl;
					connectResult = send(clientSock, "200", sizeof("200"), 0); //wrong password keycode
					closesocket(clientSock);
				}
			}
			else
			{
				closesocket(clientSock);
			}
		}
	}
}


