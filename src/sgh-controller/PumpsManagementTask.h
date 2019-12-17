#ifndef __PUMP_MANAGEMENT_TASK__
#define __PUMPS_MANAGEMENT_TASK__

#include "Task.h"
#include "LedExt.h"
#include "ServoMotor.h"
#include "Flow.h"

class PumpsManagementTask: public Task {
private:
    #define VALUE_MINIMUM_FLOW 74
    #define VALUE_MEDIUM_FLOW 128
    #define VALUE_MAXIMUM_FLOW 255
    #define ZERO_ANGLE 750
    #define MINIMUM_ANGLE 1250
    #define MEDIUM_ANGLE 1750
    #define MAXIMUM_ANGLE 2250

    ServoMotor* servo;
    LedExt* flowLed;
    Flow currentFlow;

public:
    PumpsManagementTask(ServoMotor* servo, LedExt* flowLed);
    void init(int period);
	void tick();
};

#endif
