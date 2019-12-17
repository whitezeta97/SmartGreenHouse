#include "PumpsManagementTask.h"
#include "Mode.h"
#include "Pumps.h"

PumpsManagementTask::PumpsManagementTask(ServoMotor* servo, LedExt* flowLed) {
    this->servo = servo;
    this->flowLed = flowLed;
    this->currentFlow = ZERO;

    this->servo->setAngle(ZERO_ANGLE);
}

void PumpsManagementTask::init(int period) {
    Task::init(period);
}

void PumpsManagementTask::tick() {
    if (pumps == ON && this->currentFlow != flow) {
        this->flowLed->setIntensity(flow == MINIMUM ? VALUE_MINIMUM_FLOW :
            flow == MEDIUM ? VALUE_MEDIUM_FLOW : VALUE_MAXIMUM_FLOW);
        this->flowLed->turnOn();
        this->servo->setAngle(flow == MINIMUM ? MINIMUM_ANGLE :
            flow == MEDIUM ? MEDIUM_ANGLE : MAXIMUM_ANGLE);
        this->currentFlow = flow;
    } else if (pumps == OFF && this->currentFlow != ZERO) {
        this->servo->setAngle(ZERO_ANGLE);
        this->flowLed->turnOff();
        this->currentFlow = ZERO;
    }
}
