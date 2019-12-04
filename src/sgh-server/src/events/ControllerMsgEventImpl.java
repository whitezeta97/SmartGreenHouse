package events;

public class ControllerMsgEventImpl implements ControllerMsgEvent {

	private String msg;

	public ControllerMsgEventImpl(final String value) {
		this.msg = value;
	}

	public String getMsg() {
		return this.msg;
	}
}
