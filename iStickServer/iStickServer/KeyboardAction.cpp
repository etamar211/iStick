#include "KeyboardAction.h"


KeyboardAction::KeyboardAction()
{
	this->_keyboardInput.type = INPUT_KEYBOARD; // keyboard action
	this->_keyboardInput.ki.dwExtraInfo = 0; // extra info
	this->_keyboardInput.ki.time = 0; // always 0 
	this->_keyboardInput.ki.wVk = 0; // key code for the press
}

void KeyboardAction::pressKey(WORD action)
{
	this->_keyboardInput.ki.wScan = action; // hardware scan code for key
	this->_keyboardInput.ki.dwFlags = KEYEVENTF_SCANCODE;
	SendInput(1, &this->_keyboardInput, sizeof(INPUT));
}

void KeyboardAction::releaseKey(WORD action)
{
	this->_keyboardInput.ki.wScan = action; // hardware scan code for key
	this->_keyboardInput.ki.dwFlags = KEYEVENTF_SCANCODE | KEYEVENTF_KEYUP;
	SendInput(1, &(this->_keyboardInput), sizeof(INPUT));
}

KeyboardAction::~KeyboardAction()
{

}
