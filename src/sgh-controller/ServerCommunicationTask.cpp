#include "ServerCommunicationTask.h"
#include "Pumps.h"
#include "Flow.h"
#include "Humidity.h"

#include "Arduino.h"

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
  if (message.equals(PUMP_OFF)) {
    pumps = OFF;
  } else if (message.equals(P_MIN) || message.equals(P_MED) || message.equals(P_MAX)) {
    flow = message.equals(P_MIN) ? MINIMUM : message.equals(P_MED) ? MEDIUM : MAXIMUM;
    pumps = ON;
  }
}

void ServerCommunicationTask::tick() {

  if (this->currentMode != mode) {
    this->currentMode = mode;
    MsgService.sendMsg(this->currentMode == MANUAL ? MANUALMODE_ON : MANUALMODE_OFF);
  }
 

  Msg* msg = MsgService.receiveMsg();
  if (msg != NULL) {
    String msgString = msg->getContent();
    String message = "";
    for (int i = 0; i < msgString.length(); i++) {
      char character = msgString.charAt(i);
      if (character != TERMINATOR) {
        message += character;
      } else {
        if (this->currentMode == AUTOMATIC) {
          this->manageManualModeOffState(message);
        }
        if (this->isNumber(message)) {
          humidity = atoi(message.c_str());
        }
        if (!message.equals("")) {
          MsgService.sendMsg(message);  
        }
        message = "";
      }
    } 
  }
  delete msg;
  
}
