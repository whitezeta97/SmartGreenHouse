#include "DistanceDetector.h"
#include "Arduino.h"

DistanceDetector::DistanceDetector(int echoPin, int trigPin) {
	this->echoPin = echoPin;
	this->trigPin = trigPin;
	
	pinMode(this->trigPin, OUTPUT);
	pinMode(this->echoPin, INPUT);
}

float DistanceDetector::detectDistance() {
	/* send the pulse */
		digitalWrite(this->trigPin, LOW);
		delayMicroseconds(3);
		digitalWrite(this->trigPin, HIGH);
		delayMicroseconds(5);
		/* received the eco */
		digitalWrite(this->trigPin, LOW);
		
		float tUS = pulseIn(echoPin, HIGH);
		float t = tUS / 1000.0 / 1000.0 / 2;
		
		return t*vs;
}

float DistanceDetector::getDistance() {
	float distance1;
	float distance2;
	float distance;
  int i = 0;
	do {
		distance1 = detectDistance();
		distance2 = detectDistance();
		distance = distance2 - distance1;
		if (distance < 0) {
			distance*=-1;
		}
   i++;
	} while(distance > MARGIN_ERROR && i < MAX_ATTEMPTS);
	return distance2;
}
