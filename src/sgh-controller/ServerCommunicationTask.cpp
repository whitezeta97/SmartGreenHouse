#include "ServerCommunicationTask.h"
#include "Mode.h"
#include "Pumps.h"
#include "Flow.h"
#include "Humidity.h"

ServerCommunicationTask::ServerCommunicationTask() {

}

void ServerCommunicationTask::init(int period) {
    Task::init(period);
}

void ServerCommunicationTask::tick() {

}
