#include "PumpsManagementTask.h"
#include "Mode.h"
#include "Pumps.h"
#include "Flow.h"

PumpsManagementTask::PumpsManagementTask(ServoMotor* servo, LedExt* flowLed) {
    this->servo = servo;
    this->flowLed = flowLed;
    this->pumpsOn = false;
}

void PumpsManagementTask::init(int period) {
    Task::init(period);
}

void PumpsManagementTask::tick() {
    if (this->pumpsOn == false && pumps == ON) {
        this->flowLed->setIntensity(flow == MINIMUM ? VALUE_MINIMUM_FLOW :
            flow == MEDIUM ? VALUE_MEDIUM_FLOW : VALUE_MAXIMUM_FLOW);
        this->flowLed->turnOn();
        this->servo->setAngle(flow == MINIMUM ? MINIMUM_ANGLE :
            flow == MEDIUM ? MEDIUM_ANGLE : MAXIMUM_ANGLE);
        this->pumpsOn = true;
    } else if (this->pumpsOn && pumps == OFF) {
        this->flowLed->turnOff();
        this->servo->setAngle(ZERO_ANGLE);
        this->pumpsOn = false;
    }
}
