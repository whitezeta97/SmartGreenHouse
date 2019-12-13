#ifndef __BLUETOOTH__
#define __BLUETOOTH__

#include <SoftwareSerial.h>

class Bluetooth {

private:
    int txPin;
    int rxPin;
    int baudRate;
    
    SoftwareSerial getBluetooth();
public:
    Bluetooth(int txPin, int rxPin, int baudRate);
    bool isDataAvaliable();
    char* readData();
    void sentData(char* message);
};

#endif
