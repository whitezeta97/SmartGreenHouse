package events;

/**
 * 
 * Implements a message event received from SGH Edge.
 *
 */
public class EdgeMsgEventImpl implements EdgeMsgEvent {

	private int msgValue;

	/**
	 * 
	 * @param value
	 *            the value content in the message.
	 */
	public EdgeMsgEventImpl(final int value) {
		this.msgValue = value;
	}

	@Override
	public int getMsgValue() {
		return this.msgValue;
	}
}
