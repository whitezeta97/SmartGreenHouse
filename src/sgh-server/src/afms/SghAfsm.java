package afms;

import events.ControllerMsgEvent;
import events.Event;
import events.TickEvent;

public interface SghAfsm {

	void setManualMode(String msg);

	void manageControllerMsgEvent(String msg);

	void manageTimerTickEvent(TickEvent ev);

	boolean isManualMode();

	void performAction(Event ev);
}
