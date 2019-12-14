package afms;

import events.ControllerMsgEvent;
import events.EdgeMsgEvent;

public interface SghAfsm {

	void manageControllerMsgEvent(ControllerMsgEvent ev);

	void manageEdgeMsgEvent(EdgeMsgEvent ev);

	void manageTimerTickEvent();

}
