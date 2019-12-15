package events;

/**
 * 
 * Represents a message event received from SGH Edge.
 *
 */
public interface EdgeMsgEvent extends Event {

	/**
	 * Gets the received message.
	 * 
	 * @return the message received.
	 */
	float getMsg();

}
