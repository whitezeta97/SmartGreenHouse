#ifndef __PUMP_MANAGEMENT_TASK__
#define __PUMPS_MANAGEMENT_TASK__

#include "Task.h"
#include "LedExt.h"
#include "ServoMotor.h"

class PumpsManagementTask: public Task {
private:
    #define VALUE_MINIMUM_FLOW 74
    #define VALUE_MEDIUM_FLOW 128
    #define VALUE_MAXIMUM_FLOW 255
    #define ZERO_ANGLE 0
    #define MINIMUM_ANGLE 60
    #define MEDIUM_ANGLE 120
    #define MAXIMUM_ANGLE 180
    
    ServoMotor* servo;
    LedExt* flowLed;
    bool pumpsOn;

public:
    PumpsManagementTask(ServoMotor* servo, LedExt* flowLed);
    void init(int period);
	void tick();
};

#endif
