#pragma once
#include <iostream>
#include <regex>
#include <fstream>
#include <string>
#include "QRGenerator.h"

class BarcodeCreator{
	private:
		std::string _ip;
		std::string _pass;
		std::ifstream* _QRCode;

	public:
		BarcodeCreator();
		~BarcodeCreator();
		std::string getPass();
		std::string getIp();
		std::string genRandom();

};


