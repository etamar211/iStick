#pragma once
#include <Windows.h>

class MouseAction
{
private:
	INPUT _mouseInput;

public:
	MouseAction(); // constructor that initializes the INPUT structure
	void MoveMouse(int,int); // Move the mouse.
	void RightPress();
	void LeftPress();
	void MiddlePress();
	void RightRelease();
	void LeftRelease();
	void MiddleRelease();
	~MouseAction(); // destructor
};

