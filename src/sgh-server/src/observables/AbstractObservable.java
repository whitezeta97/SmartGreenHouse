package observables;

import java.util.LinkedList;

import events.Event;
import observer.event.EventObserver;

/**
 * 
 * Implements an abstract observable.
 *
 */
public abstract class AbstractObservable implements Observable {

	private LinkedList<EventObserver> observers;

	protected AbstractObservable() {
		this.observers = new LinkedList<EventObserver>();
	}

	/**
	 * Notifies the event to every observer.
	 * 
	 * @param ev
	 *            the event that has to be notified.
	 */
	protected void notifyEvent(final Event ev) {
		synchronized (this.observers) {
			for (EventObserver obs : this.observers) {
				obs.notifyEvent(ev);
			}
		}
	}

	@Override
	public void addObserver(final EventObserver obs) {
		synchronized (this.observers) {
			this.observers.add(obs);
		}
	}

	@Override
	public void removeObserver(final EventObserver obs) {
		synchronized (this.observers) {
			this.observers.remove(obs);
		}
	}

}
