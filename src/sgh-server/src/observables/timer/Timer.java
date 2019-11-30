package observables.timer;

public interface Timer {

	/**
	 * Start generating tick event
	 * 
	 * @param period
	 *            period in milliseconds
	 */
	void start(final long period);
	
	/**
	 * Stop generating tick event
	 * 
	 * @param period
	 *            period in milliseconds
	 */
	void stop();
	
	/**
	 * Generate a tick event after a number of milliseconds
	 * 
	 * @param delta
	 */
	void scheduleTick(final long deltat);
}
