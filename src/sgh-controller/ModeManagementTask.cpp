#include "ModeManagementTask.h"
#include "Mode.h"
#include "Pumps.h"
#include "Flow.h"
#include "Humidity.h"
#include "string.h"

ModeManagementTask::ModeManagementTask(DistanceDetector* distanceDetector, SoftwareSerial* bluetooth,
        Led* autoLed, Led* manualLed) {
    this->distanceDetector = distanceDetector;
    this->bluetooth = bluetooth;
    this->autoLed = autoLed;
    this->manualLed = manualLed;
    this->connectionEnable = false;
    this->currentHumidity = humidity;

    this->autoLed->turnOn();
}

void ModeManagementTask::init(int period) {
    Task::init(period);
}

void ModeManagementTask::changeMode(bool connectionDisabled) {
    char message[MAX_LENGTH_MESSAGE];
    if (mode == AUTOMATIC) {
        mode = MANUAL;
        this->autoLed->turnOff();
        this->manualLed->turnOn();
        strcpy(message, pumps == ON ? PUMPS_ON : PUMPS_OFF);
        sendMessageToBluetooth(message);
    } else {
        mode = AUTOMATIC;
        pumps = OFF;
        this->manualLed->turnOff();
        this->autoLed->turnOn();
        if (connectionDisabled) {
            strcpy(message, CONNECTION_DISABLED);
            sendMessageToBluetooth(message);
        }
    }
}

void ModeManagementTask::sendMessageToBluetooth(char* message) {
    strcat(message, MESSAGE_TERMINATOR);
    this->bluetooth->write(message);
}

void ModeManagementTask::tick() {
    float distance;
    char data;
    char message[MAX_LENGTH_MESSAGE];

    distance = this->distanceDetector->getDistance();
    if (distance <= DIST) {
        if (!connectionEnable) {
            this->connectionEnable = true;
            strcpy(message, CONNECTION_ENABLE);
            sendMessageToBluetooth(message);
        }
        if (this->bluetooth->available() > 0) {
            data = this->bluetooth->read();
            if ((data == MANUAL_MODE && mode == AUTOMATIC) ||
                    (data == AUTOMATIC_MODE &&  mode == MANUAL)) {
                this->changeMode(false);
            } else if (data == MINIMUM_FLOW || data == MEDIUM_FLOW || data == MAXIMUM_FLOW) {
                flow = data == MINIMUM_FLOW ? MINIMUM : data == MEDIUM_FLOW ? MEDIUM : MAXIMUM;
                pumps = ON;
            } else if (data == PUMPS_OFF) {
                flow = ZERO;
                pumps = OFF;
            }
        }
        if (humidity != this->currentHumidity) {
            this->currentHumidity = humidity;
            strcpy(message, itoa(this->currentHumidity, message, BASE_10));
            sendMessageToBluetooth(message);
        }
    } else  {
        if (connectionEnable) {
            this->connectionEnable = false;
            strcpy(message, CONNECTION_DISABLED);
            sendMessageToBluetooth(message);
        }
        if (mode == MANUAL) {
            this->changeMode(true);
        }
    }
}
