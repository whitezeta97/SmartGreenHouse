#include "ServerCommunicationTask.h"
#include "Mode.h"
#include "Pumps.h"
#include "Flow.h"
#include "Humidity.h"
#include <stdlib.h>

ServerCommunicationTask::ServerCommunicationTask() {
	this->isManualMode = false;
}

void ServerCommunicationTask::init(int period) {
    Task::init(period);
}

void ServerCommunicationTask::manageManualModeOffState(Msg* message) {
	
	if (message->getContent() == "PUMP_OFF") {
		pumps = OFF;
	} else if (message->getContent() == "P_MIN"){
		flow = MINIMUM;
		pumps = ON;
	} else if (message->getContent() == "P_MED") {
		pumps = ON;
		flow = MEDIUM;
	} else if (message->getContent() == "P_MAX") {
		pumps = ON;
		flow = MAXIMUM;
	}
	
}

void ServerCommunicationTask::tick() {
	
	this->isManualMode = mode == MANUAL ? true : false;
	
	Msg* msg = MsgService.receiveMsg();
	
	if (!this->isManualMode) {
		this->manageManualModeOffState(msg);
	}
	
	if (atoi(msg)) {
		humidity = atoi(msg);
	}
	
	delete msg;
}
