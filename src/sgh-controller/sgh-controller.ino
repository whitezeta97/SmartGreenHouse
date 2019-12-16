#include "DistanceDetector.h"
#include "Bluetooth.h"
#include "Led.h"
#include "LedExt.h"
#include "ServoMotor.h"
#include "ModeManagementTask.h"
#include "PumpsManagementTask.h"
#include "Scheduler.h"

#define BAUDE_RATE 9600
#define SCHEDULER_PERIOD 100
#define MODE_MANAGEMENT_TASK_PERIOD 200
#define PUMP_MANAGEMENT_TASK_PERIOD 200
#define ECHO_SONAR_PIN 7
#define TRIG_SONAR_PIN 8
#define TX_PIN 3
#define RX_PIN 2
#define PIN_AUTO_LED 4
#define PIN_MANUAL_LED 5
#define PIN_FLOW_LED 9
#define PIN_SERVO 10

Scheduler sched;
 
void setup(){
  Serial.begin(BAUDE_RATE);   //Setta il baund per la trasmissione seriale
  sched.init(SCHEDULER_PERIOD);

  DistanceDetector* distanceDetector = new DistanceDetector(ECHO_SONAR_PIN, TRIG_SONAR_PIN);
  Bluetooth* bluetooth = new Bluetooth(TX_PIN, RX_PIN, BAUDE_RATE);
  Led* autoLed = new Led(PIN_AUTO_LED);
  Led* manualLed = new Led(PIN_MANUAL_LED);
  LedExt* flowLed = new LedExt(PIN_FLOW_LED);
  ServoMotor* servo = new ServoMotor(PIN_SERVO);

  Task* t1 = new ModeManagementTask(distanceDetector, bluetooth, autoLed, manualLed);
  t1->init(MODE_MANAGEMENT_TASK_PERIOD);
  sched.addTask(t1); 

  Task* t2 = new PumpsManagementTask(servo, flowLed);
  t2->init(PUMP_MANAGEMENT_TASK_PERIOD);
  sched.addTask(t2);
  
}
 
void loop(){
  sched.schedule();
}
