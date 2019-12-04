package events;

public class EdgeMsgEventImpl implements EdgeMsgEvent {

	private float msg;

	public EdgeMsgEventImpl(final float value) {
		this.msg = value;
	}

	public float getMsg() {
		return this.msg;
	}
}
