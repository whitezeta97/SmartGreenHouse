package serialcomm;

/**
 * Simple interface for an async msg communication channel
 */
public interface CommChannel {

	/**
	 * Send a message represented by a string (without new line).
	 * 
	 * Asynchronous model.
	 * 
	 * @param msg
	 *            the message to be sent.
	 */
	void sendMsg(final String msg);

	/**
	 * To receive a message.
	 * 
	 * Blocking behaviour.
	 */
	String receiveMsg() throws InterruptedException;

	/**
	 * To check if a message is available.
	 * 
	 * @return true if a message is available, false otherwise.
	 */
	boolean isMsgAvailable();

}
