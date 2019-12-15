package events;

/**
 * 
 * Represents a message event received from SGH Controller.
 *
 */
public interface ControllerMsgEvent extends Event {
	/**
	 * Gets the received message.
	 * 
	 * @return the message received.
	 */
	String getMsg();

}
