package events;

/**
 * 
 * Implements a timer tick event.
 *
 */
public class TickEventImpl implements TickEvent {

	private long time;

	/**
	 * 
	 * @param time
	 *            the time in which the tick occurred.
	 */
	public TickEventImpl(final long time) {
		this.time = time;
	}

	@Override
	public long getTime() {
		return this.time;
	}
}
