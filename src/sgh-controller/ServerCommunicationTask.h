#ifndef __SERVER_COMMUNICATION_TASK__
#define __SERVER_COMMUNICATION_TASK__

#include "Task.h"
#include "MsgService.h"
#include "Mode.h"

#define PUMP_OFF 		"PUMP_OFF"
#define P_MIN			"P_MIN"
#define P_MED 			"P_MED"
#define P_MAX 			"P_MAX"

#define MANUALMODE_OFF 	"manualmodeoff"
#define MANUALMODE_ON 	"manualmodeon"

class ServerCommunicationTask: public Task {
	
private:	
	Mode currentMode;
	
	bool manageManualModeOffState(Msg* message);

public:
    ServerCommunicationTask();
    void init(int period);
	void tick();
};

#endif
