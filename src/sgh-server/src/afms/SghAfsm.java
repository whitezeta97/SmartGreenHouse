package afms;

import events.Event;

public interface SghAfsm {

	boolean isManualMode();

	void performAction(Event ev);
}
