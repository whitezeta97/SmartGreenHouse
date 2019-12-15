package observer.event;

import events.Event;

/**
 * Represents an event observer.
 *
 */
public interface EventObserver {

	boolean notifyEvent(final Event ev);

}
