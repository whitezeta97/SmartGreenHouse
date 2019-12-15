package events;

/**
 * 
 * Implements a message event received from SGH Edge.
 *
 */
public class EdgeMsgEventImpl implements EdgeMsgEvent {

	private float msg;

	/**
	 * 
	 * @param value
	 *            the value content in the message.
	 */
	public EdgeMsgEventImpl(final float value) {
		this.msg = value;
	}

	@Override
	public float getMsg() {
		return this.msg;
	}
}
