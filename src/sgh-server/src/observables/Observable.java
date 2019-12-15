package observables;

import observer.event.EventObserver;

/**
 * 
 * Represents an observable behavior.
 *
 */
public interface Observable {

	/**
	 * Add an observer to this observable.
	 * 
	 * @param obs
	 *            the observer that has to be added.
	 */
	void addObserver(final EventObserver obs);

	/**
	 * Removes an observer from this observable.
	 * 
	 * @param obs
	 *            the observer that has to be removed.
	 */
	void removeObserver(final EventObserver obs);
}
