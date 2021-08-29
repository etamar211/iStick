#include "MouseAction.h"



MouseAction::MouseAction()
{
	this->_mouseInput.type = INPUT_MOUSE;
	this->_mouseInput.mi.mouseData = 0;
	this->_mouseInput.mi.dwExtraInfo = 0;
	this->_mouseInput.mi.time = 0;
}

void MouseAction::MoveMouse(int xCoor, int yCoor) // this is the difference in the x and y arguments in the application
{
	//configure the INPUT structure
	this->_mouseInput.mi.dx = (LONG)xCoor;
	this->_mouseInput.mi.dy = (LONG)yCoor;
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_MOVE;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));

}

void MouseAction::RightPress()
{
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_RIGHTDOWN;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));
}

void  MouseAction::LeftPress()
{
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_LEFTDOWN;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));
}

void  MouseAction::MiddlePress()
{
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_MIDDLEDOWN;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));
}

void  MouseAction::RightRelease()
{
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_RIGHTUP;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));
}

void  MouseAction::LeftRelease()
{
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_LEFTUP;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));
}

void  MouseAction::MiddleRelease()
{
	this->_mouseInput.mi.dwFlags = MOUSEEVENTF_MIDDLEUP;
	SendInput(1, &(this->_mouseInput), sizeof(INPUT));
}


MouseAction::~MouseAction()
{

}
