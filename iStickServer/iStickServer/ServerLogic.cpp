#include "ServerLogic.h"


ServerLogic::ServerLogic(SOCKET sock)
{	
	/*
		constructor of the ServerLogic, starting the threads
	*/
	status = 0;
	//this->stillAlive = true;
	this->sock = sock;
	this->dispatchThread = new std::thread(&ServerLogic::dispatchMessage, this);
	this->processingThread = new std::thread(&ServerLogic::ProcessingMessages, this);
	//this->aliveCheckThread = new std::thread(&ServerLogic::aliveCheck, this);
	this->dispatchThread->join();
	this->processingThread->join();
	//this->aliveCheckThread->join();
}

ServerLogic::~ServerLogic()
{
	closesocket(sock);
	WSACleanup();
}

void ServerLogic::dispatchMessage()
{
	/*
	*	this function will handle the connetions with the clients.
	*	recieve messages - and appropriately responding.
	*/
	int connectResult;
	while (status == 0)
	{
		char recievedMsg[1024] = { 0 };
		//receive data
		try
		{
			this->messagesToDiagnose_mutex.lock();
			connectResult = recv(this->sock, recievedMsg, sizeof(recievedMsg), 0);
		
		}
		catch (const std::exception e)
		{
			std::cout << e.what() << std::endl;
			std::cout << "Recieve thread exited failed to recieve" << std::endl;
			status++;
			this->messagesToDiagnose.push("2");
			this->messagesToDiagnose_mutex.unlock();
			return;
		}

		//Check if messsage recieved is disconnect or okay
		if ((recievedMsg == NULL) || (connectResult <= 0))
		{
			std::cout << "Recieve thread exited message returned was a closing one" << std::endl;
			status++;
			this->messagesToDiagnose.push("2");
			this->messagesToDiagnose_mutex.unlock();
			return;
		}
		for (size_t i = 0; i < strlen(recievedMsg); i++)
		{
			if (!(recievedMsg[i] >= '0' && recievedMsg[i] <= '9' || recievedMsg[i] == ' ' || recievedMsg[i] == '|' || recievedMsg[i] == '-'))
			{
				status++;
				this->messagesToDiagnose.push("2");
				this->messagesToDiagnose_mutex.unlock();
				return;
			}
		}
			

		//if okay then send it for processing
		if (connectResult != SOCKET_ERROR){
			this->messagesToDiagnose.push(std::string(recievedMsg));
			this->messagesToDiagnose_mutex.unlock();
		}
	}
	status++;
}

