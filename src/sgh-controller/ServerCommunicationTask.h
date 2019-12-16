#ifndef __SERVER_COMMUNICATION_TASK__
#define __SERVER_COMMUNICATION_TASK__

#include "Task.h"
#include "MsgService.h"

class ServerCommunicationTask: public Task {
	
private:	
	bool isManualMode;
	
	bool manageManualModeOffState(Msg* message);

public:
    ServerCommunicationTask();
    void init(int period);
	void tick();
};

#endif
