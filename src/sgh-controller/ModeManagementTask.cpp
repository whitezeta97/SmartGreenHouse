#include "ModeManagementTask.h"
#include "Mode.h"
#include "Pumps.h"
#include "Flow.h"
#include "string.h"

ModeManagementTask::ModeManagementTask(DistanceDetector* distanceDetector,
        Bluetooth* bluetooth, Led* autoLed, Led* manualLed) {
    this->distanceDetector = distanceDetector;
    this->bluetooth = bluetooth;
    this->autoLed = autoLed;
    this->manualLed = manualLed;

    mode = AUTOMATIC;
    pumps = OFF;
    flow = MINIMUM;
    this->autoLed->turnOn();
}

void ModeManagementTask::init(int period) {
    Task::init(period);
}

void ModeManagementTask::changeMode() {
    char message[MAX_LENGTH_MESSAGE];
    strcpy(message, AUTOMATIC_MODE);
    if (mode == AUTOMATIC) {
        mode = MANUAL;
        this->autoLed->turnOff();
        this->manualLed->turnOn();
        strcpy(message, pumps == ON ? PUMPS_ON : PUMPS_OFF);
    } else {
        mode = AUTOMATIC;
        pumps = OFF;
        this->manualLed->turnOff();
        this->autoLed->turnOn();
        strcpy(message, AUTOMATIC_MODE);
    }
    this->bluetooth->sentData(message);
}

void ModeManagementTask::tick() {
    float distance;
    char* data;
    char message[MAX_LENGTH_MESSAGE];
    char* message1;
    char* message2;

    distance = this->distanceDetector->getDistance();
    if (distance <= DIST) {
        if (this->bluetooth->isDataAvaliable()) {
            data = bluetooth->readData();
            strcpy(message, data);
            message1 = strtok(message, "+");
            message2 = strtok(NULL, "+");
            if (strcmp(message1, MANUAL_MODE) == 0 && mode == AUTOMATIC || strcmp(message1, AUTOMATIC_MODE) == 0 && mode == MANUAL) {
                this->changeMode();
            }
            else if (strcmp(message1, PUMPS_OFF) == 0) {
                pumps = OFF;
            } else if (strcmp(message1, PUMPS_ON) == 0) {
                pumps = ON;
                flow = strcmp(message2, MINIMUM_FLOW) == 0 ? MINIMUM : strcmp(message2, MEDIUM_FLOW) == 0 ? MEDIUM : MAXIMUM;
            }
        }
    } else if (mode == MANUAL) {
        this->changeMode();
    }
}
