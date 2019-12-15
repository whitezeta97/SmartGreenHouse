package eventloop;

import observer.event.EventObserver;

/**
 * 
 * Represents an Event Loop observer, getting notified whenever an observed
 * event occurs.
 *
 */
public interface EventLoopObserver extends EventLoop, EventObserver {

}
