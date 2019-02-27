#define POTPIN A0
#define MAX_VALUE 1023
#define HUMID_SENSOR 0

int humidity = 0;

void setup() {
  Serial.begin(115200);
  pinMode(POTPIN, INPUT);
}

void loop() {
  Serial.println("Hello world!"); 
  delay(500);
  if (HUMID_SENSOR) {
    
  } else {
    humidity = 100 * analogRead(POTPIN) / MAX_VALUE;
    Serial.println(String(humidity) + "%");
    //WE SEND THE HUMIDITY AT THE SERVER, NOW
  }
}
