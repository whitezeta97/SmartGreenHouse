package events;

public class TickEventImpl implements TickEvent {

	private long time;

	public TickEventImpl(final long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}
}
