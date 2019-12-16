#ifndef __SERVER_COMMUNICATION_TASK__
#define __SERVER_COMMUNICATION_TASK__

#include "Task.h"
#include "MsgService.h"

class ServerCommunicationTask: public Task {
private:

public:
    ServerCommunicationTask();
    void init(int period);
	void tick();
};

#endif
