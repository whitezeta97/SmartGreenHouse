package events;

public class TickEventImpl implements TickEvent, Event {

	private long time;

	public TickEventImpl(final long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}
}
