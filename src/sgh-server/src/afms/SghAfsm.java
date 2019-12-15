package afms;

import events.ControllerMsgEvent;
import events.EdgeMsgEvent;

/**
 * 
 * Represents the SGH Server Asynchronous Finite State Machine.
 *
 */
public interface SghAfsm {

	/**
	 * Manages the received message event from SGH Controller.
	 * 
	 * @param ev
	 *            the message event received from Controller.
	 */
	void manageControllerMsgEvent(ControllerMsgEvent ev);

	/**
	 * Manages the received message event from SGH Edge.
	 * 
	 * @param ev
	 *            the message message event from SGH Edge.
	 */
	void manageEdgeMsgEvent(EdgeMsgEvent ev);

	/**
	 * Manages timer tick event.
	 */
	void manageTimerTickEvent();

}
