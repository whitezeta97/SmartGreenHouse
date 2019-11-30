package eventloop;

import events.Event;

public interface EventLoop {

	Event waitForNextEvent() throws InterruptedException;

	Event pickNextEventIfAvail() throws InterruptedException;
}
