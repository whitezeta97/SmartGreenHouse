#include "DistanceDetector.h"
#include "Bluetooth.h"
#include "Led.h"
#include "LedExt.h"
#include "ServoMotor.h"
#include "ModeManagementTask.h"
#include "PumpsManagementTask.h"
#include "ServerCommunicationTask.h"
#include "Scheduler.h"

#define BAUDE_RATE 9600
#define SCHEDULER_PERIOD 100
#define MODE_MANAGEMENT_TASK_PERIOD 200
#define PUMP_MANAGEMENT_TASK_PERIOD 200
#define SERVER_COMMUNICATION_TASK_PERIOD 200
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

  Task* modeManagementTask = new ModeManagementTask(distanceDetector, bluetooth, autoLed, manualLed);
  modeManagementTask->init(MODE_MANAGEMENT_TASK_PERIOD);
  sched.addTask(modeManagementTask); 

  Task* pumpsManagementTask = new PumpsManagementTask(servo, flowLed);
  pumpsManagementTask->init(PUMP_MANAGEMENT_TASK_PERIOD);
  sched.addTask(pumpsManagementTask);
  
  Task* serverCommunicationTask = new ServerCommunicationTask();
  serverCommunicationTask->init(SERVER_COMMUNICATION_TASK_PERIOD);
  sched.addTask(serverCommunicationTask);
  
}
 
void loop(){
  sched.schedule();
}
