#pragma once
#include <iostream>
#include <string>
#include "KeyboardAction.h"
#include "MouseAction.h"
#include <winsock.h>
#include <thread>
#include <queue>
#include <exception>
#include "status.h"
#include <mutex>





class ServerLogic
{

private:
	//Session session;
	SOCKET sock;
	//bool stillAlive;
	std::queue<std::string> messagesToDiagnose;
	void dispatchMessage();  /*will get the messages and will put them in the queue*/
	void ProcessingMessages(); /* will process the messages coming from the client*/
	void sendMessage(std::string msg); /*Will send messages to the client*/
	std::thread* dispatchThread;
	std::thread* processingThread;
	//std::thread* aliveCheckThread;
	std::mutex messagesToDiagnose_mutex;

public:
	void aliveCheck();
	ServerLogic(SOCKET sock/*, std::thread* dispatchThread, std::thread* processingThread, std::thread* sendThread*/);
	~ServerLogic();
};

