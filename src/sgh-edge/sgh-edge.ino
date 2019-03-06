#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#ifdef HUMID_SENSOR
#include <dht.h>
#endif

#ifdef HUMID_SENSOR
  dht DHT;
#endif

#define SENSOR_PIN A0
#define MAX_VALUE 1023
#define PERIOD 1000
#define PERCENT_VALUE 100
#define BAUDE_RATE 115200
#define TIME_FOR_CHECK_STATE_CONNECTION 500
#define OK 200
#define MAX_ATTEMPTS 10

int humidity = 0;
int humidity1 = 0;
int i = 0;
int lastHumiditySend = -1;
unsigned long initTime;
unsigned long finalTime;
/* wifi network name */
char* ssidName = "G3_1477";
/* WPA2 PSK password */
char* pwd = "00000000";
/* service IP address */ 
char* address = "http://8624b131.ngrok.io";

void setup() {
   Serial.begin(BAUDE_RATE);
   pinMode(SENSOR_PIN, INPUT);  
   WiFi.begin(ssidName, pwd);  
   Serial.print("Connecting...");
  
   while (WiFi.status() != WL_CONNECTED) {  
     delay(TIME_FOR_CHECK_STATE_CONNECTION);
     Serial.print(".");
   } 
  
   Serial.println("Connected: \n local IP: " + WiFi.localIP());
}

int sendData(String address, float value, String place){
   HTTPClient http;
   http.begin(address + "/api/data/");
   http.addHeader("Content-Type", "application/json");
   String msg = 
    String("{ \"value\": ") + String(value) + 
    ", \"place\": \"" + place +"\" }";    
   int retCode = http.POST(msg);   
   http.end();   
   // String payload = http.getString();  
   // Serial.println(payload); 
    
   return retCode;
   
}

void loop() {
   initTime = millis();
   i = 0;
   do {
#ifdef HUMID_SENSOR
      DHT.read11(SENSOR_PIN);
      humidity = DHT.humidity;
      DHT.read11(SENSOR_PIN);
      humidity1 = DHT.humidity1;
#else
      humidity = PERCENT_VALUE * analogRead(SENSOR_PIN) / MAX_VALUE;
      humidity1 = PERCENT_VALUE * analogRead(SENSOR_PIN) / MAX_VALUE;
#endif
      i++;
   } while(humidity != humidity1 && i < MAX_ATTEMPTS);

   Serial.println(String(humidity) + "%");
   
   if (lastHumiditySend != humidity) {
      if (WiFi.status()== WL_CONNECTED) {
         /* send data */
         Serial.print("sending " + String(humidity) + "...");    
         int code = sendData(address, humidity, "home");
         /* log result */
         if (code == OK) {
            Serial.println("Umidity is been send correctly!");
            lastHumiditySend = humidity;  
         } else {
            Serial.println("There was been an error with sending the humidity!");
         }
      } else {
         Serial.println("Error in WiFi connection");
      }
   }

   finalTime = millis();
   unsigned long waitingTime = PERIOD - (finalTime - initTime);
   if (waitingTime <= PERIOD) {
     delay(waitingTime);
   }
}
