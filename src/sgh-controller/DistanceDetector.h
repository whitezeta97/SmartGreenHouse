#ifndef __DISTANCE_DETECTOR__
#define __DISTANCE_DETECTOR__

class DistanceDetector {

private:
	
  #define MARGIN_ERROR 0.5
  #define MAX_ATTEMPTS 10

  int echoPin;
  int trigPin;
  /* assuming to perform the test
   in an environment at 20 ° C */
  const float vs = 331.5 + 0.6*20;
  
  float detectDistance();

public:
  DistanceDetector(int echoPin, int trigPin);
  float getDistance();
};

#endif
