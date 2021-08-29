#pragma once
#include <Windows.h>

class KeyboardAction
{
private:
	INPUT _keyboardInput;

public:
	KeyboardAction(); // constructor the initializes the INPUT structure
	void pressKey(WORD); // pressing a given key
	void releaseKey(WORD); // releasing the last key that was pressed
	~KeyboardAction(); // destructor
};

