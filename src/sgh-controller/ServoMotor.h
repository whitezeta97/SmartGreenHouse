#ifndef __SERVO_MOTOR__
#define __SERVO_MOTOR__

#define byte uint8_t
#define boolean bool

#include <ServoTimer2.h>

class ServoMotor {

private:
    ServoTimer2 servo;
public:
    ServoMotor(int pin);
    void setAngle(int angle);
};

#endif
