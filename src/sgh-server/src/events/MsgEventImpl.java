package events;

public class MsgEventImpl implements MsgEvent {

	private String msg;

	public MsgEventImpl(final String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
