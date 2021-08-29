#include "BarcodeCreator.h"

std::string getIP(){
	std::string ipcon;
	std::system("ipconfig > ipcon.txt");
	std::ifstream ipconF;
	ipconF.open("ipcon.txt", std::ifstream::in);
	getline(ipconF, ipcon, (char)ipconF.eof());
	ipconF.close();
	remove("ipcon.txt");

	std::regex pattern("(\\d{1,3}(\\.\\d{1,3}){3})");
	std::smatch match;
	if (std::regex_search(ipcon, match, pattern))
		return match.str();
	else
		return NULL;

}

BarcodeCreator::BarcodeCreator(){
	this->_ip = getIP();
	//random a password
	this->_pass = genRandom();
	std::string msg(this->_ip + '|' + this->_pass);
	createBarcode(msg);
}

BarcodeCreator::~BarcodeCreator(){
	this->_QRCode->close();
}

std::string BarcodeCreator::getPass(){
	return this->_pass;
}

std::string BarcodeCreator::getIp()
{
	return this->_ip;
}

std::string BarcodeCreator::genRandom()  // Random string generator function.
{
	const char alphanum[] =
		"0123456789"
		"!@#$%^&*"
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		"abcdefghijklmnopqrstuvwxyz";

	int stringLength = sizeof(alphanum) - 1;
	std::string pass("");
	srand((unsigned int)time(0));
	for (int z = 0; z < 8; z++)
	{
		pass += alphanum[rand() % stringLength];

	}
	return pass;
}