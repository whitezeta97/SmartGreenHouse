#ifndef __LED_EXT__
#define __LED_EXT__

class LedExt {

private:
    int pin;
    int intensity;
    bool isOn;
public:
    LedExt(int pin);
    void turnOn();
    void turnOff();
    void setIntensity(int intensity);
};

#endif
