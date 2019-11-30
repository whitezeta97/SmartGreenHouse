package observables;

import java.util.LinkedList;

import events.Event;
import observer.event.EventObserver;

public abstract class AbstractObservable implements Observable {

	private LinkedList<EventObserver> observers;

	protected AbstractObservable() {
		this.observers = new LinkedList<EventObserver>();
	}

	public void addObserver(final EventObserver obs) {
		synchronized (this.observers) {
			this.observers.add(obs);
		}
	}

	public void removeObserver(final EventObserver obs) {
		synchronized (this.observers) {
			this.observers.remove(obs);
		}
	}

	protected void notifyEvent(final Event ev) {
		synchronized (this.observers) {
			for (EventObserver obs : this.observers) {
				obs.notifyEvent(ev);
			}
		}
	}

}
