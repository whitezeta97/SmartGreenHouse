#include "Led.h"
#include "Arduino.h"

Led::Led(int pin) {
    this->pin = pin;
    pinMode(this->pin, OUTPUT);
}

void Led::turnOn() {
    digitalWrite(this->pin, HIGH);
}

void Led::turnOff() {
    digitalWrite(this->pin, LOW);
}
