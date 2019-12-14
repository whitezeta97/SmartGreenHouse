#include "Bluetooth.h"

Bluetooth::Bluetooth(int txPin, int rxPin, int baudRate) {
    this->txPin = txPin;
    this->rxPin = rxPin;
    this->baudRate = baudRate;
}

SoftwareSerial Bluetooth::getBluetooth() {
    SoftwareSerial bluetooth(this->txPin, this->rxPin);
    bluetooth.begin(this->baudRate);
    return bluetooth;
}

bool Bluetooth::isDataAvaliable() {
    return getBluetooth().available() > 0;
}

char* Bluetooth::readData() {
    return getBluetooth().read();
}

void Bluetooth::sentData(char* message) {
    getBluetooth().write(message);
}
