package events;

/**
 * 
 * Represents a timer tick event.
 *
 */
public interface TickEvent extends Event {
	/**
	 * Gets the time when the tick has occurred.
	 * 
	 * @return the time when the tick has occurred.
	 */
	long getTime();

}
