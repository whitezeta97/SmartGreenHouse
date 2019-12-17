#include "ServerCommunicationTask.h"
#include "Pumps.h"
#include "Flow.h"
#include "Humidity.h"
#include <stdlib.h>

ServerCommunicationTask::ServerCommunicationTask() {
	this->currentMode = AUTOMATIC;
}

void ServerCommunicationTask::init(int period) {
    Task::init(period);
}

bool ServerCommunicationTask::manageManualModeOffState(Msg* message) {
	if (message->getContent() == PUMP_OFF) {
		pumps = OFF;
	} else if (message->getContent() == P_MIN){
		flow = MINIMUM;
		pumps = ON;
	} else if (message->getContent() == P_MED) {
		pumps = ON;
		flow = MEDIUM;
	} else if (message->getContent() == P_MAX) {
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

	if (this->currentMode == MANUAL) {
		this->manageManualModeOffState(msg);
	}

	if (atoi(msg)) {
		humidity = atoi(msg);
	}

	delete msg;
}
