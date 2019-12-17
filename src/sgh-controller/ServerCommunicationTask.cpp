#include "ServerCommunicationTask.h"
#include "Pumps.h"
#include "Flow.h"
#include "Humidity.h"

ServerCommunicationTask::ServerCommunicationTask() {
	this->currentMode = AUTOMATIC;
}

void ServerCommunicationTask::init(int period) {
    Task::init(period);
}

bool ServerCommunicationTask::isNumber(String string) { 
    for (int i = 0; i < string.length(); i++) { 
      if (isdigit(string[i]) == false) {
        return false;
      }
    }

    return true; 
} 

void ServerCommunicationTask::manageManualModeOffState(String message) {
	if (message == PUMP_OFF) {
		pumps = OFF;
	} else if (message == P_MIN){
		flow = MINIMUM;
		pumps = ON;
	} else if (message == P_MED) {
		pumps = ON;
		flow = MEDIUM;
	} else if (message == P_MAX) {
		pumps = ON;
		flow = MAXIMUM;
	}
}

void ServerCommunicationTask::tick() {
	
	if (this->currentMode != mode) {
		this->currentMode = mode;
		MsgService.sendMsg(this->currentMode == MANUAL ? MANUALMODE_ON : MANUALMODE_OFF);
	}

  Msg* msg = MsgService.receiveMsg();
	String msgString = MsgService.receiveMsg()->getContent();
  	
	if (this->currentMode == MANUAL) {
		this->manageManualModeOffState(msgString);
	}
	
	if (this->isNumber(msgString)) {
		humidity = atoi(msgString.c_str());
	}
	
	delete msg;
}
