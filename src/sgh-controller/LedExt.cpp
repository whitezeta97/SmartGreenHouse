#include "LedExt.h"
#include "Arduino.h"

LedExt::LedExt(int pin) {
    this->pin = pin;
    pinMode(this->pin, OUTPUT);
    this->isOn = false;
    this->intensity = 128;
}

void LedExt::turnOn() {
    analogWrite(this->pin, this->intensity);
    this->isOn = true;
}

void LedExt::turnOff() {
    analogWrite(this->pin, 0);
    this->isOn = false;
}

void LedExt::setIntensity(int intensity) {
    this->intensity = intensity;
    if (this->isOn) {
        analogWrite(this->pin, this->intensity);
    }
}
