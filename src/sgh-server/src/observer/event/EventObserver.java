package observer.event;

import events.Event;

public interface EventObserver {

	boolean notifyEvent(final Event ev);

}
