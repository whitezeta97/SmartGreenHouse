#include "ServoMotor.h"

ServoMotor::ServoMotor(int pin) {
    this->pin = pin;
    this->servo.attach(pin);
}

void ServoMotor::setAngle(int angle) {
    this->servo.write(angle);
}