void ServerLogic::ProcessingMessages(){
	/*
	*	this method will handle all the massages of the client. process it and respond appropriately.
	*/

	// initializing keyboard and mouse controlling classes:
	KeyboardAction keboardControl;
	MouseAction mouseControl;

	bool flag = false;
	int msgCode, lenToErase, x, y, code, pos;

	while (true)
	{
		this->messagesToDiagnose_mutex.lock();
		std::string msg = this->messagesToDiagnose.front();
		this->messagesToDiagnose.pop();
		this->messagesToDiagnose_mutex.unlock();
		do
		{
			pos = msg.find_first_of('|');
			if (pos == 0)
				msg.erase(0,1);
			try
			{
				msgCode = std::stoi(msg, nullptr, 10);
			}
			catch (const std::exception& e)
			{
				std::cout << e.what() << std::endl;
				this->sendMessage("201");
				status++;
				std::cout << "Processing thread exited" << std::endl;
				return;
			}
			switch (msgCode)
			{
				case 2:								//disconnect but wait for another connection
					std::cout << "Goodbye !" << std::endl;
					status++;
					std::cout << "Processing thread exited" << std::endl;
					return;
				case 3: // still alive check
					//this->stillAlive = true;
					break;

				case 10:	//keyboard action ASSUMING MSG WILL LOOK LIKE 10|0/1 KEYCODE
					WORD key;
					int action;
					lenToErase = msg.find_first_of('|');
					msg.erase(0, lenToErase + 1);
					try
					{
						action = std::stoi(msg, nullptr, 10);
					}
					catch (const std::exception& e)
					{
						std::cout << e.what() << std::endl;
						this->sendMessage("201");
						status++;
						std::cout << "Processing thread exited" << std::endl;
						return;
					}
					if (action)
					{
						lenToErase = msg.find_first_of(' ');
						msg.erase(0, lenToErase + 1);
						try
						{
							key = std::stoi(msg, nullptr, 10);
						}
						catch (const std::exception& e)
						{
							std::cout << e.what() << std::endl;
							this->sendMessage("201");
							status++;
							std::cout << "Processing thread exited" << std::endl;
							return;
						}
						if (key == 58 || key == 69 || key == 197 || key == 70)
						{
							keboardControl.pressKey(key);
						}
						keboardControl.releaseKey(key);
					}
					else if (!action)
					{
						lenToErase = msg.find_first_of(' ');
						msg.erase(0, lenToErase + 1);
						try
						{
							key = std::stoi(msg, nullptr, 10);
						}
						catch (const std::exception& e)
						{
							std::cout << e.what() << std::endl;
							this->sendMessage("201");
							status++;
							std::cout << "Processing thread exited" << std::endl;
							return;
						}
						keboardControl.pressKey(key);
					}
					msg.erase(0, msg.find_first_of('|') + 1);
					/***** TO DO *****/
					break;


				case 11:							//mouse click ****ASSUMING MESSAGE WILL LOOK LIKE 11|0/1/2/3/4/5
					lenToErase = msg.find_first_of('|');		
					msg.erase(0, lenToErase + 1);
					try
					{
						code = std::stoi(msg, nullptr, 10);
					}
					catch (const std::exception& e)
					{
						std::cout << e.what() << std::endl;
						this->sendMessage("201");
						status++;
						std::cout << "Processing thread exited" << std::endl;
						return;
					}
					if (code == 0)
						mouseControl.LeftPress();
					else if (code == 1)
						mouseControl.RightPress();
					else if (code == 2)
						mouseControl.MiddlePress();
					else if (code == 3)
						mouseControl.LeftRelease();
					else if (code == 4)
						mouseControl.RightRelease();
					else if (code == 5)
						mouseControl.MiddleRelease();
					else
					{
						this->sendMessage("201");
						status++;
						std::cout << "Processing thread exited" << std::endl;
						return;
					}
					msg.erase(0, msg.find_first_of('|') + 1);
					break;

				case 12:							//mouse action  **** ASSUMING MESSAGE WILL LOOK LIKE 12|35 51
					lenToErase = msg.find_first_of('|');
					msg.erase(0, lenToErase + 1);
					try
					{
						x = std::stoi(msg, nullptr, 10);
					}
					catch (const std::exception& e)
					{
						std::cout << e.what() << std::endl;
						this->sendMessage("201");
						status++;
						std::cout << "Processing thread exited" << std::endl;
						return;
					}
					lenToErase = msg.find_first_of(' ');
					msg.erase(0, lenToErase + 1);
					try
					{
						y = std::stoi(msg, nullptr, 10);
					}
					catch (const std::exception& e)
					{
						std::cout << e.what() << std::endl;
						this->sendMessage("201");
						status++;
						std::cout << "Processing thread exited" << std::endl;
						return;
					}
					mouseControl.MoveMouse(x, y);
					msg.erase(0, msg.find_first_of('|') + 1);
					break;

				default:
					this->sendMessage("201");
					status++;
					std::cout << "Processing thread exited" << std::endl;
					return;
			}
		}while (msg.find('|') != std::string::npos);

	}

}


void ServerLogic::sendMessage(std::string msg){
	int connectResult;
	//receive data
	try
	{
		connectResult = send(this->sock, msg.c_str(), sizeof(msg.c_str()), 0);
	}
	catch (const std::exception& e)
	{
		std::cout << e.what() << std::endl;
		std::cout << "Send function exited sending failed." << std::endl;
		return;
	}
	if (connectResult == SOCKET_ERROR){
		std::cout << "Send function exited Socket error." << std::endl;
		return;
	}
}

/*void ServerLogic::aliveCheck()

* this method will work once every 5 seconds to check if the client is still alive.
* it will be every 5 seconds in order to not interfere with the connection.

{
	while (status == 0)
	{
		this->stillAlive = false;
		sendMessage("103");
		Sleep(5000);
		if (!this->stillAlive)
		{
			closesocket(this->sock);
			this->messagesToDiagnose_mutex.lock();
			this->messagesToDiagnose.push("2");
			this->messagesToDiagnose_mutex.unlock();
			return;
		}
	}
}*/