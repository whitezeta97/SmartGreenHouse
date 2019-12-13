#ifndef __CODE_MANAGEMENT_TASK__
#define __CODE_MANAGEMENT_TASK__

#include "Task.h"
#include "DistanceDetector.h"
#include "Bluetooth.h"
#include "Led.h"

class ModeManagementTask: public Task {
private:
    #define MAX_LENGTH_MESSAGE 30
    #define DIST 0.3
    #define AUTOMATIC_MODE "automatic_mode"
    #define MANUAL_MODE "manual_mode"
    #define PUMPS_OFF "pumps_off"
    #define PUMPS_ON "pumps_on"
    #define MINIMUM_FLOW "minimum_flow"
    #define MEDIUM_FLOW "medium_flow"
    #define MAXIMUM_FLOW "maximum_flow"

    DistanceDetector* distanceDetector;
    Bluetooth* bluetooth;
    Led* autoLed;
    Led* manualLed;

    void changeMode();
public:
    ModeManagementTask(DistanceDetector* distanceDetector, Bluetooth* bluetooth, Led* autoLed, Led* manualLed);
    void init(int period);
	void tick();
};

#endif
