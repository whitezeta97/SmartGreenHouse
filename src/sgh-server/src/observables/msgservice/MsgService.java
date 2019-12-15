package observables.msgservice;

/**
 * 
 * Represents a message service to send data to serial port.
 *
 */
public interface MsgService {
	/**
	 * Initializes the message service.
	 */
	void init();

	/**
	 * Sends a message.
	 * 
	 * @param msg
	 *            the message that has to be sent.
	 */
	void sendMsg(final String msg);
}
