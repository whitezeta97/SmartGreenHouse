#ifndef __CODE_MANAGEMENT_TASK__
#define __CODE_MANAGEMENT_TASK__

#include "Task.h"
#include "DistanceDetector.h"
#include "Led.h"
#include <SoftwareSerial.h>

class ModeManagementTask: public Task {
private:
    #define MAX_LENGTH_MESSAGE 30
    #define DIST 0.3
    #define CONNECTION_ENABLE "connection_enable"
    #define CONNECTION_DISABLED "connection_disabled"
    #define AUTOMATIC_MODE '0'
    #define MANUAL_MODE '1'
    #define PUMPS_OFF '2'
    #define PUMPS_ON '3'
    #define MESSAGE_TERMINATOR "."
    #define MINIMUM_FLOW '4'
    #define MEDIUM_FLOW '5'
    #define MAXIMUM_FLOW '6'
    #define BASE_10 10

    DistanceDetector* distanceDetector;
    SoftwareSerial* bluetooth;
    Led* autoLed;
    Led* manualLed;
    int currentHumidity;
    bool connectionEnable;

    void changeMode(bool connectionDisabled);
    void sendMessageToBluetooth(char* message);
public:
    ModeManagementTask(DistanceDetector* distanceDetector, SoftwareSerial* bluetooth,
        Led* autoLed, Led* manualLed);
    void init(int period);
	void tick();
};

#endif
