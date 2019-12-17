#include "ServoMotor.h"

ServoMotor::ServoMotor(int pin) {
    this->servo.attach(pin);
}

void ServoMotor::setAngle(int angle) {
    this->servo.write(angle);
}
