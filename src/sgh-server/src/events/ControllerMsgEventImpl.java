package events;

/**
 * 
 * Implements a message event received from SGH Controller.
 *
 */
public class ControllerMsgEventImpl implements ControllerMsgEvent {

	private String msg;

	/**
	 * 
	 * @param value
	 *            the content of the message.
	 */
	public ControllerMsgEventImpl(final String value) {
		this.msg = value;
	}

	@Override
	public String getMsg() {
		return this.msg;
	}
}
